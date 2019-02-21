package com.duriamuk.robartifact.common.messageQueue;

import com.duriamuk.robartifact.common.tool.ApplicationContextUtils;
import com.duriamuk.robartifact.common.tool.ClassUtils;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.ThreadLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.Future;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 20:44
 */
public class MessageTask<T> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageTask.class);

    private T messageConsumerService;

    private String methodName;

    private String message;

    private Future future;

    private String cookie;

    public MessageTask(Class<T> clazz, String methodName, String message) {
        messageConsumerService = (T) ApplicationContextUtils.getBean(ClassUtils.getLowerCaseClassName(clazz));
        this.methodName = methodName;
        this.message = message;

        HttpServletRequest request = HttpUtils.getRequest();
        cookie = !ObjectUtils.isEmpty(request) ? request.getHeader(HttpUtils.COOKIE) : ThreadLocalUtils.get();
    }

    @Override
    public void run() {
        if (messageConsumerService != null) {
            try {
                ThreadLocalUtils.set(cookie);
                massage();
                cookie = ThreadLocalUtils.get();
            } catch (Exception e) {
                e.printStackTrace();
                cancelTask();
            }
        } else {
            logger.error("messageConsumerService不存在");
            cancelTask();
        }
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    private void massage() throws Exception {
        Class clazz = messageConsumerService.getClass();
        Method method = clazz.getDeclaredMethod(methodName, String.class);  // new Class[]({String.class, int.class})
        method.invoke(messageConsumerService, message);
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
