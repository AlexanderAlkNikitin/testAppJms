package com.anikitin.service;

import generated.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.ReplyFailureException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;


import static org.junit.Assert.assertEquals;

/**
 * Created by anikitin on 07.09.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springJms-context.xml")
public class JmsActivateCardServiceReceiverTest {


    private static final Logger logger= LoggerFactory.getLogger(JmsActivateCardServiceReceiverTest.class);
    @Autowired
    private JmsActivateCardServiceSender jmsActivateCardServiceSender;
    @Autowired
    private JmsActivateCardServiceReceiver jmsActivateCardServiceReceiver;

    @Autowired
    private JmsLowLevelCodeActivateServiceReceiver jmsLowLevelCodeActivateServiceReceiver;

    @Autowired
    private Destination optionalDestination;
    @Autowired
    private Destination topicDestination;
    @Autowired
    private Destination topicDurable;

    private OrderActivatedCard orderActivatedCard;

    @Test
    public void receiveXml() throws Exception {
        orderActivatedCard=init();
        jmsActivateCardServiceSender.sendObjectXmlToQueue(orderActivatedCard);
        OrderActivatedCard fromQueue = jmsActivateCardServiceReceiver.receiveOrderActivateCard();
        assertEquals(fromQueue, orderActivatedCard);

    }

    @Test
    public void durableSubscribe() throws JMSException, InterruptedException {
        orderActivatedCard=init();
        jmsLowLevelCodeActivateServiceReceiver.stopConsumer();
        jmsLowLevelCodeActivateServiceReceiver.openConsumerDurableSubscribe();
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        Thread.sleep(1000);
        jmsLowLevelCodeActivateServiceReceiver.stopConsumer();
        System.out.println("Off consumer");
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        System.out.println("on consumer");
        jmsLowLevelCodeActivateServiceReceiver.openConsumerDurableSubscribe();

    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void sendAfterReceive() throws Exception {
        logger.info("init test Object");
        orderActivatedCard=init();
        jmsActivateCardServiceSender.sendObjectXmlToQueue(orderActivatedCard);
        jmsActivateCardServiceSender.sendAndReceiv(optionalDestination);
        assertEquals(orderActivatedCard,jmsActivateCardServiceReceiver.receiveOrderActivateCard(optionalDestination));
    }
    @Test(expected = Exception.class)
    @Transactional
    public void sendAfterReceiveRallBack() throws Exception {
        logger.info("init test Object");
        orderActivatedCard=init();
        jmsActivateCardServiceSender.sendObjectXmlToQueue(orderActivatedCard);
        OrderActivatedCard forwardingOrder = jmsActivateCardServiceReceiver.sendAfterReceiveRallBack();
        Thread.sleep(1000);
        OrderActivatedCard forwardedOrder = jmsActivateCardServiceReceiver.receiveOrderActivateCardFromOrder();

        assertEquals(orderActivatedCard,forwardingOrder);
        assertEquals(orderActivatedCard,forwardedOrder);
        assertEquals(forwardingOrder,forwardedOrder);

    }

    public OrderActivatedCard init(){
        Person person = new Person();
        person.setID(1);
        person.setFirstName("Ivan");
        person.setLastName("Ivanov");
        Card card = new Card();
        card.setID(1);
        card.setPerson(person);
        card.setLimit(10000);
        card.setStatus(StatusType.DISABLED);
        orderActivatedCard = new OrderActivatedCard();
        orderActivatedCard.setID(1);
        orderActivatedCard.setCard(card);
        orderActivatedCard.setSolve(SolveType.YES);
        return orderActivatedCard;
    }
}