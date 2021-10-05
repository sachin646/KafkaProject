import java.util.Properties;
import java.util.Collections;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.TreeMap;
import java.net.InetSocketAddress;

import com.datastax.oss.driver.api.core.CqlSession;

public class KafkaCassandraCon {
    public static void main(String[] args) {
        String topicName = "ktopic";
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("buffer.memory", 33554432);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "groupx");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));
        consumer.poll(0);
        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(10);
            consumerRecords.forEach(record -> {
                //System.out.printf("Consumer Record:(%s, %s)\n", record.key(), record.value());
                TreeMap<String, String> map = new TreeMap<String, String>();
                String line = record.value();
                line = line.substring(1, line.length()-1);
                String[] keyValuePairs = line.split(",");
                for(String pair : keyValuePairs){
                    String[] entry = pair.split(":");
                    map.put(entry[0].trim(), entry[1].trim());
                }
                String contactPoint = "127.0.0.1";
                int port = 9042;
                String keySpace = "kprj";
                String dataCenter = "datacenter1";
                try (CqlSession session = CqlSession.builder().addContactPoint(new InetSocketAddress(contactPoint, port))
                        .withLocalDatacenter(dataCenter).withKeyspace(keySpace).build()){
                    String query= "INSERT INTO kprj.productsc (PogId,Supc,Brand,Description,Size,Category,Sub_Category,Country,Seller_Code) VALUES("+map.get("\"PogId\"")+","+
                            map.get("\"Supc\"")+","+
                            map.get("\"Brand\"")+","+
                            map.get("\"Description\"")+","+
                            map.get("\"Size\"")+","+
                            map.get("\"Category\"")+","+
                            map.get("\"SubCategory\"")+","+
                            map.get("\"Country\"")+","+
                            map.get("\"SellerCode\"")+");";
                    System.out.println(map);
                    System.out.println(map.get("\"seller_code\""));
                    query=query.replace("\"","\'");
                    System.out.println(query);
                    session.execute(query);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }
    }
}
