package com.mg.framework.component.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝回调
 *
 * @author : tjq
 * @since : 2022-05-11
 */
public class RejectedEventHandlerCallback implements RejectedExecutionHandler {

    private Logger log = LoggerFactory.getLogger(RejectedEventHandlerCallback.class);


    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.error("线程池投递事件被拒绝");
    }
}
