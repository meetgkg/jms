package p2p;

import java.util.Enumeration;

import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class LoanRequestQueueBrowser {
	public static void main(String[] args) {
		try {
			// establish connection
			Context context = new InitialContext();
			QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("QueueCF");
			QueueConnection connection = factory.createQueueConnection();
			connection.start();
			// establish session
			Queue queue = (Queue) context.lookup("LoanRequestQ");
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueBrowser browser = session.createBrowser(queue);
			Enumeration e = browser.getEnumeration();
			while (e.hasMoreElements()) {
				TextMessage msg = (TextMessage) e.nextElement();
				System.out.println("Browsing: " + msg.getText());
			}
			browser.close();
			connection.close();
			System.exit(0);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}