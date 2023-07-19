package mvtproductionback.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author wyjq
 * @Date 2023/1/6
 */

@Data
public class CacheLayersDTO {
    String shpName;
    List<String> field;
    int minzoom;
    int maxzoom;
    List<Double> bounds;
    List<Double> mercBounds; //墨卡托坐标，前端不需要
    List<JSONObject> shpAttrs;
//    List<JSONObject> vector_layers; //[{id:"",field:[],min:0,max:22,bounds:[-180,85,180,85]}]
}
