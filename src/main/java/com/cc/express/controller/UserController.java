package com.cc.express.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cc.express.entity.UserEntity;
import com.cc.express.service.UserService;
import com.cc.express.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/user/registration")
    public Map<String, Object> registration(@RequestBody UserEntity user) {
        Map<String, Object> map = new HashMap<>();
        try {
            userService.registration(user);
            map.put("state", true);
            map.put("msg", "注册成功");
        } catch (Exception e) {
            map.put("state", false);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @PostMapping("/user/login")
    public Map<String, Object> login(@RequestBody UserEntity user) {
        Map<String, Object> map = new HashMap<>();
        try {
            UserEntity userEntity = userService.login(user);
            Map<String, String> payload = new HashMap<>();
            payload.put("id", String.valueOf(userEntity.getId()));
            payload.put("name", userEntity.getName());
            payload.put("authority", userEntity.getAuthority());
            // 生成jwt令牌
            String token = JWTUtil.getToken(payload);
            map.put("state", true);
            map.put("msg", "认证成功！");
            map.put("token", token);  // 响应token
            map.put("id", userEntity.getId());
            map.put("name", userEntity.getName());
            map.put("authority", userEntity.getAuthority());
        } catch (Exception e) {
            map.put("state", false);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @GetMapping("/user/refresh")
    public Map<String, Object> refresh(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            map.put("state", false);
            map.put("msg", "无可用token");
        }
        try {
            // 验证令牌
            DecodedJWT jwt = JWTUtil.verify(token);

            Map<String, String> payload = new HashMap<>();
            payload.put("id", jwt.getClaim("id").asString());
            payload.put("name", jwt.getClaim("name").asString());
            payload.put("authority", jwt.getClaim("authority").asString());

            // 生成jwt令牌
            String new_token = JWTUtil.getToken(payload);

            map.put("state", true);
            map.put("msg", "已更新token");
            map.put("token", new_token);  // 响应token
            map.put("id", Integer.valueOf(jwt.getClaim("id").asString()));
            map.put("name", jwt.getClaim("name").asString());
            map.put("authority", jwt.getClaim("authority").asString());

        } catch (Exception e) {
            map.put("state", false);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @GetMapping("/user/check")
    public Map<String, Object> check(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            map.put("state", false);
            map.put("msg", "请登录");
        }
        try {
            // 验证令牌
            JWTUtil.verify(token);
            map.put("state", true);
            map.put("msg", "已保存登录状态");
        } catch (Exception e) {
            map.put("state", false);
            map.put("msg", "请登录");
        }
        return map;
    }

    @GetMapping("user/get/{id}")
    public UserEntity test(@PathVariable Integer id){
        return userService.selectById(id);
    }

    @GetMapping("/user/test")
    public Map<String, Object> test(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        // 验证令牌  交给拦截器去做
        // 只需要在这里处理自己的业务逻辑
        String token = request.getHeader("token");
        JWTUtil.verify(token);
        map.put("state", true);
        map.put("msg", "请求成功");
        return map;
    }
}
