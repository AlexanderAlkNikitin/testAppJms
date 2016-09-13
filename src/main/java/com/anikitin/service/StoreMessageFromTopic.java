package com.anikitin.service;

import generated.OrderActivatedCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by anikitin on 13.09.2016.
 */
@Component
public class StoreMessageFromTopic {

    private static final Logger LOG = LoggerFactory.getLogger(StoreMessageFromTopic.class);

    private Set<OrderActivatedCard> orderActivatedCardArrayList = new HashSet<>();


    public void add(OrderActivatedCard orderActivatedCard) {
        orderActivatedCardArrayList.add(orderActivatedCard);
        LOG.info("Added object to store " + orderActivatedCard);
    }

    public int getSize() {
        return orderActivatedCardArrayList.size();
    }

    public OrderActivatedCard get() {
        return orderActivatedCardArrayList.size() == 0 ? null : orderActivatedCardArrayList.stream().findAny().get();
    }

    public void clear() {
        orderActivatedCardArrayList.clear();
        LOG.info("Clear store");
    }
}
