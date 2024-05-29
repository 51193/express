package com.cc.express.service;

import com.cc.express.algorithm.Edge;
import com.cc.express.algorithm.Node;
import com.cc.express.algorithm.Solver;
import com.cc.express.entity.request.AlgorithmReturn;
import com.cc.express.entity.request.SupplyDemand;
import com.cc.express.entity.unjsonfy.Graph;
import com.cc.express.entity.util.TransportPlan;
import javafx.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlgorithmService {

    public List<Edge> getEdges(List<Graph> graph) {
        var edges = convert(graph);
        for (var e : edges) {
            e.flow = 0;
        }
        return edges;
    }

    public List<AlgorithmReturn> generatePathPlan(List<Graph> graphList, List<SupplyDemand> supplyDemandList) {
        var result = new ArrayList<AlgorithmReturn>();
        var solver = new Solver();

        var graphMap = new HashMap<Integer, Graph>();

        for (var g : graphList) {
            graphMap.put(g.getId(), g);
        }

        var edges = convert(graphList);

        for (var e : edges) {
            e.flow = 0x3f3f3f3f;
        }

        for (var s : supplyDemandList) {
            var provider = new ArrayList<Node>();
            var consumer = new ArrayList<Node>();

            if (s.startPlaces.isEmpty()) {
                for (var g : graphList) {
                    s.startPlaces.add(g.getId());
                }
            }

            for (var a : s.endPlaces) {
                for (int i = 0; i < s.startPlaces.size(); i++) {
                    if (Objects.equals(s.startPlaces.get(i), a)) {
                        s.startPlaces.remove(i);
                        break;
                    }
                }
            }

            for (var startPlace : s.startPlaces) {
                var goods = graphMap.get(startPlace).getGoodsList();
                int amount = 0;
                for (var g : goods) {
                    if (Objects.equals(g.getName(), s.itemName)) {
                        amount = g.getAmount();
                    }
                }
                var node = new Node(startPlace, amount);
                provider.add(node);
            }


            for (int i = 0; i < s.endPlaces.size(); i++) {
                var node = new Node(s.endPlaces.get(i), s.quantity.get(i));
                consumer.add(node);
            }

            var solveRes = solver.solve(edges, provider, consumer);

            var r = new AlgorithmReturn();
            r.cost = solveRes.cost;

//            var e = new ArrayList<Edge>();
//            for (var cur : solveRes.edges) {
//                if (cur.flow > 0.1) {
//                    e.add(cur);
//                }
//            }

            r.edges = solveRes.edges;
            r.name = s.itemName;
            result.add(r);
        }

        for (var r : result) {
            var goodsName = r.name;
            for (var e : r.edges) {
                var startGoodsList = graphMap.get(e.u).getGoodsList();
                for (var s : startGoodsList) {
                    if (Objects.equals(s.getName(), goodsName)) {
                        s.setAmount(s.getAmount() - (int) e.flow);
                        break;
                    }
                }

                var endGoodsList = graphMap.get(e.v).getGoodsList();
                for (var d : endGoodsList) {
                    if (Objects.equals(d.getName(), goodsName)) {
                        d.setAmount(d.getAmount() + (int) e.flow);
                        break;
                    }
                }
            }
        }

        return result;
    }

    public List<Edge> convert(List<Graph> graphList) {
        List<Edge> edgeList = new ArrayList<>();

        int index = 1;

        for (var g : graphList) {
            int from = g.getId();
            for (var e : g.getEdgeCostList()) {
                int to = e.getToId();
                int capacity = e.getCapacity();
                int fee = e.getFee();

                var temp = new Edge(index, from, to, capacity, fee);

                index += 1;
                edgeList.add(temp);
            }
        }

        return edgeList;
    }

    public List<Edge> convertTimeCost(List<Graph> graphList) {
        List<Edge> edgeList = new ArrayList<>();

        int index = 1;

        for (var g : graphList) {
            int from = g.getId();
            for (var e : g.getEdgeCostList()) {
                int to = e.getToId();
                int capacity = e.getCapacity();
                int fee = e.getTime();

                var temp = new Edge(index, from, to, capacity, fee);

                index += 1;
                edgeList.add(temp);
            }
        }

        return edgeList;
    }

    public Map<String, List<Node>> generateProvider(List<Graph> graphList) {
        var nodeMap = new HashMap<String, List<Node>>();

        for (var g : graphList) {
            Integer id = g.getId();
            for (var i : g.getGoodsList()) {
                String name = i.getName();
                Integer amount = i.getAmount();
                Integer threshold = i.getThreshold();

                if (threshold < amount) {
                    var n = new Node(id, amount - threshold);
                    nodeMap.computeIfAbsent(name, k -> new ArrayList<>());
                    nodeMap.get(name).add(n);
                }

            }
        }

        return nodeMap;
    }

    public Map<String, List<Node>> generateConsumer(List<Graph> graphList) {
        var nodeMap = new HashMap<String, List<Node>>();

        for (var g : graphList) {
            Integer id = g.getId();
            for (var i : g.getGoodsList()) {
                String name = i.getName();
                Integer amount = i.getAmount();
                Integer threshold = i.getThreshold();

                if (threshold > amount) {
                    var n = new Node(id, threshold - amount);
                    nodeMap.computeIfAbsent(name, k -> new ArrayList<>());
                    nodeMap.get(name).add(n);
                }

            }
        }

        return nodeMap;
    }

    public List<TransportPlan> generateTransportPlan(List<Graph> graphList, boolean isBlackHole) {
        var transportPlan = new ArrayList<TransportPlan>();
        var solver = new Solver();

        var graphMap = new HashMap<Integer, Graph>();
        var onTheWayGoods = new HashMap<Integer, HashMap<String, Integer>>();

        for (var i : graphList) {
            graphMap.put(i.getId(), i);
            var goods = new HashMap<String, Integer>();
            for (var g : i.getGoodsList()) {
                goods.put(g.getName(), 0);
            }
            onTheWayGoods.put(i.getId(), goods);
        }

        var onTheWayTransport = new ArrayList<TransportPlan>();
        var edges = convert(graphList);

        int timer = 0;
        int nothingTransportTime = 0;


        while (nothingTransportTime < 20) {
            var providers = generateProvider(graphList);
            var consumers = generateConsumer(graphList);

            boolean somethingChanged = false;

            for (var goodsName : providers.keySet()) {

                if (consumers.get(goodsName) != null && !consumers.get(goodsName).isEmpty()) {
                    var res = solver.solve(edges, providers.get(goodsName), consumers.get(goodsName));

                    for (var edgeFlow : res.edges) {

                        if (isBlackHole) {
                            var goods = graphMap.get(edgeFlow.u).getGoodsList();
                            for (var g : goods) {
                                if (Objects.equals(g.getName(), goodsName)) {
                                    if (g.getAmount() - onTheWayGoods.get(edgeFlow.u).get(goodsName) - g.getThreshold() < edgeFlow.flow) {
                                        edgeFlow.flow = g.getAmount() - onTheWayGoods.get(edgeFlow.u).get(goodsName) - g.getThreshold();
                                    }
                                    break;
                                }
                            }
                        }

                        if (edgeFlow.flow > 0.1) {
                            var startId = edgeFlow.u;
                            var endId = edgeFlow.v;
                            var timeConsume = 0;
                            for (var temp : graphMap.get(startId).getEdgeCostList()) {
                                if (temp.getToId() == endId) {
                                    timeConsume = temp.getTime();
                                    break;
                                }
                            }
                            var goodsAmount = (int) edgeFlow.flow;
                            var cost = (int) edgeFlow.cost;

                            var plan = new TransportPlan();

                            plan.setStartId(startId);
                            plan.setEndId(endId);
                            plan.setStartTime(timer);
                            plan.setEndTime(timer + timeConsume);
                            plan.setGoodsName(goodsName);
                            plan.setGoodsAmount(goodsAmount);
                            plan.setCost(cost * goodsAmount);

                            onTheWayTransport.add(plan);
                            somethingChanged = true;

                            for (var edge : edges) {
                                if (edge.u == startId && edge.v == endId) {
                                    edge.flow -= goodsAmount;
                                    break;
                                }
                            }

                            for (var cur : graphMap.get(startId).getGoodsList()) {
                                if (Objects.equals(cur.getName(), goodsName)) {
                                    var amount = cur.getAmount();
                                    cur.setAmount(amount - goodsAmount);
                                    break;
                                }
                            }

                            for (var cur : graphMap.get(endId).getGoodsList()) {
                                if (Objects.equals(cur.getName(), goodsName)) {
                                    var amount = cur.getAmount();
                                    cur.setAmount(amount + goodsAmount);
                                    break;
                                }
                            }

                            onTheWayGoods.get(endId).put(goodsName, onTheWayGoods.get(endId).get(goodsName) + goodsAmount);
                        }
                    }
                }
            }

            for (int i = 0; i < onTheWayTransport.size(); i++) {
                if (onTheWayTransport.get(i).getEndTime() <= timer) {
                    var plan = onTheWayTransport.get(i);

                    transportPlan.add(plan);
                    for (var edge : edges) {
                        if (edge.u == plan.getStartId() && edge.v == plan.getEndId()) {
                            edge.flow += plan.getGoodsAmount();
                            break;
                        }
                    }

                    onTheWayGoods.get(plan.getEndId()).put(plan.getGoodsName(), onTheWayGoods.get(plan.getEndId()).get(plan.getGoodsName()) - plan.getGoodsAmount());

                    onTheWayTransport.remove(i);
                    i--;
                    somethingChanged = true;
                }
            }

            timer += 1;
            if (!somethingChanged && onTheWayTransport.isEmpty()) {
                nothingTransportTime += 1;
            }

        }

        return transportPlan;
    }

    public List<TransportPlan> generateEmergencyPlan(List<Graph> graphList, Node needyNode, String goodsName) {
        var transportPlan = new ArrayList<TransportPlan>();

        var edges = convertTimeCost(graphList);
        var costMap = new HashMap<Pair<Integer, Integer>, Integer>();

        for (var e : edges) {
            var path = new Pair<>(e.u, e.v);
            costMap.put(path, (int) e.cost);
        }

        int size = edges.size();

        int multiplyTime = 10;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < multiplyTime; j++) {
                var id = edges.get(i).id + 10000 * (j + 1);
                var u = edges.get(i).u;
                var v = edges.get(i).v;
                var flow = edges.get(i).flow;
                var cost = edges.get(i).cost * (j + 2);
                var edge = new Edge(id, u, v, flow, cost);

                edges.add(edge);
            }
        }

        var provider = new ArrayList<Node>();

        for (var g : graphList) {
            for (var goods : g.getGoodsList()) {
                if (Objects.equals(goodsName, goods.getName())) {
                    var node = new Node(g.getId(), goods.getAmount());
                    provider.add(node);
                    break;
                }
            }
        }

        var consumer = new ArrayList<Node>();

        consumer.add(needyNode);

        var solver = new Solver();

        var result = solver.solve(edges, provider, consumer);

        for (var edge : result.edges) {
            if (edge.flow > 0.1) {
                var plan = new TransportPlan();
                plan.setStartId(edge.u);
                plan.setEndId(edge.v);
                plan.setGoodsName(goodsName);
                plan.setCost((costMap.get(new Pair<>(edge.u, edge.v))));
                plan.setGoodsAmount((int) edge.flow);
                if (edge.id / 10000 == 0) {
                    plan.setStartTime(0);
                } else {
                    plan.setStartTime((int) edge.cost - (int) edge.cost / (edge.id / 10000 + 1));
                }
                plan.setEndTime((int) edge.cost);
                transportPlan.add(plan);
            }
        }

        return transportPlan;
    }
}
