package com.ks.kissai.pojo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(@JsonPropertyDescription("订单ID")
                    Long orderId,
                    @JsonPropertyDescription("商品名称")
                    String productName,
                    @JsonPropertyDescription("商品价格")
                    BigDecimal price,
                    @JsonPropertyDescription("商品数量")
                    Integer quantity,
                    @JsonPropertyDescription("订单状态")
                    String status,
                    @JsonPropertyDescription("创建时间")
                    LocalDateTime createTime,
                    @JsonPropertyDescription("支付时间")
                    LocalDateTime payTime) {
}
