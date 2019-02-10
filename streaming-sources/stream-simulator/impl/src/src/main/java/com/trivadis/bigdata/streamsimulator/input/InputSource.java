package com.trivadis.bigdata.streamsimulator.input;

import java.io.Closeable;
import java.util.Map;

/**
 * Common interface for an input source
 * 
 * TODO don't limit to Map type, should be at least a JSON object or something
 * generic
 * 
 * @author mzehnder
 */
public interface InputSource extends Closeable, Iterable<Map<String, String>> {

}
