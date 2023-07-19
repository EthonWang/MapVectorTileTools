package mvtproductionback.controller;

import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import mvtproductionback.entity.response.JsonResult;
import mvtproductionback.kafka.KafkaProducer;
import mvtproductionback.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @Description
 * @Author wyjq
 * @Date 2022/3/15
 */
@Slf4j
@CrossOrigin
@RestController
public class HelloController {

    @Autowired
    private KafkaProducer producer;


    @ApiOperation(value = "hello测试")
    @GetMapping("/hello")
    public JsonResult hello() {
        return ResponseResult.success("hello okkk");
    }

    @ApiOperation(value = "kafka测试")
    @GetMapping("/kafka-test/{msg}")
    public JsonResult hello1(@PathVariable("msg") String msg) throws ExecutionException, InterruptedException {
        int id = (int) (System.currentTimeMillis() / 1000);
        producer.cacheTaskProducerTest(id,"msg:"+msg);
        return ResponseResult.success("hello okkk");
    }

}
