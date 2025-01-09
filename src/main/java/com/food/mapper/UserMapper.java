package com.food.mapper;

import com.food.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // 사용자 ID로 사용자 정보 조회
    UserDTO findByUsername(String id);

    // 사용자 등록
    void insertUser(UserDTO userDTO);
}

