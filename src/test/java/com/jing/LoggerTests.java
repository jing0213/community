package com.jing;

import com.jing.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoggerTests {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);
    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    void testLogger(){
        logger.info("info log");
        logger.error("error log");
        logger.warn("warn log");
        logger.debug("debug log");
    }
    @Test
    void test(){
        String text = "你是个贱人， 你还吸毒，爱看 半 藏 森 林 ！";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
