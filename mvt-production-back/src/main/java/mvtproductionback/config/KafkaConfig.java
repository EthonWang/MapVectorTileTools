package mvtproductionback.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/6/2
 **/

@Configuration
public class KafkaConfig {
    // 创建一个名为testtopic的Topic并设置分区数为8，分区副本数为2
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("CacheTask",3, (short) 1 );
    }

    @Bean
    public NewTopic initialTopic2() {
        return new NewTopic("CacheTest",3, (short) 2 );
    }

    @Bean
    public NewTopic initialTopic3() {
        return new NewTopic("CacheTaskProd",3, (short) 2 );
    }


    // 如果要修改分区数，只需修改配置值重启项目即可
    // 修改分区数并不会导致数据的丢失，但是分区数只能增大不能减小
//    @Bean
//    public NewTopic updateTopic() {
//        return new NewTopic("geo-topic2",5, (short) 1 );
//    }
}
