package com.innominds.config;

import java.net.URI;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.innominds.receiver.JMSFileReceiver;
import com.innominds.receiver.JMSImageReceiver;

@Configuration
public class JMSConfig {

    @Bean
    public BrokerService brokerServiceFromFactory() throws Exception {
        final BrokerService service = BrokerFactory.createBroker( new URI( "broker:tcp://localhost:61888" ) );
        service.setBrokerName( "MyEmbeddedBroker" );
        service.setPersistent( false );
        // true requires kahaDB
        service.start();
        return service;
    }

    @Bean
    @DependsOn( "brokerServiceFromFactory" )
    public Connection connection() throws JMSException {
        final Connection connection = new ActiveMQConnectionFactory( "vm://MyEmbeddedBroker" ).createConnection();
        connection.start();
        return connection;
    }

    @Bean
    public JMSFileReceiver fileReceiver() {
        return new JMSFileReceiver();
    }

    @Bean
    public JMSImageReceiver imageReceiver() {
        return new JMSImageReceiver();
    }

}
