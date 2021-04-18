package org.example.trade.launch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.example.trade.adapter.rest")
public class RestLaunchConfig {
}
