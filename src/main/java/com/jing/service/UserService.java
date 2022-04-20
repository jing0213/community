package com.jing.service;

import com.jing.mapper.UserMapper;
import com.jing.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
