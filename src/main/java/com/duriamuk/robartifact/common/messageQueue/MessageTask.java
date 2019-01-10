package com.duriamuk.robartifact.common.messageQueue;

import com.duriamuk.robartifact.common.tool.ApplicationContextUtils;
import com.duriamuk.robartifact.common.tool.ClassUtils;
import com.duriamuk.robartifact.common.tool.ThreadLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 20:44
 */
public class MessageTask <T> implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(MessageTask.class);

    private MessageConsumerService messageConsumerService;

    private String message;

    private Future future;

    private String cookie = "";

    public MessageTask(Class<T> clazz, String message) {
        messageConsumerService = (MessageConsumerService) ApplicationContextUtils.getBean(ClassUtils.getLowerCaseClassName(clazz));
        this.message = message;
    }

    public MessageTask(Class<T> clazz, String message, String cookie) {
        messageConsumerService = (MessageConsumerService) ApplicationContextUtils.getBean(ClassUtils.getLowerCaseClassName(clazz));
        this.message = message;
        this.cookie = cookie;
    }

    @Override
    public void run() {
        if (messageConsumerService != null) {
            try {
                ThreadLocalUtils.set(cookie);
                messageConsumerService.consumeMessage(message);
                cookie = ThreadLocalUtils.get();
            } catch (Exception e) {
                e.printStackTrace();
                cancelTask();
            }
        }
    }

    public void setFuture(Future future){
        this.future = future;
    }

    private void cancelTask() {
        boolean isCancel = false;
        while (!isCancel) {
            if (future != null) {
                isCancel = future.cancel(false);
            }
        }
    }
}
