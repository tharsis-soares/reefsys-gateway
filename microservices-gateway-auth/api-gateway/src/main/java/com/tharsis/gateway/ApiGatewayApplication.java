package com.tharsis.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * 
 * Funcionalidades:
 * - Roteamento de requisições para microserviços
 * - Autenticação JWT
 * - Rate limiting
 * - CORS configuration
 * - Load balancing
 * 
 * Porta: 8080
 * 
 * @author Tharsis Soares
 */
@SpringBootApplication(exclude = {
    ReactiveSecurityAutoConfiguration.class,
    ReactiveUserDetailsServiceAutoConfiguration.class
})
@EnableDiscoveryClient
public class ApiGatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
