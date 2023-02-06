package mvtproductionback.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Auther wyjq
 * @Date 2022/12/31
 **/

@Data
@Component
public class SqliteConnectMap {
    Map<String, Connection> sqliteMap=new HashMap<>();

    public Connection getConnect(String key){
        return sqliteMap.get(key);
    };

    public void setConnect(String key,Connection connection){
        sqliteMap.put(key,connection);
    }

    public boolean isExits(String key) {
        return sqliteMap.containsKey(key);
    }

    public boolean setConnect(String key,String path) {
        try{
            Connection conn;
            String connectUrl = "jdbc:sqlite:"+path;
            conn = DriverManager.getConnection(connectUrl,null,null);
            conn.setAutoCommit(false);
            sqliteMap.put(key,conn);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void removeConnect(String key){
        sqliteMap.remove(key);
    }


    public List getConnectMapInfo() throws SQLException {

        JSONArray list=new JSONArray();
        for (Map.Entry<String, Connection> entry : sqliteMap.entrySet()) {
            JSONObject connectMapInfo=new JSONObject();
            String key=entry.getKey();
            String url=entry.getValue().getMetaData().getURL();
            connectMapInfo.put("name",key);
            connectMapInfo.put("url",url);
            list.add(connectMapInfo);
        }
        return list;
    }

}
