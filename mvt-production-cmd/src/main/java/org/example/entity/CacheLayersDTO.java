package org.example.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/2/13
 **/

@Data
public class CacheLayersDTO {
    String shpName;
    List<String> field;
    int minzoom;
    int maxzoom;
    List<Double> bounds;
    List<Double> mercBounds; //墨卡托坐标，前端不需要
    List<JSONObject> shpAttrs;
}
