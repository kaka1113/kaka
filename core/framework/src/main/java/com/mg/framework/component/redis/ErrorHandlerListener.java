package com.mg.framework.component.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

/**
 * redis失效key消费异常兜底
 *
 * @author tjq
 * @since 2022/5/18 10:50
 */
public class ErrorHandlerListener implements ErrorHandler {

    private Logger logger = LoggerFactory.getLogger(ErrorHandlerListener.class);

    @Override
    public void handleError(Throwable t) {
        if (this.logger.isErrorEnabled()) {
            this.logger.error("Unexpected error occurred in scheduled task.", t);
        }
    }
}
