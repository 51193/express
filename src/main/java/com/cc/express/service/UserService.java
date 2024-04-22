package com.cc.express.service;

import com.cc.express.dao.UserDao;
import com.cc.express.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public UserEntity login(UserEntity user) {
        UserEntity userEntity = userDao.login(user);
        if (userEntity != null) {
            return userEntity;
        } else {
            throw new RuntimeException("登录失败");
        }
    }

    public void registration(UserEntity user) {
        userDao.registration(user);
    }

    public UserEntity selectById(Integer id) {
        UserEntity userEntity = userDao.selectById(id);
        if (userEntity != null) {
            userEntity.setPassword(null);
            return userEntity;
        } else {
            throw new RuntimeException("错误id");
        }
    }
}
