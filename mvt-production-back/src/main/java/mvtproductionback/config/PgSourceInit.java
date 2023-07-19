package mvtproductionback.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author wyjq
 * @Date 2023/1/6
 */

@Slf4j
@Component
public class PgSourceInit implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    PgConnectMap pgConnectMap;



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            pgConnectMap.setConnect("localhost","5432",
                    "MVTDB1","postgres","123456");
            log.info("---添加pg源: localhost-5432-MVTDB1 成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("---添加pg源: localhost-5432-MVTDB1 失败");
        }
    }
}




