package br.com.devires.test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GcpPubSubEmulatorApplicationTests {

	@Autowired
	private PubSubAdmin pubSubAdmin;

	@Autowired
	private PubSubTemplate pubSubTemplate;
	
	private static Subscriber subscriber;

	@Before
	public void createSubscription() {
		if (subscriber == null) {
			try {
				Subscription subscription = pubSubAdmin.createSubscription("MyTestSubscription", "MyTestTopic");
				log.info("Subscription created: " + subscription.getName());
			} catch (AlreadyExistsException e) {
				log.info("Subscription already exists, skipping");
			}
		}
	}
	
	@Before
	public void subscribe() {
		if (subscriber == null) {
			subscriber = pubSubTemplate.subscribe("MyTestSubscription", (pubSubMessage) -> {
				PubsubMessage message = pubSubMessage.getPubsubMessage();
				log.info("Message Received: " + message.getData().toStringUtf8());
				pubSubMessage.ack();
			});
			subscriber.awaitRunning();
		}
	}
	
	@Test
	public void listTopics() {
		List<String> topics = pubSubAdmin.listTopics().stream().map(Topic::getName).collect(Collectors.toList());
		topics.forEach((item) -> {
			log.info(item);
		});
	}

	@Test
	public void publish() throws InterruptedException, ExecutionException {	
		String messageId = pubSubTemplate.publish("MyTestTopic", "Hello Devires").get();
		log.info("Message sent: " + messageId);
	}
	
	@AfterClass
	public static void after() throws InterruptedException {
		Thread.sleep(1000);
		if (subscriber != null) {
			subscriber.stopAsync();
		}
	}

}
