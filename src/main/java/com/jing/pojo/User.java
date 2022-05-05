package com.jing.pojo;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.util.Date;


@Data
@Validated
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    @Email(message = "邮箱格式不正确！")
    private String email;
    private int type;
    private int status;    //status为0表示未激活
    //激活码
    private String activationCode;
    private String headerUrl;
    private Date createTime;

}
