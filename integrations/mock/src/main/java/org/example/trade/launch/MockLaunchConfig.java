package org.example.trade.launch;

import org.example.trade.adapter.broker.SingleAccountBrokerService;
import org.example.trade.adapter.broker.mock.MockNodeConfig;
import org.example.trade.adapter.broker.mock.MockSingleAccountBrokerService;
import org.example.trade.adapter.broker.mock.UserConfig;
import org.example.trade.application.TradeService;
import org.example.trade.interfaces.SyncService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Profile("mock")
@Configuration
@ComponentScan(basePackages = "org.example.trade.adapter.broker.mock")
public class MockLaunchConfig {

    @Bean
    List<SingleAccountBrokerService> registerAll(
        TradeService tradeService,
        SyncService syncService,
        MockNodeConfig config
    ) {
        List<UserConfig> configs = config.getUsers();
        List<SingleAccountBrokerService> services = new ArrayList<>(configs.size());
        for (UserConfig userConfig : configs) {
            MockSingleAccountBrokerService e =
                new MockSingleAccountBrokerService(tradeService, syncService, userConfig);
            services.add(e);
        }
        return services;
    }

}
