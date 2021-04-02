package org.example.trade.adapter.broker.mock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("mock-broker")
@Configuration
@ConfigurationProperties(prefix = "mock")
public class MockConfig {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
