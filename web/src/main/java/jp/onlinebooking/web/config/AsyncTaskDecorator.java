package jp.onlinebooking.web.config;

import io.micrometer.common.lang.NonNullApi;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Objects;

@NonNullApi
class AsyncTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        // メインスレッドからコンテキストデータを取得
        var contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (Objects.nonNull(contextMap)) {
                    // サブスレッドが実行される前に、現在のサブスレッドのコンテキストにデータを設定
                    MDC.setContextMap(contextMap);
                } else {
                    MDC.clear();
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
