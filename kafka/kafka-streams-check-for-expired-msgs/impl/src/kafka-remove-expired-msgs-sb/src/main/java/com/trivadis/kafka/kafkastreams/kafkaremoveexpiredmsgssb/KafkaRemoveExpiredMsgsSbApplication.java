package com.trivadis.kafka.kafkastreams.kafkaremoveexpiredmsgssb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class KafkaRemoveExpiredMsgsSbApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaRemoveExpiredMsgsSbApplication.class, args);
	}

    @Autowired
    private KafkaRemoveExpiredMsgsStream kafkaStreamExample;
	
}

