package com.mg.framework.component.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 线程池工具类
 *
 * @author : tjq
 * @since : 2022/4/25 11:06
 */
public class ThreadPoolUtils {

    private static Logger log = LoggerFactory.getLogger(ThreadPoolUtils.class);

    /**
     * 异步线程池
     */
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutorMdcWrapper(Runtime.getRuntime().availableProcessors(), 50,
            1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5000));

    /**
     * 定时线程池
     */
    private static final ScheduledExecutorService scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("## stop the threadPool");
                threadPoolExecutor.shutdown();
                scheduledThreadPoolExecutor.shutdown();
            } catch (Throwable e) {
                log.warn("##something goes wrong when stopping threadPool:", e);
            } finally {
                log.info("## threadPool client is down.");
            }
        }));
    }

    /**
     * 获取执行器
     *
     * @return
     */
    public static ThreadPoolExecutor getExePool() {
        return threadPoolExecutor;
    }

    /**
     * 定时线程池
     *
     * @return
     */
    public static ScheduledExecutorService getScheduledPool() {
        return scheduledThreadPoolExecutor;
    }

    /**
     * 添加异步任务
     *
     * @param runnable
     */
    public static void submit(Runnable runnable) {
        log.info("异步任务-当前队列线程数 {} 堆积数量 {}", threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
        threadPoolExecutor.submit(runnable);
    }

    /**
     * 添加异步任务
     *
     * @param task
     * @param <T>
     * @return
     */
    public static <T> Future<T> submit(Callable<T> task) {
        log.info("异步任务-当前队列线程数 {} 堆积数量 {}", threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
        return threadPoolExecutor.submit(task);
    }

    /**
     * 添加本地定时任务
     *
     * @param callable
     * @param delay
     * @param unit
     * @param <T>
     * @return
     */
    public static <T> Future<T> schedule(Callable<T> callable, long delay, TimeUnit unit) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) scheduledThreadPoolExecutor;
        log.info("延时任务-当前队列线程数 {} 堆积数量 {}", executor.getActiveCount(), executor.getQueue().size());
        return scheduledThreadPoolExecutor.schedule(callable, delay, unit);
    }


}
