package com.anikitin.service;

import generated.OrderActivatedCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MarshallingMessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Service;

import javax.jms.*;


/**
 * Created by baldessarinii on 09.09.16.
 */
@Service
public class JmsLowLevelCodeActivateServiceReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(JmsLowLevelCodeActivateServiceReceiver.class);
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private Topic topicDurable;
    @Autowired
    private MarshallingMessageConverter marshaller;
    @Autowired
    private StoreMessageFromTopic storeMessageFromTopic;
    private TopicSession session;
    private TopicConnection connection;
    private TopicSubscriber topicSubscriber;

    public void openConsumerDurableSubscribe() {
        try {
            connection = (TopicConnection) connectionFactory.createConnection();
            connection.setClientID("topic.durable.id");
            connection.start();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topicSubscriber = session.createDurableSubscriber(topicDurable, "subName");
            topicSubscriber.setMessageListener(message -> {
                OrderActivatedCard orderActivatedCard=null;
                try {
                    orderActivatedCard = (OrderActivatedCard) marshaller.fromMessage(message);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
               LOG.info("Received " + orderActivatedCard);
                storeMessageFromTopic.add(orderActivatedCard);
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void stopConsumer() throws JMSException {
        if (connection != null) {
            topicSubscriber.close();
            session.close();
            connection.close();
        }
    }
}
