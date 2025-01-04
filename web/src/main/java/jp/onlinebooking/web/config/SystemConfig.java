package jp.onlinebooking.web.config;

import jp.onlinebooking.web.config.properties.SystemProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SystemProperties.class)
public class SystemConfig {
}
