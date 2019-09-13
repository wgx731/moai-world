package com.github.wgx731.ak47;

import com.github.wgx731.ak47.config.AppConfig;
import com.github.wgx731.ak47.security.SecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Import({
    AppConfig.class,
    SecurityConfiguration.class
})
@EntityScan(basePackages = {
    "com.github.wgx731.ak47.model"
})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
