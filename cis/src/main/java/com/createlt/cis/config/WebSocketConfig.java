package com.createlt.cis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebSocketConfig {
   /**
    * 使用spring boot时，使用的是spring-boot的内置容器，
    * 如果要使用WebSocket，需要注入ServerEndpointExporter
    *
    * @return
    */
   @Bean
   public ServerEndpointExporter serverEndpointExporter() {
       return new ServerEndpointExporter();
   }
}
