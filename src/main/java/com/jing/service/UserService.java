package com.jing.service;

import com.jing.mapper.LoginTicketMapper;
import com.jing.mapper.UserMapper;
import com.jing.pojo.LoginTicket;
import com.jing.pojo.Page;
import com.jing.pojo.User;
import com.jing.utils.CommunityConstant;
import com.jing.utils.CommunityUtils;
import com.jing.utils.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Value("${community.path.domain}")
    private String domian;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该用户名已存在！");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已被注册！");
            return map;
        }
        user.setSalt(CommunityUtils.generateUUID().substring(0,5));
        //密码加盐Md5
        user.setPassword(CommunityUtils.md5(user.getPassword()+user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(CommunityUtils.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //数据库
        userMapper.insertUser(user);
        //激活邮箱
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domian+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.setMail(user.getEmail(),"验证邮箱",content);
        return map;
    }

    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1 && code.equals(user.getActivationCode())){
            return ACTIVATION_REPEAT;
        }else if(code.equals(user.getActivationCode())){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else
            return ACTIVATION_FAILURE;
    }

    public Map<String,Object> login(String username,String password,long expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","账号不存在！");
            return map;
        }
        //验证状态
        if(user.getStatus() == 0){
            map.put("usernameMsg","该用户未激活！");
            return map;
        }
        //验证密码
        password = CommunityUtils.md5(password+user.getSalt());
        if(!password.equals(user.getPassword())){
            map.put("usernameMsg","账号或密码错误！");
            return map;
        }
        //登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtils.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }
    public LoginTicket findLoginTickeByTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }
    public int updateHeader(int userId, String headUrl){
        return userMapper.updateHeader(userId, headUrl);
    }

    public int updatePassword(String salt,int userId,String newPassword){
        return userMapper.updatePassword(userId, CommunityUtils.md5(newPassword+salt));
    }
}
