package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.PO.passenger.PassengerPO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 19:35
 */
public interface PassengerService {
    String passengerInfo();

    String checkOrderInfo(String payload);

    String doOrder(String url, String data);

    String getQueueCount(String payload);

    Boolean updatePassenger();

    List<PassengerPO> listPassengerByUsername(String username);

    Boolean sync12306Passenger(String payload);
}
