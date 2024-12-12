package com.dws.challenge.common;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI controllerApi() {
        Info info= new Info();
        info.title("Bank Application");
        info.version("1.0.0");
        return new OpenAPI().info(info)
                .addServersItem(new Server().url("http://localhost:8080/").description("Account API"));
    }

}
