package com.ice.framework.component.thread;


import com.ice.framework.component.tracing.TracingContext;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author : tjq
 * @since : 2022/3/31 8:52
 */
public class ThreadMdcUtil {

    public static void setTraceIdIfAbsent() {
        if (MDC.get(TracingContext.TRACING_XID) == null) {
            MDC.put(TracingContext.TRACING_XID, MDC.get(TracingContext.TRACING_XID));
        }
    }

    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

}
