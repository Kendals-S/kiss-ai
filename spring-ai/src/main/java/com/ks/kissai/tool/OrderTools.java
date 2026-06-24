package com.ks.kissai.tool;

import com.ks.kissai.pojo.Order;
import com.ks.kissai.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTools {

    private final OrderService orderService;


    @Tool(name = "get_order_list", description = "查询当前用户所有订单")
    public List<Order> getOrderList() {
        log.info("正在查询当前用户所有订单");

        return orderService.getOrders();
    }

    @Tool(name = "apply_refund", description = "根据订单Id发起退款操作")
    public String refundOrder(@ToolParam(description = "订单ID") Long orderId,
                              @ToolParam(description = "退款原因") String reason) {
        log.info("正在申请订单退款，订单Id：{}，申请原因：{}", orderId, reason);

        String refundOrderId = orderService.refundOrder(orderId, reason);

        return String.format("订单退款成功，订单号：%s", refundOrderId);
    }
}
