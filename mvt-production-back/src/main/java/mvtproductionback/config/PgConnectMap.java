package mvtproductionback.config;

import lombok.Data;
import mvtproductionback.entity.AddPgDTO;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/1/6
 **/

@Data
@Component
public class PgConnectMap {
    Map<String, Connection> pgMap=new HashMap<>();

    public Connection getConnect(String key){
        return pgMap.get(key);
    };

    public void setConnect(String key,Connection connection){
        pgMap.put(key,connection);
    }

    public void removeConnect(String key){
        pgMap.remove(key);
    }

    public boolean isExits(String key) {
        return pgMap.containsKey(key);
    }

    public boolean setConnect(String ip,String port,String dbName,String userName, String password){
        try{
            Class.forName("org.postgresql.Driver");
            Connection conn;
//            String connectUrl = "jdbc:postgresql://localhost:5432/shp_source_1?stringtype=unspecified";
            String connectUrl="jdbc:postgresql://"+ip+":"+port+"/"+dbName+"?stringtype=unspecified";
            conn = DriverManager.getConnection(connectUrl,userName, password);
            String key=ip+":"+port+"_"+dbName;
            pgMap.put(key,conn);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean setConnect(AddPgDTO addPgDTO){
        try{
            Class.forName("org.postgresql.Driver");
            Connection conn;
            String connectUrl="jdbc:postgresql://"+addPgDTO.getIp()+":"+addPgDTO.getPort()+"/"+addPgDTO.getPgName()+"?stringtype=unspecified";
            conn = DriverManager.getConnection(connectUrl,addPgDTO.getUserName(), addPgDTO.getPassword());

            String key =addPgDTO.getIp()+":"+addPgDTO.getPort()+"_"+addPgDTO.getPgName();
            pgMap.put(key,conn);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Object getConnectList(){
        return pgMap.keySet().toArray();
    }


}
