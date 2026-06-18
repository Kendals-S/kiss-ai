package com.ks.kissai.pojo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.ks.kissai.converter.MapKeyField;

public record Airplane(@MapKeyField @JsonPropertyDescription("飞机名称") String name,
                       @JsonPropertyDescription("飞机型号") String model,
                       @JsonPropertyDescription("国家") String country,
                       @JsonPropertyDescription("最大速度，单位 Km/h") int maxSpeed,
                       @JsonPropertyDescription("续航距离，单位 Km") int endurance,
                       @JsonPropertyDescription("造价，单位 人民币千万元") double cost,
                       @JsonPropertyDescription("重量，单位 t") double weight,
                       @JsonPropertyDescription("简介") String desc) {
}
