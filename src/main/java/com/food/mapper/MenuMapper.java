// src/main/java/com/food/mapper/MenuMapper.java
package com.food.mapper;

import com.food.dto.MenuDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    //모든 매뉴 가져오는 함수
    List<MenuDTO> getMenuList();

    // 공식당_a 메뉴 가져오기
    List<MenuDTO> getGongsikdang_AMenu();

    //공식당_b 메뉴 가져오기
    List<MenuDTO> getGongsikdang_BMenu();

    //공식당_c 메뉴 가져오기
    List<MenuDTO> getGongsikdang_CMenu();

    //공식당_d 메뉴 가져오기
    List<MenuDTO> getGongsikdang_DMenu();

    MenuDTO getMenuByFoodId(@Param("foodId") int foodId); // 매뉴 이름으로 조회

    void reduceMenuQuantity(@Param("foodId") int foodId, @Param("quantity") int quantity); // 재고 감소




}
