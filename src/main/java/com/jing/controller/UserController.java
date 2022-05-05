package com.jing.controller;

import com.jing.annotation.LoginRequired;
import com.jing.pojo.User;
import com.jing.service.UserService;
import com.jing.utils.CommunityUtils;
import com.jing.utils.HostHolder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Value("${server.servlet.context-path}")
    String contextPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @GetMapping("/setting")
    public String toSettingPage(){
        return "site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImg, Model model){
        if(headerImg == null){
            model.addAttribute("error","你还没有上传图片！");
            return "site/setting";
        }

        String fileName = headerImg.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式错误！");
            return "site/setting";
        }
        //随机文件名
        fileName = CommunityUtils.generateUUID()+suffix;
        //指定存放路径
        File dest = new File(uploadPath+"/"+fileName);
        try {
            //存储文件
            headerImg.transferTo(dest);
        } catch (IOException e) {
            log.error("【上传文件】失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常",e);
        }
        //更新用户的图片路径
        User user = hostHolder.getUser();
        String headUrl =domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headUrl);
        return "redirect:/index";
    }
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath+"/"+fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        response.setContentType("image/"+suffix);
        try (
                ServletOutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);
                ){
            byte[] buffer = new byte[1024];
            int p = 0;
            while ((p = fis.read(buffer))!=-1){
                os.write(buffer,0,p);
            }

        } catch (IOException e) {
            log.error("读取头像错误"+e.getMessage());
        }

    }
    @PostMapping("/password")
    public String password(Model model,String oldpassword,String newpassword){
        User user = hostHolder.getUser();
        if(!user.getPassword().equals(CommunityUtils.md5(oldpassword+user.getSalt()))){
            model.addAttribute("passwordMsg","原密码错误！");
            return "site/setting";
        }
        userService.updatePassword(user.getSalt(),user.getId(),newpassword);
        return "redirect:/logout";
    }

}
