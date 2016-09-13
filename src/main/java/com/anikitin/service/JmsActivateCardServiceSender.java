package com.anikitin.service;

import generated.Card;
import generated.OrderActivatedCard;
import generated.SolveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;

/**
 * Created by anikitin on 07.09.2016.
 */
@Service
public class JmsActivateCardServiceSender {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination defaultDestination;

    private static final Logger LOG= LoggerFactory.getLogger(JmsActivateCardServiceSender.class);



    public void sendObjectXmlToQueue(OrderActivatedCard orderActivatedCard) {
        LOG.info("sending test object");
        this.jmsTemplate.convertAndSend(defaultDestination,orderActivatedCard);
        jmsTemplate.browse((BrowserCallback<Message>) (session, browser) -> {
            session.commit();
            return session.createMapMessage();
        });
        LOG.info("test object sended");
    }


    @Transactional
    public void sendObjectXmlTo(Destination destination, OrderActivatedCard orderActivatedCard) {
        LOG.info("Send message to " + destination.toString());
        this.jmsTemplate.convertAndSend(destination, orderActivatedCard);
    }


    @Transactional
    public void sendAndReceive(Destination destination) {
        LOG.debug(destination.toString());
        this.jmsTemplate.sendAndReceive(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                LOG.debug("create message consumer");
                MessageConsumer messageConsumer = session.createConsumer(defaultDestination);
                return messageConsumer.receive(1000);
            }
        });
    }

}
