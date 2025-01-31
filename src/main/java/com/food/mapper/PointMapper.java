package com.food.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointMapper {

    int getUserPoint(String userId);

    //포인트 저장 함수
    void updatePoint(String userId, int point);
}
