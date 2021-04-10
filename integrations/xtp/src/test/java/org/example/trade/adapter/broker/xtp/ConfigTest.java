package org.example.trade.adapter.broker.xtp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("xtp")
@SpringBootTest(classes = XTPNodeConfig.class)
public class ConfigTest {

    @Autowired
    XTPNodeConfig XTPNodeConfig;

    @Test
    void configTest() {
        System.out.println(XTPNodeConfig.clientId());
    }
}
