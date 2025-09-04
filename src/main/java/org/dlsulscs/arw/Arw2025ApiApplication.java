package org.dlsulscs.arw;

import org.dlsulscs.arw.config.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class Arw2025ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Arw2025ApiApplication.class, args);
    }

}
