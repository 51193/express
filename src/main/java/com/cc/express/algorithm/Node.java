package com.cc.express.algorithm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    int id;
    Long size;

    public Node(int id, int size) {
        this.id = id;
        this.size = (long) size;
    }
}
