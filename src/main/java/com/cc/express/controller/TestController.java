package com.cc.express.controller;

import com.cc.express.entity.TestEntity;
import com.cc.express.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping(value = "/test/get/{id}")
    public TestEntity test(@PathVariable Integer id) {
        System.out.println("id:" + id);
        return testService.getById(id);
    }

    @PostMapping(value = "/test")
    public Map<String, Object> reflect(@RequestBody Map<String, Object> params) {
        System.out.println(params.toString());
        return params;
    }

    @PostMapping(value = "/test/array")
    public List<Map<String, Object>> reflectArray(@RequestBody List<Map<String, Object>> params) {
        for (var param : params) {
            System.out.println(param.toString());
        }
        return params;
    }
}