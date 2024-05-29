package com.cc.express.algorithm;

import java.util.*;
import java.util.concurrent.Callable;

import static java.lang.Math.min;


class MinimumCostFlow {
    public List<Edge> EdmondsKarp(List<Edge> edges, Integer s, Integer t) {
        Map<Integer, Edge> edgesMap = new HashMap<>();
        Map<Integer, List<Edge>> adj = new HashMap<>();
        for (var edge : edges) {
            var forEdge = new Edge(edge);
            edgesMap.put(forEdge.id, forEdge);
            adj.computeIfAbsent(edge.u, k -> new ArrayList<>()).add(forEdge);

            var revEdge = new Edge(-edge.id, edge.v, edge.u, 0L, -edge.cost);
            edgesMap.put(revEdge.id, revEdge);
            adj.computeIfAbsent(edge.v, k -> new ArrayList<>()).add(revEdge);
        }

        Callable<Boolean> spfa = () -> {
            Queue<Integer> q = new LinkedList<>();

            Map<Integer, Long> dis = new HashMap<>();
            Set<Integer> vis = new HashSet<>();
            Map<Integer, Edge> preEdge = new HashMap<>();
            Map<Integer, Long> preFlow = new HashMap<>();

            q.add(s);
            vis.add(s);
            dis.put(s, 0L);
            preEdge.put(s, new Edge(s, s, s, 0L, 0L));
            preFlow.put(s, 0x3f3f3f3fL);

            while (!q.isEmpty()) {
                var u = q.poll();
                vis.remove(u);
                for (var edge : adj.get(u)) {
                    var v = edge.v;
                    var flow = edge.flow;
                    var cost = edge.cost;

                    if (flow == 0) {
                        continue;
                    }

                    if (dis.containsKey(v) && dis.get(u) + cost >= dis.get(v)) {
                        continue;
                    }

                    dis.put(v, dis.get(u) + cost);
                    preEdge.put(v, edge);
                    preFlow.put(v, min(flow, preFlow.get(u)));
                    if (vis.contains(v)) {
                        continue;
                    }
                    vis.add(v);
                    q.add(v);
                }
            }

            if (!dis.containsKey(t)) {
                return false;
            }

            var flow = preFlow.get(t);
//            var cost = dis.get(t);

            for (var curEdge = preEdge.get(t); curEdge.v != s; curEdge = preEdge.get(curEdge.u)) {
                curEdge.flow -= flow;
                edgesMap.get(-curEdge.id).flow += flow;
            }
            return true;
        };

        try {
            while (spfa.call()) {
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Edge> resEdges = new ArrayList<>();
        for (Edge edge : edges) {
            var forEdge = edgesMap.get(edge.id);
            resEdges.add(forEdge);
        }

        return resEdges;
    }
}