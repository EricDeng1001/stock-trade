package org.example.trade.launch;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "org.example.trade.adapter.jpa")
@EnableJpaRepositories("org.example.trade.adapter.jpa")
@EntityScan(basePackages = "org.example.trade.adapter.jpa.model")
public class JpaLaunchConfig {
}
