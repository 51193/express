package com.cc.express.controller;

import com.cc.express.algorithm.Node;
import com.cc.express.entity.request.EmergencyRequest;
import com.cc.express.entity.request.SupplyDemand;
import com.cc.express.service.AlgorithmService;
import com.cc.express.service.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class AlgorithmController {
    @Autowired
    AlgorithmService algorithmService;

    @Autowired
    GraphService graphService;

    @GetMapping("/algorithm/generatePlan")
    public Map<String, Object> generatePlan() {
        Map<String, Object> map = new HashMap<>();

        var graphs = graphService.getAll();
        var transportPlan = algorithmService.generateTransportPlan(graphs, false);

        map.put("state", true);
        map.put("msg", "请求成功");
        map.put("transportPlan", transportPlan);
        map.put("graph", graphs);
        return map;
    }

    @GetMapping("/algorithm/generateBlackHolePlan")
    public Map<String, Object> generateBlackHolePlan() {
        Map<String, Object> map = new HashMap<>();

        var graphs = graphService.getAll();
        var transportPlan = algorithmService.generateTransportPlan(graphs, true);

        map.put("state", true);
        map.put("msg", "请求成功");
        map.put("transportPlan", transportPlan);
        map.put("graph", graphs);
        return map;
    }

    @PostMapping("/algorithm/generateEmergencyPlan")
    public Map<String, Object> generateEmergencyPlan(@RequestBody EmergencyRequest request) {
        Map<String, Object> map = new HashMap<>();

        var node = new Node(request.getId(), request.getQuantity());

        var graphs = graphService.getAll();
        var emergencyPlan = algorithmService.generateEmergencyPlan(graphs, node, request.getItemName());

        map.put("state", true);
        map.put("msg", "请求成功");
        map.put("transportPlan", emergencyPlan);
        map.put("graph", graphs);
        return map;
    }

    @PostMapping("/algorithm/generatePathPlan")
    public Map<String, Object> generatePathPlan(@RequestBody List<SupplyDemand> supplyDemands) {
        Map<String, Object> map = new HashMap<>();

        var graph = graphService.getAll();

        map.put("state", true);
        map.put("msg", "请求成功");
        map.put("result", algorithmService.generatePathPlan(graph, supplyDemands));
        map.put("graph", graph);

        return map;
    }

    @GetMapping("/algorithm/getEdges")
    public Map<String, Object> getEdges() {
        Map<String, Object> map = new HashMap<>();

        map.put("state", true);
        map.put("msg", "请求成功");
        map.put("edges", algorithmService.getEdges(graphService.getAll()));

        return map;
    }
}
