package org.example.trade.adapter.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = NodeConfig.class)
public class ConfigTest {

    @Autowired
    NodeConfig nodeConfig;

    @Test
    void configTest() {
        System.out.println(nodeConfig.clientId());
    }
}
