package com.duriamuk.robartifact.service;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-02 17:03
 */
public interface TicketService {
    String checkUser();

    String listTicket(String payload);

    String submitOrderRequest(String payload);
}
