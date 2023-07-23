package mvtproductionback.kafka;

import lombok.extern.slf4j.Slf4j;
import mvtproductionback.entity.CreateCacheTaskDTO;
import mvtproductionback.entity.kafka.CacheTaskMessageProd;
import mvtproductionback.entity.kafka.CacheTaskMessageTest;
import mvtproductionback.entity.kafka.CacheTest;
import mvtproductionback.service.MvtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/7/13
 **/
@Slf4j
@Component
public class KafkaConsumer {

    @Autowired
    MvtService mvtService;

    @Resource
    private KafkaListenerEndpointRegistry registry;

    int numThreads = 5;
    int maxNumTasks = 10;

    @Autowired
    CustomThreadPool customThreadPool;

//    @KafkaListener(topics = CacheTaskMessageTest.TOPIC,groupId = "consumer-group-A-" + CacheTaskMessageTest.TOPIC)

    @KafkaListener(id = "consumer0",groupId = "group-A",topicPartitions ={
            @TopicPartition(topic = CacheTaskMessageTest.TOPIC,partitions = "0")
    })
    public void onMessage0(CacheTaskMessageProd message) {

        customThreadPool.submitTask(()->{
            System.out.println("Task  is running on thread " + Thread.currentThread().getName());
            CreateCacheTaskDTO cacheTaskDTO = message.getCacheTaskDTO();
            log.info("--监听消息:消费者:consumer0 [线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
            mvtService.createTileCache(cacheTaskDTO);
            registry.getListenerContainer("consumer0").resume();
        });

//        CreateCacheTaskDTO cacheTaskDTO = message.getCacheTaskDTO();
//        log.info("--监听消息:消费者:consumer0 [线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
//        registry.getListenerContainer("consumer0").pause();
//        mvtService.createTileCache(cacheTaskDTO);
//        registry.getListenerContainer("consumer0").resume();
    }

    @KafkaListener(id = "consumer1",groupId = "group-A",topicPartitions ={
            @TopicPartition(topic = CacheTaskMessageTest.TOPIC,partitions = "1")
    })
    @KafkaListener(topics = CacheTaskMessageProd.TOPIC,groupId = "consumer-group-B-" + CacheTaskMessageProd.TOPIC)
    public void onMessage1(CacheTaskMessageProd message) {
        customThreadPool.submitTask(()->{
            System.out.println("Task  is running on thread " + Thread.currentThread().getName());
            CreateCacheTaskDTO cacheTaskDTO = message.getCacheTaskDTO();
            log.info("--监听消息:消费者:consumer1 [线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
            mvtService.createTileCache(cacheTaskDTO);
            registry.getListenerContainer("consumer1").resume();
        });


//        CreateCacheTaskDTO cacheTaskDTO = message.getCacheTaskDTO();
//        log.info("--监听消息:消费者:consumer1 [线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
//        registry.getListenerContainer("consumer0").pause();
//        mvtService.createTileCache(cacheTaskDTO);
//        registry.getListenerContainer("consumer0").resume();
    }


}
