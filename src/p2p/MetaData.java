package p2p;

import java.util.Enumeration;
import javax.jms.ConnectionMetaData;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;


public class MetaData {
	public static void main(String[] args) {
		try {
			Context ctx = new InitialContext();
			QueueConnectionFactory qFactory = (QueueConnectionFactory) ctx.lookup("QueueCF");
			QueueConnection qConnect = qFactory.createQueueConnection();
			ConnectionMetaData metadata = qConnect.getMetaData();
			System.out.println("JMS Version: " + metadata.getJMSMajorVersion() + "." + metadata.getJMSMinorVersion());
			System.out.println("JMS Provider: " + metadata.getJMSProviderName());
			System.out.println("JMSX Properties Supported: ");
			Enumeration e = metadata.getJMSXPropertyNames();
			while (e.hasMoreElements()) {
				System.out.println(" " + e.nextElement());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}