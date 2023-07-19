package mvtproductionback.kafka;

import lombok.extern.slf4j.Slf4j;
import mvtproductionback.entity.CreateCacheTaskDTO;
import mvtproductionback.entity.kafka.CacheTaskMessageProd;
import mvtproductionback.entity.kafka.CacheTaskMessageTest;
import mvtproductionback.entity.kafka.CacheTest;
import mvtproductionback.service.MvtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

    @KafkaListener(topics = CacheTaskMessageTest.TOPIC,groupId = "consumer-group-A-" + CacheTaskMessageTest.TOPIC)
    public void onMessage(CacheTaskMessageTest message) {
        CacheTest cacheTest = message.getCacheTest();
        log.info("--监听消息:消费者:group-A [线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
    }

    @KafkaListener(topics = CacheTaskMessageProd.TOPIC,groupId = "consumer-group-B-" + CacheTaskMessageProd.TOPIC)
    public void onMessage(CacheTaskMessageProd message) {
        CreateCacheTaskDTO cacheTaskDTO = message.getCacheTaskDTO();
        mvtService.createTileCache(cacheTaskDTO);
        log.info("--监听消息:消费者:group-A [线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
    }

//    @KafkaListener(topics = CacheTaskMessage.TOPIC,
//            groupId = "consumer-group-B-" + CacheTaskMessage.TOPIC)
//    public void onMessage2(CacheTaskMessage message) {
//        log.info("[onKafkaMessage-group-B][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
//    }

}
