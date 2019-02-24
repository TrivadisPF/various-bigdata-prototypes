package com.trivadis.bigdata.streamsimulator.transform;

import java.util.Map;

/**
 * Service interface to derive headers from a given payload.
 * 
 * @author Markus Zehnder
 *
 * @param <T> payload type
 */
public interface HeaderProvider<T> {

    /**
     * Get headers for the given payload.
     * 
     * @param payload the payload of the current message
     * @return the headers to add to the message or null if none
     */
    Map<String, ?> getHeaders(T payload);
}
