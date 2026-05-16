package com.purchasehistorysystem.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
    private static final ExecutorService POOL = Executors.newFixedThreadPool(2);

    public static ExecutorService getPool() {
        return POOL;
    }

    public static void shutdown() {
        POOL.shutdown();
    }
}
