package com.cc.express.algorithm;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class Solver {

    /**
     * 在图上，有一些点提供物品，有一些点需要消费这些物品。求最小的费用，并描述如何运输。
     *
     * @param edges 图的边集合，id必须为正整数。如果没有流量约束，则flow需设置极大值，如0x3f3f3f3fL。
     * @param providers 提供物品的节点集合。size为能提供物品的数量。
     * @param consumers 消费物品的节点集合。size为需消费物品的数量。
     * @return cost为总费用。edges为有流量经过的边，并且flow字段为该边需要流过的流量。
     */
    public SolveRes solve(List<Edge> edges, List<Node> providers, List<Node> consumers) {
        List<Edge> newEdges = new ArrayList<>();
        List<Edge> resEdges = new ArrayList<>();
        for (Edge edge : edges) {
            newEdges.add(new Edge(edge));
            resEdges.add(new Edge(edge));
        }

        int maxEdgeId = 0;
        int maxNodeId = 0;
        for (Edge edge : edges) {
            maxEdgeId = max(maxEdgeId, edge.id);
            maxNodeId = max(maxNodeId, edge.u);
            maxNodeId = max(maxNodeId, edge.v);
        }
        for (Node provider : providers) {
            maxNodeId = max(maxNodeId, provider.id);
        }

        for (Node consumer : consumers) {
            maxNodeId = max(maxNodeId, consumer.id);
        }

        maxEdgeId++;
        maxNodeId++;

        int s = maxNodeId++;
        int t = maxNodeId++;

        for (Node provider : providers) {
            newEdges.add(new Edge(maxEdgeId++, s, provider.id, provider.size, 0));
        }

        for (Node consumer : consumers) {
            newEdges.add(new Edge(maxEdgeId++, consumer.id, t, consumer.size, 0));
        }

        var minimumCostFlow = new MinimumCostFlow();
        var minEdges = minimumCostFlow.EdmondsKarp(newEdges, s, t);
        for (int i = 0; i < resEdges.size(); i++) {
            resEdges.get(i).flow = resEdges.get(i).flow - minEdges.get(i).flow;
        }

        var res = new SolveRes();

        res.edges = resEdges;
        res.cost = 0L;
        for (Edge edge : resEdges) {
            res.cost += edge.flow * edge.cost;
        }
        return res;
    }
}
