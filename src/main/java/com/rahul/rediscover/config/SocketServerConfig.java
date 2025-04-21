package com.rahul.rediscover.config;

import com.rahul.rediscover.service.RequestResponseService;
import com.rahul.rediscover.socket.server.SocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SocketServerConfig {

    @Value("${socket.server.port}")
    private int port;

    @Autowired
    private RequestResponseService requestResponseService;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SocketServer socketServer() {
        return new SocketServer(port, requestResponseService);
    }

}
