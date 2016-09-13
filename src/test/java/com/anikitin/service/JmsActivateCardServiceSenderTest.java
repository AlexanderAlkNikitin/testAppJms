package com.anikitin.service;

import generated.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;
import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by anikitin on 07.09.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springJms-context.xml")
public class JmsActivateCardServiceSenderTest {

    private static final Logger LOG = LoggerFactory.getLogger(JmsActivateCardServiceSenderTest.class);
    @Autowired
    private JmsActivateCardServiceSender jmsActivateCardServiceSender;
    @Autowired
    private JmsActivateCardServiceReceiver jmsActivateCardServiceReceiver;
    @Autowired
    private StoreMessageFromTopic storeMessageFromTopic;
    @Autowired
    private Destination topicDestination;

    @Autowired
    private Destination optionalDestination;

    @Autowired
    private DefaultMessageListenerContainer jmsContainer2;

    @Autowired
    private DefaultMessageListenerContainer jmsContainer;
    @Autowired
    private JmsLowLevelCodeActivateServiceReceiver jmsLowLevelCodeActivateServiceReceiver;
    @Autowired
    private Destination topicDurable;

    @org.junit.Test
    public void sendAndReceiveQueueAndTopic() throws Exception {
        LOG.info("Starting test send to queue and receive");
        storeMessageFromTopic.clear();
        OrderActivatedCard orderActivatedCard = getOrderActivatedCard();
        jmsActivateCardServiceSender.sendObjectXmlToQueue(orderActivatedCard);
        OrderActivatedCard fromQueue = jmsActivateCardServiceReceiver.receiveOrderActivateCard();
        assertEquals(fromQueue, orderActivatedCard);
        LOG.info("Starting test send to topic and receive");
        orderActivatedCard = getOrderActivatedCard();
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDestination, orderActivatedCard);
        Thread.sleep(1000);
        assertTrue(storeMessageFromTopic.getSize() == 1);
        assertEquals(orderActivatedCard, storeMessageFromTopic.get());
    }

    @Test
    public void sendObjectXmlToTopicWhenTopicOffDropMessage() throws Exception {
        LOG.info("Starting test send to topic and drop");
        storeMessageFromTopic.clear();
        LOG.info("Stopping listeners");
        jmsContainer.shutdown();
        jmsContainer2.shutdown();
        OrderActivatedCard orderActivatedCard = getOrderActivatedCard();
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDestination, orderActivatedCard);
        LOG.info("Start listeners");
        jmsContainer.start();
        jmsContainer2.start();
        assertTrue(storeMessageFromTopic.getSize() == 0);
        assertEquals(null, storeMessageFromTopic.get());
    }

    @Test
    public void sendObjectXmlToTopicWhenTopicOffAndReceiveWhenOnMessage() throws JMSException, InterruptedException {
        storeMessageFromTopic.clear();
        OrderActivatedCard orderActivatedCard = getOrderActivatedCard();
        jmsLowLevelCodeActivateServiceReceiver.stopConsumer();
        jmsLowLevelCodeActivateServiceReceiver.openConsumerDurableSubscribe();
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        Thread.sleep(1000);
        jmsLowLevelCodeActivateServiceReceiver.stopConsumer();
        Thread.sleep(1000);
        LOG.info("Off listener");
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        jmsActivateCardServiceSender.sendObjectXmlTo(topicDurable,orderActivatedCard);
        LOG.info("on listener");
        jmsLowLevelCodeActivateServiceReceiver.openConsumerDurableSubscribe();
        assertTrue(storeMessageFromTopic.getSize() == 1);
        assertEquals(orderActivatedCard, storeMessageFromTopic.get());
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void sendAfterReceive() throws Exception {
        LOG.info("Start test send after receive");
        OrderActivatedCard orderActivatedCard = getOrderActivatedCard();
        jmsActivateCardServiceSender.sendObjectXmlToQueue(orderActivatedCard);
        jmsActivateCardServiceSender.sendAndReceive(optionalDestination);
        assertEquals(orderActivatedCard,jmsActivateCardServiceReceiver.receiveOrderActivateCard(optionalDestination));
    }

    @Test(expected = Exception.class)
    @Transactional
    public void sendAfterReceiveRollBack() throws Exception {
        LOG.info("Start test rollBack");
        OrderActivatedCard orderActivatedCard = getOrderActivatedCard();
        jmsActivateCardServiceSender.sendObjectXmlToQueue(orderActivatedCard);
        OrderActivatedCard forwardingOrder = jmsActivateCardServiceReceiver.sendAfterReceiveRollBack();
        Thread.sleep(1000);
        OrderActivatedCard forwardedOrder = jmsActivateCardServiceReceiver.receiveOrderActivateCardFromOrder();
        assertEquals(orderActivatedCard,forwardingOrder);
        assertEquals(orderActivatedCard,forwardedOrder);
        assertEquals(forwardingOrder,forwardedOrder);

    }

    private OrderActivatedCard getOrderActivatedCard() {
        Person person = new Person();
        person.setID(1);
        person.setFirstName("Ivan");
        person.setLastName("Ivanov");
        Card card = new Card();
        card.setID(1);
        card.setPerson(person);
        card.setLimit(10000);
        card.setStatus(StatusType.DISABLED);

        OrderActivatedCard orderActivatedCard = new OrderActivatedCard();
        orderActivatedCard.setID(1);
        orderActivatedCard.setCard(card);
        orderActivatedCard.setSolve(SolveType.YES);
        return orderActivatedCard;
    }

}