package com.jing.controller.interceptor;

import com.jing.pojo.LoginTicket;
import com.jing.pojo.User;
import com.jing.service.UserService;
import com.jing.utils.CookieUtils;
import com.jing.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtils.getValue(request, "ticket");
        //从cookies获得凭证

        //凭证是否有效
        if(ticket!=null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTickeByTicket(ticket);
            //检查凭证
            if(loginTicket!=null && loginTicket.getStatus()!=1 && loginTicket.getExpired().after(new Date())){
                //有效：存取该用户
                User user = userService.findUserById(loginTicket.getUserId());
                //通过hosthodler存储
                hostHolder.setUsers(user);
            }
        }
        System.out.println("pre  ......................................");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null &&modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
