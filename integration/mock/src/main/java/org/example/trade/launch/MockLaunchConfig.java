package org.example.trade.launch;

import org.example.trade.adapter.broker.mock.MockNodeConfig;
import org.example.trade.adapter.broker.mock.MockSingleAccountBrokerService;
import org.example.trade.adapter.broker.mock.UserConfig;
import org.example.trade.application.TradeService;
import org.example.trade.interfaces.SyncService;
import org.example.trade.port.broker.SingleAccountBrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(config.getCoreSize());
        for (UserConfig userConfig : configs) {
            MockSingleAccountBrokerService e =
                new MockSingleAccountBrokerService(tradeService, syncService, userConfig, scheduledExecutorService);
            services.add(e);
        }
        return services;
    }

}
