package com.jing;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoggerTests {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    void testLogger(){
        logger.info("info log");
        logger.error("error log");
        logger.warn("warn log");
        logger.debug("debug log");
    }
}
