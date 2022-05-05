package com.jing;

import com.jing.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTests {
    @Autowired
    MailClient client;
    @Autowired
    TemplateEngine templateEngine;
    @Test
    void a(){
        client.setMail("jollyhawk@163.com","Test from caigou","我真的是菜！");
    }
    @Test
    void sendHtml(){
        Context context = new Context();
        context.setVariable("username","jing213");
        String content = templateEngine.process("/mail/demo", context);
        client.setMail("jollyhawk@163.com","Test from caigou(HTML)",content);
    }
}
