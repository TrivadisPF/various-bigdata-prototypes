package com.trivadis.init.publish;

public interface EventPublisher {

	public void send(String channelName, String message);
}
