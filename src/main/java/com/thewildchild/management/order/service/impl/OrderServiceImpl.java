package com.thewildchild.management.order.service.impl;

import com.thewildchild.management.diningsession.entity.DiningSession;
import com.thewildchild.management.diningsession.repository.DiningSessionRepository;
import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.menu.entity.AddOn;
import com.thewildchild.management.menu.entity.MenuItem;
import com.thewildchild.management.menu.repository.AddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemAddOnRepository;
import com.thewildchild.management.menu.repository.MenuItemRepository;
import com.thewildchild.management.order.dto.request.AddOrderItemAddOnRequest;
import com.thewildchild.management.order.dto.request.AddOrderItemRequest;
import com.thewildchild.management.order.dto.response.OrderResponse;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderBillingStatus;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.order.entity.OrderItemAddOn;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.order.service.OrderService;
import com.thewildchild.management.order.service.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DiningSessionRepository diningSessionRepository;
    private final MenuItemRepository menuItemRepository;
    private final AddOnRepository addOnRepository;
    private final OrderMapper orderMapper;
    private final MenuItemAddOnRepository menuItemAddOnRepository;


    @Override
    public OrderResponse createOrder(UUID diningSessionId) {

        DiningSession diningSession = diningSessionRepository
                .findById(diningSessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Dining session not found with id: "
                                        + diningSessionId
                        )
                );

        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());
        order.setDiningSession(diningSession);
        order.setBillingStatus(OrderBillingStatus.UNBILLED);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse addOrderItem(
            UUID orderId,
            AddOrderItemRequest request
    ) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        validateRequest(request);

        MenuItem menuItem = menuItemRepository
                .findById(request.getMenuItemId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item not found with id: "
                                        + request.getMenuItemId()
                        )
                );

        if (!menuItem.isActive()) {
            throw new BusinessException(
                    "Menu item is not active"
            );
        }

        List<AddOn> addOns = getAndValidateAddOns(request);

        OrderItem existingOrderItem =
                findMatchingOrderItem(order, menuItem, addOns);

        if (existingOrderItem != null) {

            existingOrderItem.setQuantity(request.getQuantity());

        } else {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(request.getQuantity());
            orderItem.setKotSentQuantity(0);
            orderItem.setUnitPrice(menuItem.getPrice());
            orderItem.setSpecialInstructions(
                    request.getSpecialInstructions()
            );

            for (AddOn addOn : addOns) {

                OrderItemAddOn orderItemAddOn =
                        new OrderItemAddOn();

                orderItemAddOn.setOrderItem(orderItem);
                orderItemAddOn.setAddOn(addOn);
                orderItemAddOn.setUnitPrice(addOn.getPrice());

                orderItem.getAddOns().add(orderItemAddOn);
            }

            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    private void validateRequest(
            AddOrderItemRequest request
    ) {

        if (request.getQuantity() == null
                || request.getQuantity() < 1) {

            throw new BusinessException(
                    "Quantity must be at least 1"
            );
        }

        if (request.getAddOns() == null) {
            return;
        }

        // Specific checks for addOns
        Set<UUID> addOnIds = new HashSet<>();

        for (AddOrderItemAddOnRequest addOnRequest
                : request.getAddOns()) {

            if (!addOnIds.add(addOnRequest.getAddOnId())) {

                throw new BusinessException(
                        "Duplicate add-on is not allowed: "
                                + addOnRequest.getAddOnId()
                );
            }
        }
    }

    private List<AddOn> getAndValidateAddOns(
            AddOrderItemRequest request
    ) {

        if (request.getAddOns() == null
                || request.getAddOns().isEmpty()) {

            return List.of();
        }

        List<UUID> addOnIds = request.getAddOns()
                .stream()
                .map(AddOrderItemAddOnRequest::getAddOnId)
                .toList();

        List<AddOn> addOns =
                addOnRepository.findAllById(addOnIds);

        if (addOns.size() != addOnIds.size()) {

            throw new ResourceNotFoundException(
                    "One or more add-ons were not found"
            );
        }

        for (AddOn addOn : addOns) {

            if (!addOn.isActive()) {

                throw new BusinessException(
                        "Add-on is not active: "
                                + addOn.getName()
                );
            }
            boolean allowed =
                    menuItemAddOnRepository
                            .existsByMenuItemIdAndAddOnId(
                                    request.getMenuItemId(),
                                    addOn.getId()
                            );

            if (!allowed) {
                throw new BusinessException(
                        "Add-on '" + addOn.getName()
                                + "' is not available for menu item"
                );
            }
        }

        return addOns;
    }

    private OrderItem findMatchingOrderItem(
            Order order,
            MenuItem menuItem,
            List<AddOn> requestedAddOns
    ) {

        for (OrderItem item : order.getItems()) {

            if (!item.getMenuItem()
                    .getId()
                    .equals(menuItem.getId())) {
                continue;
            }

            if (item.getAddOns().size()
                    != requestedAddOns.size()) {
                continue;
            }

            Set<UUID> existingAddOnIds =
                    item.getAddOns()
                            .stream()
                            .map(addOn ->
                                    addOn.getAddOn().getId()
                            )
                            .collect(
                                    java.util.stream.Collectors
                                            .toSet()
                            );

            Set<UUID> requestedAddOnIds =
                    requestedAddOns
                            .stream()
                            .map(AddOn::getId)
                            .collect(
                                    java.util.stream.Collectors
                                            .toSet()
                            );

            if (existingAddOnIds.equals(
                    requestedAddOnIds
            )) {

                return item;
            }
        }

        return null;
    }

    private String generateOrderNumber() {

        return "ORD-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}