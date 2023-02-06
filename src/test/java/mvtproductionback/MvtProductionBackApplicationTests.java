package mvtproductionback;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.MessageFormat;

@SpringBootTest
class MvtProductionBackApplicationTests {

    @Value("${dataStorePath}")
    String dataStorePath;



    @Test
    void contextLoads() throws IOException {
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
        String a="11";
        String str= MessageFormat.format("aaa {0}",a);
        System.out.println(str);
    }

    @Test
    void t2(){
        double a=-0.617;
        int b =(int) Math.floor(a);
        System.out.println(b);
    }

}
