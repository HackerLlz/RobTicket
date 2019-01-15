package com.duriamuk.robartifact.common.messageQueue;

import java.util.concurrent.*;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 20:49
 */
public class MessageConsumerThreadPool {
    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    public static Future<?> message(MessageTask messageTask) {
        Future<?> future = executor.submit(messageTask);
        messageTask.setFuture(future);
        return future;
    }
}
