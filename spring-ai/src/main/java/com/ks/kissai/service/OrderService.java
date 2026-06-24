package com.ks.kissai.service;

import com.ks.kissai.pojo.Order;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
public class OrderService {

    private final List<Order> orders;

    public OrderService() {
        orders = List.of(
                new Order(192382761873L, "牛仔裤", new BigDecimal("99.99"), 2,
                        "已完成", LocalDateTime.parse("2023-06-30T12:16:09"), LocalDateTime.parse("2023-06-10T12:17:23")),
                new Order(192382761874L, "T恤", new BigDecimal("19.99"), 1,
                        "交易取消", LocalDateTime.parse("2024-02-12T19:11:36"), LocalDateTime.parse("2024-02-12T19:17:23")),
                new Order(192382761875L, "运动鞋", new BigDecimal("89.99"), 1,
                        "待收货", LocalDateTime.parse("2026-06-22T21:35:17"), LocalDateTime.parse("2026-06-22T22:30:23"))
        );
    }

    public String refundOrder(Long orderId, String reason) {
        Map<Long, Order> orderMap = orders.stream()
                .collect(Collectors.toMap(Order::orderId, Function.identity()));
        Order order = orderMap.get(orderId);
        if (order == null) {
            return "订单不存在";
        }
        UUID uuid = UUID.randomUUID();
        log.info("订单退款成功，退款单号：{}，退款原因：{}", uuid, reason);
        return uuid.toString();
    }
}
