package com.cc.express.dao;

import com.cc.express.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao {
    UserEntity login(UserEntity user);
    void registration(UserEntity user);

    UserEntity selectById(Integer id);
}
