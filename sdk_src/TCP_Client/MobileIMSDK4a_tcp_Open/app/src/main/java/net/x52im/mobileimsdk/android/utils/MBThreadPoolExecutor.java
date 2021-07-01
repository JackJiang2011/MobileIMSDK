/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project. 
 * All rights reserved.
 *
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 *
 * MBThreadPoolExecutor.java at 2020-8-8 15:58:02, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 本类是自定义固定大小的线程池实现类。
 * <br>
 * 每次提交一个任务就创建一个线程，直到线程达到线程池的最大大小。
 * 线程池的大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。
 * <p>
 * 合理利用线程池能够带来三个好处：
 * 第一：降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的消耗。
 * 第二：提高响应速度。当任务到达时，任务可以不需要等到线程创建就能立即执行。
 * 第三：提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。
 * 我们可以通过ThreadPoolExecutor来创建一个线程池：
 * new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, milliseconds,runnableTaskQueue, handler);
 * <p>
 * corePoolSize（线程池的基本大小）：
 * 当提交一个任务到线程池时，线程池会创建一个线程来执行任务，
 * 即使其他空闲的基本线程能够执行新任务也会创建线程，等到需要执行的任务数大于线程池基本大小时就不再创建。
 * 如果调用了线程池的prestartAllCoreThreads方法，线程池会提前创建并启动所有基本线程。
 * <p>
 * runnableTaskQueue（任务队列）：用于保存等待执行的任务的阻塞队列。 可以选择以下几个阻塞队列。
 * ArrayBlockingQueue：是一个基于数组结构的有界阻塞队列，此队列按 FIFO（先进先出）原则对元素进行排序。
 * <p>
 * LinkedBlockingQueue：一个基于链表结构的阻塞队列，此队列按FIFO （先进先出） 排序元素，吞吐量通常要高于ArrayBlockingQueue。
 * 静态工厂方法Executors.newFixedThreadPool()使用了这个队列。
 * <p>
 * SynchronousQueue：一个不存储元素的阻塞队列。每个插入操作必须等到另一个线程调用移除操作，否则插入操作一直处于阻塞状态，
 * 吞吐量通常要高于LinkedBlockingQueue，静态工厂方法Executors.newCachedThreadPool使用了这个队列。
 * <p>
 * PriorityBlockingQueue：一个具有优先级的无限阻塞队列。
 * <p>
 * maximumPoolSize（线程池最大大小）：
 * 线程池允许创建的最大线程数。如果队列满了，并且已创建的线程数小于最大线程数，则线程池会再创建新的线程执行任务。值得注意的是如果使用了无界的任务队列这个参数就没什么效果。
 * <p>
 * ThreadFactory：
 * 用于设置创建线程的工厂，可以通过线程工厂给每个创建出来的线程设置更有意义的名字。
 * <p>
 * RejectedExecutionHandler（饱和策略）：当队列和线程池都满了，说明线程池处于饱和状态，那么必须采取一种策略处理提交的新任务。
 * 这个策略默认情况下是AbortPolicy，表示无法处理新任务时抛出异常。以下是JDK1.5提供的四种策略。
 * AbortPolicy：直接抛出异常。
 * CallerRunsPolicy：只用调用者所在线程来运行任务。
 * DiscardOldestPolicy：丢弃队列里最近的一个任务，并执行当前任务。
 * DiscardPolicy：不处理，丢弃掉。
 * 当然也可以根据应用场景需要来实现RejectedExecutionHandler接口自定义策略。如记录日志或持久化不能处理的任务。
 * <p>
 * keepAliveTime（线程活动保持时间）：线程池的工作线程空闲后，保持存活的时间。
 * 所以如果任务很多，并且每个任务执行的时间比较短，可以调大这个时间，提高线程的利用率。
 * <p>
 * TimeUnit（线程活动保持时间的单位）：可选的单位有天（DAYS），小时（HOURS），分钟（MINUTES），
 * 毫秒(MILLISECONDS)，微秒(MICROSECONDS, 千分之一毫秒)和毫微秒(NANOSECONDS, 千分之一微秒)。</p>
 *
 * @author FreddyChen（原作者）, Jack Jiang（修订者）
 * @since 5.0
 * @see <a href="http://www.infoq.com/cn/articles/java-threadPool">参考资料1</a>
 * @see <a href="http://www.52im.net/thread-2671-1-1.html">本类的原始代码参考来源</a>
 */
public class MBThreadPoolExecutor {

    private static final String TAG = MBThreadPoolExecutor.class.getSimpleName();

    /** CPU个数 */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /** 线程池中核心线程的数量 */
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    /** 线程池中最大线程数量 */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /** 非核心线程的超时时长，当系统中非核心线程闲置时间超过keepAliveTime之后，则会被回收。
     * 如果ThreadPoolExecutor的allowCoreThreadTimeOut属性设置为true，则该参数也表示核心线程的超时时长.*/
    private static final long KEEP_ALIVE_TIME = 30L;
    /** 最多排队个数，这里控制线程创建的频率 */
    private static final int WAIT_COUNT = 128;

    private static ThreadPoolExecutor pool = createThreadPoolExecutor();

