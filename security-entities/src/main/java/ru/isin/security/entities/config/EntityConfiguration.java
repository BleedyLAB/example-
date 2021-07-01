package ru.isin.security.entities.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Krylov Sergey (07.01.2021)
 */
@Configuration
@EntityScan(basePackages = {"ru.isin.security.entities.*"})
public class EntityConfiguration {
}
