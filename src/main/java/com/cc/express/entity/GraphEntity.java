package com.cc.express.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GraphEntity {
    Integer id;
    String name;
    String to;
    String timecost;
    String expressfee;
    String capacity;
    String goods;
    String goodsamount;
    String goodsthreshold;
    Boolean isdelete;
}
