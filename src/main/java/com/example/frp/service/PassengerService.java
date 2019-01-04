package com.example.frp.service;

import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 19:35
 */
public interface PassengerService {
    String checkOrderInfo(String payload);

    String doOrder(String url,String data);

    String getQueueCount(String payload);
}
