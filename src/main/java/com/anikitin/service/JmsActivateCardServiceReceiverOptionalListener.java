package com.anikitin.service;

import generated.OrderActivatedCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MarshallingMessageConverter;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by anikitin on 13.09.2016.
 */
@Service
public class JmsActivateCardServiceReceiverOptionalListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(JmsActivateCardServiceReceiverOptionalListener.class);
    @Autowired
    private MarshallingMessageConverter oxmMessageConverter;
    @Autowired
    private StoreMessageFromTopic storeMessageFromTopic;
    @Override
    public void onMessage(Message message) {
        OrderActivatedCard orderActivatedCard = null;
        try {
            orderActivatedCard = (OrderActivatedCard) oxmMessageConverter.fromMessage(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        LOG.info("received: " + orderActivatedCard);
        storeMessageFromTopic.add(orderActivatedCard);
    }
}
