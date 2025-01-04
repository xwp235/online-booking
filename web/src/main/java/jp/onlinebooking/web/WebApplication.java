package jp.onlinebooking.web;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import io.micrometer.common.util.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;
import java.util.ResourceBundle;

@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        System.out.println(args.length);
        System.getProperty("user.dir");
        for (String arg : args) {
            System.out.println("++");
            System.out.println(arg);
        }
//        loadConfiguration();
        /**
         * // 创建 SpringApplication 实例
         *         SpringApplication application = new SpringApplication(OnlineBookingApplication.class);
         *
         *         // 创建临时的 Spring 环境，用于读取配置文件
         *         ConfigurableEnvironment environment = new StandardEnvironment();
         *
         *         // 读取配置属性 `config.logging.structured`
         *         String structured = environment.getProperty("config.logging.structured", "off");
         *
         *         // 根据属性值决定加载的日志配置文件
         *         String logConfig = "off".equalsIgnoreCase(structured)
         *                 ? "classpath:logback-spring.xml"
         *                 : "classpath:logback-spring-structured.xml";
         *
         *         // 动态设置 logging.config 属性
         *         Map<String, Object> defaultProperties = new HashMap<>();
         *         defaultProperties.put("logging.config", logConfig);
         *         application.setDefaultProperties(defaultProperties);
         */
        SpringApplication.run(WebApplication.class, args);
    }

    public static void loadConfiguration() {
        try {
            // 加载相应的.env文件
            var envName = System.getProperty("spring.profiles.active", "dev");
            var filepath = System.getProperty("config");
            if (StringUtils.isBlank(filepath)) {
                filepath = System.getProperty("user.dir");
            }
            // 获取当前系统属性
            var currentEnv = System.getenv();
            var dotenv = Dotenv.configure()
                    .directory(filepath)
                    .filename(".env." + envName)
                    .load();
            dotenv.entries().forEach(entry -> {
                if (!currentEnv.containsKey(entry.getKey())) {
//                    System.out.println(entry.getKey()+"--->"+entry.getValue());
                    System.setProperty(entry.getKey(), entry.getValue());
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

}
