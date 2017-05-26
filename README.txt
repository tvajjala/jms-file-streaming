This application demonstrates
1. Usage of EmbeddedBroker
2. Enabling /Disabling KahaDB service.setPersistent( false ); Refer JMSConfig
3. Sending Stream data
4. Significance of JMSXGroupID in stream data processing . if multiple receivers exists then with this same receiver will receive all the stream. (unable to reproduce error sometime)
5. Usage of vm:// protocol. can be used within the same JVM
6. Usage of Transaction session in JMS-1.1
