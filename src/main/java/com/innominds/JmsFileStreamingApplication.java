package com.innominds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.innominds.config.JMSConfig;

//@formatter:off

/**
 *  This application demonstrates
 *  1. Usage of EmbeddedBroker
 *  2. Enabling /Disabling KahaDB    service.setPersistent( false ); Refer {@link JMSConfig}
 *  3. Sending Stream data
 *  4. Significance of JMSXGroupID in stream data processing . if multiple receivers exists then with this same receiver will receive all the stream.
 *      (unable to reproduce error sometime)
 *  5. Usage of vm:// protocol.  can be used within the same JVM
 *  6. Usage of Transaction session JMS-1.1
 *
 * @author ThirupathiReddy V
 */

//@formatter:on
@SpringBootApplication
public class JmsFileStreamingApplication {

    public static void main( String[] args ) {
        SpringApplication.run( JmsFileStreamingApplication.class, args );
    }
}
