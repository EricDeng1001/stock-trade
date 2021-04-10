package org.example.trade.launch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("xtp")
@Configuration
@ComponentScan(basePackages = "org.example.trade.adapter.broker.xtp")
public class XtpLaunchConfig {
}
