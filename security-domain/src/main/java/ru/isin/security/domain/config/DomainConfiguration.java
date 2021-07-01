package ru.isin.security.domain.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ru.isin.security.domain.*")
public class DomainConfiguration {
}
