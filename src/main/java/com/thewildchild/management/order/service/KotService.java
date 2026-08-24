package com.thewildchild.management.order.service;

import com.thewildchild.management.order.dto.response.KotResponse;

import java.util.UUID;

public interface KotService {

    KotResponse generateKot(UUID orderId);
}