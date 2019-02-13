package com.trivadis.bigdata.streamsimulator.input;

import java.io.Closeable;
import java.util.Map;

/**
 * Common interface for an input source
 * 
 * FIXME use Spring Integration Message concept
 * 
 * @author mzehnder
 */
public interface InputSource extends Closeable, Iterable<Map<String, String>> {

}
