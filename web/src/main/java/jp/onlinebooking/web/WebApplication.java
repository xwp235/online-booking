package jp.onlinebooking.web;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;
import java.util.*;

@SpringBootApplication
public class WebApplication {

    private static final Logger log = LoggerFactory.getLogger(WebApplication.class);

    public static void main(String[] args) {
        loadConfiguration(args);
        startApp(args);
    }

    public static void loadConfiguration(String[] args) {
        try {
            var useDefaultConfigFilepath = false;
            if (args.length == 0) {
                useDefaultConfigFilepath = true;
                args = new String[] {
                  System.getProperty("user.dir")
                };
            }
            var configFilePath = args[0];
            if (useDefaultConfigFilepath) {
                configFilePath = configFilePath + File.separator + "env";
            }
            var separatorIndex = configFilePath.lastIndexOf(File.separator);
            var configFileDir = configFilePath.substring(0, separatorIndex);
            var configFilename = configFilePath.substring(separatorIndex + 1);
            // 获取当前系统属性
            var currentEnv = System.getenv();
            var dotenv = Dotenv.configure()
                    .directory(configFileDir)
                    .filename(configFilename)
                    .load();
            dotenv.entries().forEach(entry -> {
                var key = entry.getKey();
                var val = entry.getValue();
                if (!currentEnv.containsKey(key)) {
                    if ("LOGGING_STRUCTURED".equals(key)) {
                        var logFilename = "classpath:logback-spring.xml";
                        if ("on".equals(val)) {
                          logFilename = "classpath:logback-spring-structured.xml";
                        }
                        System.setProperty("logging.config", logFilename);
                    }
                    System.setProperty(key, val);
                }
            });
        } catch (DotenvException e) {
//            var errorMessage = e.getMessage();
//            var locale = Locale.getDefault();
//            if (errorMessage.contains("Could not find")) {
//                var bundle = ResourceBundle.getBundle("multi-languages/warnings", locale);
//                LogUtil.warn(bundle.getString("useDefaultAppConfig"));
//            } else if (errorMessage.contains("Malformed entry")) {
//                var bundle = ResourceBundle.getBundle("multi-languages/errors", locale);
//                var param = errorMessage.substring(errorMessage.indexOf('y') + 2);
//                LogUtil.warn(bundle.getString("appConfigMalformed"), param);
//                throw e;
//            } else {
//                throw e;
//            }
        }
    }

    public static void startApp(String[] args) {
        var app = new SpringApplicationBuilder(WebApplication.class);
        app
           .web(WebApplicationType.SERVLET)
           .run(args);
    }

}
