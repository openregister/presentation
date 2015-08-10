package uk.gov.register.presentation.app;

import com.google.common.collect.ImmutableMap;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import uk.gov.register.presentation.config.ZookeeperConfiguration;
import uk.gov.register.presentation.dao.PGObjectFactory;
import uk.gov.register.presentation.dao.RecentEntryIndexUpdateDAO;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConsumerRunnable implements Runnable {

    private final Properties properties;
    private final String topicName;
    private final RecentEntryIndexUpdateDAO updateDAO;

    public ConsumerRunnable(ZookeeperConfiguration zkConfig, String topicName, RecentEntryIndexUpdateDAO updateDAO) {
        this.topicName = topicName;
        this.updateDAO = updateDAO;
        properties = new Properties();
        properties.put("zookeeper.connect", zkConfig.getZookeeperServer());
        properties.put("zookeeper.session.timeout.ms", "3000");
        properties.put("zookeeper.sync.time.ms", "200");
        properties.put("group.id", "debug"); // should be unique to this presentation app
        properties.put("enable.auto.commit", "false");
        properties.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
        properties.put("value.deserializer", org.apache.kafka.common.serialization.ByteArrayDeserializer.class);
        properties.put("partition.assignment.strategy", "range");

        updateDAO.ensureTableExists();
    }

    @Override
    public void run() {
        ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        Map<String, List<KafkaStream<String, byte[]>>> messageStreams = consumerConnector.createMessageStreams(ImmutableMap.of(topicName, 1), keyDecoder, bytes -> bytes);
        KafkaStream<String, byte[]> kafkaStream = messageStreams.get(topicName).get(0);
        for (MessageAndMetadata<String, byte[]> messageAndMetadata : kafkaStream) {
            byte[] message = messageAndMetadata.message();
            //TODO: check can we directly get getBytes into string
            updateDAO.append(PGObjectFactory.jsonbObject(new String(message, Charset.forName("UTF-8"))));
        }
    }

}
