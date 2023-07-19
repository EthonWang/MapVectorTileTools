package mvtproductionback.kafka;

import lombok.extern.slf4j.Slf4j;
import mvtproductionback.entity.CreateCacheTaskDTO;
import mvtproductionback.entity.kafka.CacheTaskMessageProd;
import mvtproductionback.entity.kafka.CacheTaskMessageTest;
import mvtproductionback.entity.kafka.CacheTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/7/13
 **/
@Slf4j
@Component
public class KafkaProducer {
    @Resource
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public void syncSend(Integer id,String msg) throws ExecutionException, InterruptedException {
        // 创建 Demo01Message 消息
        CacheTaskMessageTest message = new CacheTaskMessageTest();
        message.setId(id);
        message.setMsg(msg);
        // 同步发送消息
        kafkaTemplate.send(CacheTaskMessageTest.TOPIC, message);
    }

    public void cacheTaskProducerTest(Integer id,String msg){
        CacheTaskMessageTest message = new CacheTaskMessageTest();
        message.setId(id);
        message.setMsg(msg);
        CacheTest cacheTest=new CacheTest("newcache","cachetest");
        message.setCacheTest(cacheTest);

        kafkaTemplate.send(CacheTaskMessageTest.TOPIC, message).addCallback(success -> {
            // 消息发送到的topic
            String topic = success.getRecordMetadata().topic();
            // 消息发送到的分区
            int partition = success.getRecordMetadata().partition();
            // 消息在分区内的offset
            long offset = success.getRecordMetadata().offset();
            log.info("--发送消息成功:" + topic + "-" + partition + "-" + offset);
        }, failure -> {
            log.error("--发送消息失败:" + failure.getMessage());
        });
    }

    public void cacheTaskProducerProd(Integer id, String msg, CreateCacheTaskDTO cacheTaskDTO){
        CacheTaskMessageProd message = new CacheTaskMessageProd();
        message.setId(id);
        message.setMsg(msg);
        message.setCacheTaskDTO(cacheTaskDTO);

        kafkaTemplate.send(CacheTaskMessageTest.TOPIC, message).addCallback(success -> {
            String topic = success.getRecordMetadata().topic();
            int partition = success.getRecordMetadata().partition();
            long offset = success.getRecordMetadata().offset();
            log.info("--发送消息成功:" + topic + "-" + partition + "-" + offset);
        }, failure -> {
            log.error("--发送消息失败:" + failure.getMessage());
        });
    }
}
