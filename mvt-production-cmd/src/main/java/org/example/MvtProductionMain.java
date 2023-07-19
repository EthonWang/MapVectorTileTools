package org.example;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import org.example.entity.CreateCacheTaskDTO;
import org.example.service.MvtService;
import java.io.File;
import java.text.DecimalFormat;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/2/13
 **/
public class MvtProductionMain {
    public static void main(String[] args) {
        if(!(args.length==1||args.length==2)){
            System.out.println("! error arguments ! ");
            System.out.println("第一个参数：配置文件路径");
            System.out.println("第二个参数：输出mbtiles的路径（默认当前）");
            return;
        }
        System.out.println("--- start ---");
        System.out.println("--config file path: "+args[0]);
        String outputPath="./output_"+System.currentTimeMillis() +".mbtiles";
        if (args.length==1){
            System.out.println("--output file path(default): ./");
        }else {
            System.out.println("--output file path: "+args[1]);
            outputPath=args[1];
        }

        File file = new File(args[0]);
        FileReader fileReader = new FileReader(file);
        JSONObject taskJson = JSONObject.parseObject(fileReader.readString());
        CreateCacheTaskDTO taskDTO= new CreateCacheTaskDTO();
        BeanUtil.copyProperties(taskJson,taskDTO);
//        System.out.println(taskJson.toJSONString());

        long start_time = System.currentTimeMillis();
        MvtService.createTileCache(taskDTO,outputPath);
        long end_time=System.currentTimeMillis();
        long duration=end_time-start_time;
        DecimalFormat df = new DecimalFormat("0.000");
        if(duration<60*1000){
            System.out.println("--任务耗时："+ df.format(duration/1000.0)+"s");
        }else{
            System.out.println("--任务耗时："+ df.format(duration/1000.0/60.0)+"min");
        }
        System.out.println("--- end ---");
    }
}
