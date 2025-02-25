// src/main/java/com/food/service/MenuService.java
package com.food.service;

import com.food.dto.MenuDTO;
import com.food.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    //모든 매뉴 가져오기
    public List<MenuDTO> getMenuList() {
        return menuMapper.getMenuList();
    }

    public List<MenuDTO> getGongsikdang_AMenu() { return menuMapper.getGongsikdang_AMenu(); }

    public List<MenuDTO> getGongsikdang_BMenu() { return menuMapper.getGongsikdang_BMenu(); }

    public List<MenuDTO> getGongsikdang_CMenu() { return menuMapper.getGongsikdang_CMenu(); }

    public List<MenuDTO> getGongsikdang_DMenu() { return menuMapper.getGongsikdang_DMenu(); }

    public boolean reduceMenuQuantity(int foodId, int quantity) {
        MenuDTO menu = menuMapper.getMenuByFoodId(foodId); // DB에서 직접 매뉴 조회

        if (menu != null && menu.getNumber() >= quantity) {
            menuMapper.reduceMenuQuantity(foodId, quantity); // 재고 감소 쿼리 실행
            return true;
        }
        return false; // 재고가 부족하면 false 반환
    }
}