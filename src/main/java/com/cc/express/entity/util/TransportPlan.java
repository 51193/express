package com.cc.express.entity.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransportPlan {
    Integer startTime;
    Integer endTime;
    Integer startId;
    Integer endId;
    String goodsName;
    Integer goodsAmount;
    Integer cost;
}
