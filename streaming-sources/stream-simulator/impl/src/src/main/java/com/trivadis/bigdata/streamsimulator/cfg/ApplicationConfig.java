package com.trivadis.bigdata.streamsimulator.cfg;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

import com.trivadis.bigdata.streamsimulator.input.InputSource;
import com.trivadis.bigdata.streamsimulator.input.csv.CsvSource;

/**
 * Common application configuration
 * 
 * @author mzehnder
 */
@Configuration
public class ApplicationConfig {

    @Autowired
    ApplicationProperties cfg;

    @Bean
    public MessageChannel inboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnProperty(name = "csv")
    public InputSource csvInputSource(@Value("${csv}") URI csvFileName) throws IOException {
        return new CsvSource(csvFileName, cfg.getSource().getCsv());
    }

}
