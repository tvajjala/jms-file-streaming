package com.innominds.sender;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class JMSFileSender implements InitializingBean {

    @Autowired
    Connection connection;

    private static final Logger LOG = LoggerFactory.getLogger( JMSFileSender.class );

    MessageProducer producer;
    Session session;

    @Override
    public void afterPropertiesSet() throws Exception {

        session = connection.createSession( true, Session.AUTO_ACKNOWLEDGE );
        final Queue queue = session.createQueue( "STREAMBOX" );

        producer = session.createProducer( queue );

        final String uid = UUID.randomUUID().toString();

        try ( InputStream stream = new ClassPathResource( "source.jpg" ).getInputStream() ) {

            sendStream( null, "BEGIN", uid );

            final byte[] bytes = new byte[10000];

            while ( stream.read( bytes ) >= 0 ) {
                sendStream( bytes, null, uid );
            }

            sendStream( null, "END", uid );
        }

        session.commit();
        producer.close();
        session.close();
    }

    public void sendStream( byte[] bytes, String marker, String uuid ) throws Exception {

        final BytesMessage msg = session.createBytesMessage();
        msg.setStringProperty( "JMSXGroupID", uuid );// will be received by same receiver
        // any message set by this JMSXGroupID will be received by same receiver
        if ( bytes == null ) {
            msg.setStringProperty( "sequenceMarker", marker );
            LOG.info( "Sending sequence marker {}", marker );
        } else {
            msg.writeBytes( bytes );
            LOG.info( "Streaming..." );
        }

        producer.send( msg );

    }

    @PostConstruct
    public void printMetadata() throws JMSException {

        LOG.info( "#################################  METADATA #################################" );

        final ConnectionMetaData metaData = connection.getMetaData();

        LOG.info( "Provider Version : " + metaData.getProviderVersion() );
        LOG.info( "Version : " + metaData.getJMSMajorVersion() + metaData.getJMSMinorVersion() );
        LOG.info( "ProviderName :  " + metaData.getJMSProviderName() );
        LOG.info( "PropertyNames : " );
        final Enumeration<?> e = metaData.getJMSXPropertyNames();

        while ( e.hasMoreElements() ) {
            LOG.info( "   >>>  " + e.nextElement() );
        }

        LOG.info( "#################################  METADATA #################################" );

    }

    @PreDestroy
    public void destroy() throws JMSException {
        connection.close();
    }
}
