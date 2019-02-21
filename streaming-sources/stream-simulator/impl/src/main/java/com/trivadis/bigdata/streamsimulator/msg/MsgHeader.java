package com.trivadis.bigdata.streamsimulator.msg;

/**
 * Message header constants.
 *
 * @author mzehnder
 */
public abstract class MsgHeader {

    public final static String ORIGINAL_EVENT_TIME = "sim.originalEventTime";
    public final static String TIME_TO_SEND = "sim.timeToSend";

    /**
     * Delay message by given amount in milliseconds, or if a <code>java.util.Date</code> instance is set, by an
     * absolute time.
     */
    public final static String DELAY = "delay";
}
