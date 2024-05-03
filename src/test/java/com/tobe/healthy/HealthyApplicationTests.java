package com.tobe.healthy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

@Slf4j
class HealthyApplicationTests {

    @Test
    void contextLoads() {
        boolean result = StringUtils.hasText("        ");
        log.info("result => {}", result);
    }
}
