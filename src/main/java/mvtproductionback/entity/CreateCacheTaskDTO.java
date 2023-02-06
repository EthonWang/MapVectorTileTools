package mvtproductionback.entity;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author wyjq
 * @Date 2023/1/6
 */

@Data
public class CreateCacheTaskDTO {
    String name;
    String description="";
    String ip;
    String port;
    String pgName;
    List<CacheLayersDTO> layers;
//    List<JSONObject> vector_layers; //[{id:"",field:[],min:0,max:22,bounds:[-180,85,180,85]}]
}
