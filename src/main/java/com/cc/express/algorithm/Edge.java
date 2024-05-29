package com.cc.express.algorithm;

public class Edge {
    public int id;
    public int u;
    public int v;
    public long flow; // 流量
    public long cost; // 费用

    public Edge(Edge edge) {
        this.id = edge.id;
        this.u = edge.u;
        this.v = edge.v;
        this.flow = edge.flow;
        this.cost = edge.cost;
    }

    public Edge(int id, int u, int v, long flow, long cost) {
        this.id = id;
        this.u = u;
        this.v = v;
        this.flow = flow;
        this.cost = cost;
    }
}
