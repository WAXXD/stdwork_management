package com.stdwork_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableSwagger2
@MapperScan("com.stdwork_management.mapper")
public class StdworkManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StdworkManagementApplication.class, args);
    }

}
