package com.anikitin.service;

import generated.OrderActivatedCard;
import org.springframework.stereotype.Service;

/**
 * Created by anikitin on 07.09.2016.
 */
@Service
public class JmsActivateCardServiceReceiverOptional {

    public OrderActivatedCard receiveOrderActivateCardFromTopic(OrderActivatedCard orderActivatedCard) {
        System.out.println("received: " + orderActivatedCard);
        return orderActivatedCard;
    }
}
