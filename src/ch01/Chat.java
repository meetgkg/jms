package ch01;

import java.io.*;
import javax.jms.*;
import javax.naming.*;

import org.apache.activemq.jndi.ActiveMQInitialContextFactory;

/*
 * The chat client creates a JMS publisher and subscriber for a specific topic. 
 * The topic represents the chat room. 
 * The JMS server registers all the JMS clients that want to publish or subscribe to a specific topic. 
 * When text is entered at the command line of one of the chat clients, it is published to the messaging server.
 * The messaging server identifies the topic associated with the publisher and delivers the message
 * to all the JMS clients that have subscribed to that topic. 
 * As Figure 2-1 illustrates, 
 * messages published by any one of the JMS clients are delivered to all the JMS subscribers for that topic.
 */
public class Chat implements javax.jms.MessageListener {
	private TopicSession pubSession;
	private TopicPublisher publisher;
	private TopicConnection connection;
	private String username;

	/* Constructor used to Initialize Chat */
	//topicFactory => TopicCF 
	//topicName => topic1
	//username => Fred/Wilma
	public Chat(String topicFactory, String topicName, String username) throws Exception {

		// Obtain a JNDI connection using the jndi.properties file
		InitialContext ctx = new InitialContext();
		
		System.out.println(ctx.getClass());
		
		// Look up a JMS connection factory and create the connection
		TopicConnectionFactory conFactory = (TopicConnectionFactory)ctx.lookup(topicFactory);

		TopicConnection connection = conFactory.createTopicConnection();

		//Create two JMS session objects
		TopicSession pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		// Look up a JMS topic
		Topic chatTopic = (Topic)ctx.lookup(topicName);

		// Create a JMS publisher and subscriber. The additional parameters
		// on the createSubscriber are a message selector (null) and a true
		// value for the noLocal flag indicating that messages produced from
		// this publisher should not be consumed by this publisher.
		TopicPublisher publisher = pubSession.createPublisher(chatTopic);
		TopicSubscriber subscriber = subSession.createSubscriber(chatTopic, null, true);
		// Set a JMS message listener
		subscriber.setMessageListener(this);
		// Intialize the Chat application variables
		this.connection = connection;
		this.pubSession = pubSession;
		this.publisher = publisher;
		this.username = username;
		// Start the JMS connection; allows messages to be delivered
		connection.start();
	}

	/* Receive Messages From Topic Subscriber */
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			System.out.println(textMessage.getText());
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
	}

	/* Create and Send Message Using Publisher */
	protected void writeMessage(String text) throws JMSException {
		TextMessage message = pubSession.createTextMessage();
		message.setText(username + ": " + text);
		publisher.publish(message);
	}

	/* Close the JMS Connection */
	public void close() throws JMSException {
		connection.close();
	}

	/* Run the Chat Client */
	public static void main(String[] args) {
		try {
			if (args.length != 3)
				System.out.println("Factory, Topic, or username missing");
			// args[0]=topicFactory; args[1]=topicName; args[2]=username
			Chat chat = new Chat(args[0], args[1], args[2]);
			// Read from command line
			BufferedReader commandLine = new java.io.BufferedReader(
					new InputStreamReader(System.in));
			// Loop until the word "exit" is typed
			while (true) {
				String s = commandLine.readLine();
				if (s.equalsIgnoreCase("exit")) {
					chat.close();
					System.exit(0);
				} else
					chat.writeMessage(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}