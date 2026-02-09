package com.igna.tienda.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Aplicación Web Spring Boot para Tienda
 * 
 * Reutiliza 100% la lógica de negocio de las capas core e infra.
 * NO modifica entidades ni servicios existentes.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.igna.tienda.core.domain")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
