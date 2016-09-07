package com.anikitin.service;

import generated.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.Destination;

import static org.junit.Assert.assertEquals;

/**
 * Created by anikitin on 07.09.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springJms-context.xml")
public class JmsActivateCardServiceReceiverTest {

    @Autowired
    private JmsActivateCardServiceSender jmsActivateCardServiceSender;
    @Autowired
    private JmsActivateCardServiceReceiver jmsActivateCardServiceReceiver;

    @Autowired
    private Destination optionalDestination;

    private OrderActivatedCard orderActivatedCard;

    @Before
    public void setUp() throws Exception {
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
        jmsActivateCardServiceSender.sendObjectXmlToQueue(orderActivatedCard);

    }

    @Test
    public void receiveXml() throws Exception {
        OrderActivatedCard fromQueue = jmsActivateCardServiceReceiver.receiveOrderActivateCard();
        assertEquals(fromQueue, orderActivatedCard);

    }

    @Test
    public void sendAfterReceive() throws Exception {

        OrderActivatedCard forwardingOrder = jmsActivateCardServiceReceiver.sendAfterReceive();
        OrderActivatedCard forwardedOrder = jmsActivateCardServiceReceiver.receiveOrderActivateCard(optionalDestination);

        assertEquals(orderActivatedCard,forwardingOrder);
        assertEquals(orderActivatedCard,forwardedOrder);
        assertEquals(forwardingOrder,forwardedOrder);

    }

}