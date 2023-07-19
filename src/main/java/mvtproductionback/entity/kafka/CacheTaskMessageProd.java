package mvtproductionback.entity.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mvtproductionback.entity.CreateCacheTaskDTO;

/**
 * 示例 01 的 Message 消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheTaskMessageProd {

    public static final String TOPIC = "CacheTaskProd";

    private Integer id;
    private String msg;
    CreateCacheTaskDTO cacheTaskDTO;

}
