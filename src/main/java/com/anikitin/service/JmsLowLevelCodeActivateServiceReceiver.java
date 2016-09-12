package com.anikitin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.*;


/**
 * Created by baldessarinii on 09.09.16.
 */
@Service
public class JmsLowLevelCodeActivateServiceReceiver {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private Topic topicDurable;

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
            topicSubscriber.setMessageListener(message -> System.out.println("Received " + message.toString()));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void stopConsumer() throws JMSException {
        if (connection != null) {
            topicSubscriber.close();
//            topicSubscriber=null;
            session.close();
//            session=null;
            connection.close();
//            connection=null;
        }
    }
}
