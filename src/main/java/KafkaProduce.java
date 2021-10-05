import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProduce {
    public static void main(String[] args) {
        String topicName = "ktopic";
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        try {
            FileInputStream fis = new FileInputStream("/home/edureka/Desktop/products.csv");
            Scanner sc = new Scanner(fis);
            while (sc.hasNextLine()) {
/*            producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(i), sc.nextLine()));
            System.out.println(Integer.toString(i)+sc.nextLine());
            i++;*/
                int k=0;
                String sline = "";
                String[] mydata = sc.nextLine().split(",");
                String[] header = new String[]{"PogId", "Supc", "Brand", "Description", "Size", "Category", "SubCategory", "Price", "Quantity", "Country", "SellerCode", "creationtime", "stock"};
                for (int i=0, j=0; i<header.length && j< header.length; i++, j++) {
                    if(i==0 && j==0){
                        sline+="{";
                    }
                    if(j != mydata.length-1) {
                        sline += "\"" + header[i] + "\":\"" + mydata[j] + "\",";
                    }
                    if(j == mydata.length-1){
                        sline += "\"" + header[i] + "\":\"" + mydata[j] + "\"";
                    }
                    if( i == header.length-1 && j == mydata.length-1 ){
                        sline+="}";
                    }
                }
                sline+="}";
//                System.out.println(sline);
                producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(k), sline));
            }
            System.out.println("Message sent successfully");
            producer.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
            System.out.println(e);
        }
    }
}