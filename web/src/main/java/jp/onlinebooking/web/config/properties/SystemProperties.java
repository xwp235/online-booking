package jp.onlinebooking.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config")
public class SystemProperties {

    private LoggingProperties logging;

    public LoggingProperties getLogging() {
        return logging;
    }

    public void setLogging(LoggingProperties logging) {
        this.logging = logging;
    }

    public static class LoggingProperties {

        private boolean structured;

        public boolean isStructured() {
            return structured;
        }

        public void setStructured(boolean structured) {
            this.structured = structured;
        }
    }

}
