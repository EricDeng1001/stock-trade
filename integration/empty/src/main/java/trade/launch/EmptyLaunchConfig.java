package trade.launch;

import org.example.trade.adapter.broker.SingleAccountBrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class EmptyLaunchConfig {

    @Bean
    List<SingleAccountBrokerService> services() {
        return Collections.emptyList();
    }

}
