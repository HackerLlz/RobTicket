package com.duriamuk.robartifact.common.messageQueue;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 20:45
 */
public class TaskDispatcher {
    public static Boolean message(MessageTask messageTask) {
        MessageConsumerThreadPool.submit(messageTask);
        return true;
    }
}
