package com.innominds.receiver;

import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class JMSFileReceiver implements MessageListener, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( JMSFileReceiver.class );

    private OutputStream stream = null;

    @Override
    public void onMessage( Message message ) {
        try {
            if ( message.propertyExists( "sequenceMarker" ) ) {
                final String marker = message.getStringProperty( "sequenceMarker" );

                if ( marker.equals( "BEGIN" ) ) {
                    stream = new FileOutputStream( "Received.jpg" );
                }

                if ( marker.equals( "END" ) ) {
                    stream.close();
                    Runtime.getRuntime().exec( "open -a Preview Received.jpg" );
                }

            } else {
                final BytesMessage byteMsg = (BytesMessage) message;
                final byte[] value = new byte[new Long( byteMsg.getBodyLength() ).intValue()];
                byteMsg.readBytes( value );
                stream.write( value );
                LOG.info( "Received byte stream ...." );
            }

        } catch ( final Exception e ) {
            LOG.error( "Error {} ", e.getMessage() );
        }

    }

    @Autowired
    Connection connection;

    @Override
    public void afterPropertiesSet() throws Exception {

        final Session session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
        final Queue queue = session.createQueue( "STREAMBOX" );
        final MessageConsumer consumer = session.createConsumer( queue );
        consumer.setMessageListener( this );
    }

}
