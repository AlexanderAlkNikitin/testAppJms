package com.anikitin.service;

import generated.OrderActivatedCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

/**
 * Created by anikitin on 07.09.2016.
 */
@Service
public class JmsActivateCardServiceReceiver {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JmsActivateCardServiceSender jmsActivateCardServiceSender;
    @Autowired
    private Destination optionalDestination;

    public OrderActivatedCard receiveOrderActivateCard() {
        return (OrderActivatedCard) this.jmsTemplate.receiveAndConvert();
    }

    public OrderActivatedCard receiveOrderActivateCardFromTopic(OrderActivatedCard orderActivatedCard) {
        System.out.println("received: " + orderActivatedCard);
        return orderActivatedCard;
    }

    public OrderActivatedCard sendAfterReceive() {
        OrderActivatedCard receivedOrder = (OrderActivatedCard) this.jmsTemplate.receiveAndConvert();
        jmsActivateCardServiceSender.sendObjectXmlToQueue(optionalDestination, receivedOrder);
        return receivedOrder;

    }

    public OrderActivatedCard receiveOrderActivateCard(Destination optionalDestination) {
        return (OrderActivatedCard) jmsTemplate.receiveAndConvert(optionalDestination);
    }
}
