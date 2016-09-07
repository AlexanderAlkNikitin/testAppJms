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
public class JmsActivateCardServiceSender {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicDestination;


    public void sendObjectXmlToQueue(OrderActivatedCard orderActivatedCard) {
        this.jmsTemplate.convertAndSend(orderActivatedCard);
    }
    public void sendObjectXmlToQueue(Destination destination,OrderActivatedCard orderActivatedCard) {
        this.jmsTemplate.convertAndSend(destination,orderActivatedCard);
    }

    public void sendObjectXmlToTopic(OrderActivatedCard orderActivatedCard) {
        this.jmsTemplate.convertAndSend(topicDestination, orderActivatedCard);
    }

}
