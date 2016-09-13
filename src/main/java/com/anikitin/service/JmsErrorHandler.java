package com.anikitin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

/**
 * Created by anikitin on 13.09.2016.
 */
@Service
public class JmsErrorHandler implements ErrorHandler {

    private static final Logger LOG= LoggerFactory.getLogger(JmsErrorHandler.class);
    @Override
    public void handleError(Throwable t) {
        LOG.error("Error in listener", t);
    }
}
