package com.thirdparty.ticketing.support;

import com.thirdparty.ticketing.support.integration.AspectTestConfig;
import com.thirdparty.ticketing.support.integration.AspectTestConfig.DebounceTarget;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@SpringBootTest
@AutoConfigureMockMvc
@Import(AspectTestConfig.class)
public class BaseIntegrationTest extends TestContainerStarter {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DebounceTarget debounceTarget() {
            return new DebounceTarget();
        }
    }
}
