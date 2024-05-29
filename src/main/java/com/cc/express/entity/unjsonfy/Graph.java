package com.cc.express.entity.unjsonfy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Graph {
    Integer id;
    String name;
    List<EdgeCost> edgeCostList;
    List<Goods> goodsList;
    Boolean isDelete;
}
