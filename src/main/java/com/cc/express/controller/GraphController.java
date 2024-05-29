package com.cc.express.controller;

import com.cc.express.entity.request.ModifyGoodsConfigRequest;
import com.cc.express.service.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class GraphController {

    @Autowired
    GraphService graphService;

    @GetMapping("/graph/all")
    public Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<>();

        map.put("state", true);
        map.put("msg", "请求成功");
        map.put("graph", graphService.getAll());

        return map;
    }

    @PostMapping("/graph/change")
    public Map<String, Object> changeGoods(@RequestBody ModifyGoodsConfigRequest request) {
        Map<String, Object> map = new HashMap<>();

        if (request.threshold == null && graphService.modifyGoodsConfig(request.id, request.itemName, request.quantity)) {
            map.put("state", true);
            map.put("msg", "请求成功");
        } else if (graphService.modifyGoodsConfig(request.id, request.itemName, request.quantity, request.threshold)) {
            map.put("state", true);
            map.put("msg", "请求成功");
        } else {
            map.put("state", false);
            map.put("msg", "请求失败");
        }

        return map;
    }
}
