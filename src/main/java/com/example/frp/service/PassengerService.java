package com.example.frp.service;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 19:35
 */
public interface PassengerService {
    String checkOrderInfo(String payload);

    String doOrder(String url,String data);
}
