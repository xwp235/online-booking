package jp.onlinebooking.web.modules.base.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class BaseService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected TransactionTemplate transactionTemplate;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @PostConstruct
    private void init() {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        this.transactionTemplate.setTimeout(TransactionDefinition.TIMEOUT_DEFAULT);
    }

    protected void error(String message, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    protected void warn(String message, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, args);
        }
    }

    protected void info(String message, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(message, args);
        }
    }

    protected void debug(String message, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, args);
        }
    }

    protected void trace(String message, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace(message, args);
        }
    }

}
