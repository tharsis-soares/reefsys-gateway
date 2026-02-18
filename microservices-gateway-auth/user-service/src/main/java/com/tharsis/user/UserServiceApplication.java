package com.tharsis.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Service Application
 *
 * Responsabilidades:
 * - Gerenciamento de usuários
 * - CRUD de usuários
 * - Validação de dados de usuários
 * - Integração com outros serviços
 *
 * Porta: 8082
 *
 * @author Tharsis Soares
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
