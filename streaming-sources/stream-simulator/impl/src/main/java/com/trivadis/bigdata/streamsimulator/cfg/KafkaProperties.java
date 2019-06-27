package com.trivadis.bigdata.streamsimulator.cfg;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

/**
 * Kafka specific configuration properties
 * 
 * @author Markus Zehnder
 */
@Component
@ConfigurationProperties(prefix = "simulator.kafka")
@Validated
@Getter
@Setter
public class KafkaProperties {

    private Avro avro = new Avro();

    @Validated
    @Getter
    @Setter
    public static class Avro {
        private boolean enabled;
        private boolean schemaRegistry = true;
        private List<String> schemaRegistryUrls = Arrays.asList("http://localhost:8081");
        private int identityMapCapacity = 100;
    }

}
