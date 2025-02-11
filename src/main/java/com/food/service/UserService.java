// backend/src/main/java/com/food/service/UserService.java
package com.food.service;

import com.food.dto.UserDTO;
import com.food.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticateUser(UserDTO userDTO) {
        UserDTO userFromDB = userMapper.findByUsername(userDTO.getId());

        if (userFromDB == null) {
            return false;
        }

        // 비밀번호 검증 (암호화된 비밀번호 비교)
        return passwordEncoder.matches(userDTO.getPassword(), userFromDB.getPassword());
    }

    public boolean registerUser(UserDTO userDTO) {
        if (userMapper.findByUsername(userDTO.getId()) != null) {
            return false; // 중복된 사용자
        }

        // 비밀번호 암호화 후 저장
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encryptedPassword);
        System.out.println("🔥 회원 등록시도");
        userMapper.insertUser(userDTO);
        return true;
    }

    public boolean isIdDuplicated(String id) {
        return userMapper.findByUsername(id) != null;
    }


}

