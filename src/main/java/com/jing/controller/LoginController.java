package com.jing.controller;

import com.google.code.kaptcha.Producer;
import com.jing.pojo.User;
import com.jing.service.UserService;
import com.jing.utils.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
@Slf4j
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    @Value("server.servlet.context-path")
    String contextPath;
    @GetMapping("/register")
    public String toRegisterPage(){
        return "site/register";
    }
    @GetMapping("/login")
    public String toLoginPage(){
        return "site/login";
    }
    @PostMapping("/register")
    public String register(Model model, @Valid User user){
        Map<String,Object> map = null;
        try {
            map = userService.register(user);
        }catch (Exception e){

        }
        if(map == null){
            model.addAttribute("emailMsg","邮箱格式错误！！！");
            return "site/register";
        }
        //注册成功
        if(map.isEmpty()){
            model.addAttribute("msg","注册成功，已经发了邮件，快去激活下吧！");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "site/register";
        }
    }
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId")int userId, @PathVariable("code")String code){
        int res = userService.activation(userId, code);
        if(res == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，快去登陆吧！");
            model.addAttribute("target","/login");
            return "site/operate-result";
        }else if(res == ACTIVATION_FAILURE){
            model.addAttribute("msg","激活失败，你的激活码失效了！");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }else {
            model.addAttribute("msg","你已经激活过了，快去登录吧！");
            model.addAttribute("target","/login");
            return "site/operate-result";
        }
    }
    @GetMapping("/kaptcha")
    public void kaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //存入session来验证
        session.setAttribute("kaptcha",text);

        //返回客户端
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            log.error("【获取验证码】失败："+e.getMessage());
        }

    }

    @PostMapping("/login")
    public String login(Model model,String username,String password,String code,boolean remember,
                        HttpSession session,HttpServletResponse response){
        String kaptcha = (String) session.getAttribute("kaptcha");
        //判断验证码
        if(!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "site/login";
        }
        //验证账号密码
        int expiredSeconds = remember == false ? DEFAULT_EXPIRED_SECONDS : REMEMBER_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setMaxAge(expiredSeconds);
            cookie.setPath(contextPath);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
