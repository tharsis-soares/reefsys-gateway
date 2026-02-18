package com.tharsis.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server - Service Discovery
 * 
 * Responsável por:
 * - Registro de microserviços
 * - Health checks
 * - Service discovery
 * 
 * Dashboard disponível em: http://localhost:8761
 * 
 * @author Tharsis Soares
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
