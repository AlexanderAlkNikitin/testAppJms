package com.anikitin.service;

import generated.OrderActivatedCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

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

    private static final Logger LOG= LoggerFactory.getLogger(JmsActivateCardServiceReceiver.class);



    public OrderActivatedCard receiveOrderActivateCard() {
        return (OrderActivatedCard) this.jmsTemplate.receiveAndConvert();
    }

    public OrderActivatedCard receiveOrderActivateCardFromTopic(OrderActivatedCard orderActivatedCard) {
        System.out.println("received: " + orderActivatedCard);
        return orderActivatedCard;
    }

    @Transactional
    public OrderActivatedCard sendAfterReceiveRallBack() throws Exception {
        LOG.info("receiving test Object");
        OrderActivatedCard receivedOrder = receiveOrderActivateCard();
        LOG.info("test object received");
        LOG.info("sending test object after receive");
        jmsActivateCardServiceSender.sendObjectXmlTo(optionalDestination, receivedOrder);
        if(1==1)throw new Exception();
        LOG.info("test object sended after receive");
        return receivedOrder;

    }



    public OrderActivatedCard receiveOrderActivateCardFromOrder() {
        return (OrderActivatedCard) jmsTemplate.receiveAndConvert(optionalDestination);
    }

    public OrderActivatedCard receiveOrderActivateCard(Destination destination) {

        return (OrderActivatedCard) this.jmsTemplate.receiveAndConvert(destination);
    }
}
