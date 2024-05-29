package com.cc.express.entity.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmergencyRequest {
    Integer id;
    Integer quantity;
    String itemName;
}
