package com.thewildchild.management.order.service.impl;

import com.thewildchild.management.common.exception.BusinessException;
import com.thewildchild.management.common.exception.ResourceNotFoundException;
import com.thewildchild.management.order.dto.response.KotItemResponse;
import com.thewildchild.management.order.dto.response.KotResponse;
import com.thewildchild.management.order.dto.response.OrderItemAddOnResponse;
import com.thewildchild.management.order.entity.Order;
import com.thewildchild.management.order.entity.OrderItem;
import com.thewildchild.management.order.repository.OrderRepository;
import com.thewildchild.management.order.service.KotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class KotServiceImpl implements KotService {

    private final OrderRepository orderRepository;

    @Override
    public KotResponse generateKot(UUID orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        List<KotItemResponse> kotItems = new ArrayList<>();

        for (OrderItem orderItem : order.getItems()) {

            int pendingQuantity =
                    orderItem.getQuantity()
                            - orderItem.getKotSentQuantity();

            if (pendingQuantity <= 0) {
                continue;
            }

            KotItemResponse kotItem = new KotItemResponse();

            kotItem.setOrderItemId(orderItem.getId());

            kotItem.setMenuItemId(
                    orderItem.getMenuItem().getId()
            );

            kotItem.setMenuItemName(
                    orderItem.getMenuItem().getName()
            );

            kotItem.setQuantity(pendingQuantity);

            kotItem.setSpecialInstructions(
                    orderItem.getSpecialInstructions()
            );

            List<OrderItemAddOnResponse> addOns =
                    orderItem.getAddOns()
                            .stream()
                            .map(addOn -> {

                                OrderItemAddOnResponse response =
                                        new OrderItemAddOnResponse();

                                response.setId(addOn.getId());

                                response.setAddOnId(
                                        addOn.getAddOn().getId()
                                );

                                response.setAddOnName(
                                        addOn.getAddOn().getName()
                                );

                                response.setUnitPrice(
                                        addOn.getUnitPrice()
                                );

                                return response;
                            })
                            .toList();

            kotItem.setAddOns(addOns);

            kotItems.add(kotItem);

            orderItem.setKotSentQuantity(
                    orderItem.getQuantity()
            );
        }

        if (kotItems.isEmpty()) {
            throw new BusinessException(
                    "No new items available for KOT"
            );
        }

        orderRepository.save(order);

        KotResponse response = new KotResponse();

        response.setOrderId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setItems(kotItems);

        return response;
    }
}