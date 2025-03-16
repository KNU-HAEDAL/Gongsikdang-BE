package com.food.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PointMapper {

    int getUserPoint(String userId);

    void updatePoint(@Param("userId") String userId, @Param("point") int point); // 포인트 충전

    void deductPoint(@Param("userId") String userId, @Param("point") int point); // 포인트 차감
}
