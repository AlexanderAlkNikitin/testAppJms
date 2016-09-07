package com.anikitin.service;

import generated.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by anikitin on 07.09.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springJms-context.xml")
public class JmsActivateCardServiceSenderTest {

    @Autowired
    private JmsActivateCardServiceSender jmsActivateCardServiceSender;
    @Autowired
    private JmsActivateCardServiceReceiver jmsActivateCardServiceReceiver;

    @org.junit.Test
    public void sendObjectXml() throws Exception {
        OrderActivatedCard orderActivatedCard = getOrderActivatedCard();
        jmsActivateCardServiceSender.sendObjectXmlToQueue(orderActivatedCard);
        OrderActivatedCard fromQueue = jmsActivateCardServiceReceiver.receiveOrderActivateCard();
        assertEquals(fromQueue, orderActivatedCard);
    }

    @Test
    public void sendObjectXmlToTopic() throws Exception {
        OrderActivatedCard orderActivatedCard = getOrderActivatedCard();
        jmsActivateCardServiceSender.sendObjectXmlToTopic(orderActivatedCard);

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