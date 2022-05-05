package com.jing;

import com.jing.mapper.DiscussPostMapper;
import com.jing.mapper.LoginTicketMapper;
import com.jing.mapper.UserMapper;
import com.jing.pojo.DiscussPost;
import com.jing.pojo.LoginTicket;
import com.jing.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    void userSelectTest() {
        User user = userMapper.selectById(146);
        System.out.println(user);
        System.out.println(userMapper.selectByName("lihonghe"));
        System.out.println(userMapper.selectByEmail("nowcoder146@sina.com"));
    }

    @Autowired
    DiscussPostMapper mapper;
    @Test
    void postSelectTest(){
        int i = mapper.selectDiscussPostRows(0);
        System.out.println(i);

    }

    @Test
    void ticketTests(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("sdads");
        System.out.println(loginTicket);
    }
}
