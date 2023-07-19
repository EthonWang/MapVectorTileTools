package mvtproductionback;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import mvtproductionback.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@Slf4j
@SpringBootTest
class MvtProductionBackApplicationTests {

    @Value("${dataStorePath}")
    String dataStorePath;

    @Test
    void contextLoads() {
        try {
            String createMetadataTableSql = "CREATE TABLE metadata (" +
                    "   name text," +
                    "   value text" +
                    ");";

            Connection conn;
            String connectUrl = "jdbc:sqlite:C:\\Users\\OpenGMS\\Desktop\\wyj\\mvt\\mbtiles\\cache_test.mbtiles";
            conn = DriverManager.getConnection(connectUrl,null,null);
            conn.setAutoCommit(false);

            Statement state = conn.createStatement();
            state.execute(createMetadataTableSql);
            conn.commit();
            state.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void t1(){

        int[] nums= new int[]{-1,0,1,2,-1,-4};
        int target=7;
        int i=0,j=0;
        int sum=0;
        int minLength=Integer.MAX_VALUE;
        while(j<nums.length){
            sum+=nums[j];
            if(sum>=target){
                minLength=Math.min(j-i+1,minLength);
                i++;
            }else{
                j++;
            }
        }
    }

    void find(int[] nums,int i,List<List<Integer>> result){
        int j=i+1,k=nums.length-1;

        while(j<k){
            int tmp=nums[i]+nums[j]+nums[k];
            if(tmp==0){
                List<Integer> arr= Arrays.asList(nums[i],nums[j],nums[k]);
                result.add(arr);
                j++;
                while(j<k&&nums[j]==nums[j-1]){
                    j++;
                }
                k--;
                while(j<k&&nums[k]==nums[k+1]){
                    k--;
                }
            }
            if(tmp<0){
                j++;
            }else{
                k--;
            }
        }


    }

    @Test
    void t2(){
        int a=3;
        int b=a/2;

    }

    @Autowired
    private KafkaProducer producer;

    @Test
    public void testSyncSend() throws ExecutionException, InterruptedException {
        int id = (int) (System.currentTimeMillis() / 1000);
//        SendResult result = producer.syncSend(id,"msmsmsmsm11111");
//        log.info("[testSyncSend][发送编号：[{}] 发送结果：[{}]]", id, result);

        // 阻塞等待，保证消费
//        new CountDownLatch(1).await();
    }

}
