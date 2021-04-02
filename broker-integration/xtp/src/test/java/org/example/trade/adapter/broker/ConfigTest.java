package org.example.trade.adapter.broker;

import org.example.trade.adapter.broker.xtp.NodeConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("xtp")
@SpringBootTest(classes = NodeConfig.class)
public class ConfigTest {

    @Autowired
    NodeConfig nodeConfig;

    @Test
    void configTest() {
        System.out.println(nodeConfig.clientId());
    }
}
