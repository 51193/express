package com.cc.express.dao;

import com.cc.express.entity.GraphEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GraphDao {
    List<GraphEntity> getAll();

    GraphEntity getGraph(Integer id);

    boolean updateGraph(GraphEntity graph);
}
