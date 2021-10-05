import java.util.Properties;
import java.util.Collections;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.sql.*;
import java.util.TreeMap;

public class KafkaSqlConsumer {
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
//                System.out.println(map.get("\"PogId\""));

                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/kafkaprj","edureka","edureka");
                    Statement stmt=con.createStatement();
                    String query= "INSERT INTO kafkaprj.products VALUES("+map.get("\"PogId\"")+","+map.get("\"Supc\"")+","+map.get("\"Price\"")+","+map.get("\"Quantity\"")+");";
                    System.out.println(query);
                    int rs=stmt.executeUpdate(query);
                    System.out.println(rs);
                }
                catch(Exception e){
                    System.out.println(e);
                }
            });
        }
    }
}
