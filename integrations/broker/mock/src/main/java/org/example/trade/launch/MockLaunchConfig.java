package org.example.trade.launch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("mock-broker")
@Configuration
@ComponentScan(basePackages = "org.example.trade.adapter.broker.mock")
public class MockLaunchConfig {
}
