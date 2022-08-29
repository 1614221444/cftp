package com.createlt.cis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

@SpringBootApplication
public class CisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CisApplication.class, args);
    }

}