    private static ThreadPoolExecutor createThreadPoolExecutor() {
        if (pool == null) {
            pool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(WAIT_COUNT),
                    new CThreadFactory("MBThreadPool", Thread.NORM_PRIORITY - 2),
                    new CHandlerException());
        }
        return pool;
    }

    public static class CThreadFactory implements ThreadFactory {
        private AtomicInteger counter = new AtomicInteger(1);
        private String prefix = "";
        private int priority = Thread.NORM_PRIORITY;

        public CThreadFactory(String prefix, int priority) {
            this.prefix = prefix;
            this.priority = priority;
        }

        public CThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        public Thread newThread(Runnable r) {
            Thread executor = new Thread(r, prefix + " #" + counter.getAndIncrement());
            executor.setDaemon(true);
            executor.setPriority(priority);
            return executor;
        }
    }

    /**
     * 抛弃当前的任务
     */
    private static class CHandlerException extends ThreadPoolExecutor.AbortPolicy {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            Log.d(TAG, "rejectedExecution:" + r);
            Log.e(TAG, logAllThreadStackTrace().toString());
//          Tips.showForce("任务被拒绝", 5000);
            if (!pool.isShutdown()) {
                pool.shutdown();
                pool = null;
            }

            pool = createThreadPoolExecutor();
        }
    }

    private static ExecutorService jobsForUI = Executors.newFixedThreadPool(
            CORE_POOL_SIZE, new CThreadFactory("MBJobsForUI", Thread.NORM_PRIORITY - 1));

    public static void runInBackground(Runnable runnable) {
        if (pool == null) {
            createThreadPoolExecutor();
        }
        pool.execute(runnable);
    }

    private static Thread mainThread;
    private static Handler mainHandler;

    static {
        Looper mainLooper = Looper.getMainLooper();
        mainThread = mainLooper.getThread();
        mainHandler = new Handler(mainLooper);
    }

    public static boolean isOnMainThread() {
        return mainThread == Thread.currentThread();
    }

    public static void runOnMainThread(Runnable r) {
        if (isOnMainThread()) {
            r.run();
        } else {
            mainHandler.post(r);
        }
    }

    public static void runOnMainThread(Runnable r, long delayMillis) {
        if (delayMillis <= 0) {
            runOnMainThread(r);
        } else {
            mainHandler.postDelayed(r, delayMillis);
        }
    }

    // 用于记录后台等待的Runnable，第一个参数外面的Runnable，第二个参数是等待中的Runnable
    private static HashMap<Runnable, Runnable> mapToMainHandler = new HashMap<Runnable, Runnable>();

    public static void runInBackground(final Runnable runnable, long delayMillis) {
        if (delayMillis <= 0) {
            runInBackground(runnable);
        } else {
            Runnable mainRunnable = () -> {
                mapToMainHandler.remove(runnable);
                pool.execute(runnable);
            };

            //# Bug FIX: 20200716 by Jack Jiang
            if(mapToMainHandler.containsKey(runnable)) {
                Log.d(TAG, "该runnable（"+runnable+"）仍在mapToMainHandler中，表示它并未被执行，将" +
                        "先从mainHandler中移除，否则存在上次延迟执行并未完成，本次又再次提交延迟执行任务，失去了延迟执行的意义！");
                removeCallbackInBackground(runnable);
            }
            //# Bug FIX: END

            mapToMainHandler.put(runnable, mainRunnable);
            mainHandler.postDelayed(mainRunnable, delayMillis);
        }
    }

    /**
     * 对runOnMainThread的，移除Runnable
     *
     * @param r
     */
    public static void removeCallbackOnMainThread(Runnable r) {
        mainHandler.removeCallbacks(r);
    }

    public static void removeCallbackInBackground(Runnable runnable) {
        Runnable mainRunnable = mapToMainHandler.get(runnable);
        if (mainRunnable != null) {
            mainHandler.removeCallbacks(mainRunnable);
            pool.remove(mainRunnable); // add by jackjiang 20200718：记得尝试清除已加入线程队列但未开始执行的任务
        }
        else
            pool.remove(runnable);     // add by jackjiang 20200718：记得尝试清除已加入线程队列但未开始执行的任务
    }

    public static void logStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("getActiveCount=");
        sb.append(pool.getActiveCount());
        sb.append("\ngetTaskCount=");
        sb.append(pool.getTaskCount());
        sb.append("\ngetCompletedTaskCount=");
        sb.append(pool.getCompletedTaskCount());
        Log.d(TAG, sb.toString());
    }

    public static StringBuilder logAllThreadStackTrace() {
        StringBuilder builder = new StringBuilder();
        Map<Thread, StackTraceElement[]> liveThreads = Thread.getAllStackTraces();
        for (Iterator<Thread> i = liveThreads.keySet().iterator(); i.hasNext(); ) {
            Thread key = i.next();
            builder.append("Thread ").append(key.getName()).append("\n");
            StackTraceElement[] trace = liveThreads.get(key);
            for (int j = 0; j < trace.length; j++) {
                builder.append("\tat ").append(trace[j]).append("\n");
            }
        }
        return builder;
    }
}
