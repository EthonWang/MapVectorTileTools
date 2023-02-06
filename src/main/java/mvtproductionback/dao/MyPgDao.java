package mvtproductionback.dao;


import mvtproductionback.config.PgConnectMap;
import mvtproductionback.config.SqliteConnectMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class MyPgDao {

    @Autowired
    PgConnectMap pgConnectMap;

    public byte[] getMvtFromPgSource( String ip, String port,String pgName, String sql) {
        try{
            byte[] reByte =null;
            Connection con = pgConnectMap.getConnect(ip + ":" + port+"_"+pgName);
            Statement state = con.createStatement();
            ResultSet rs = state.executeQuery(sql); //查询数据
            while (rs.next()) {
                reByte=rs.getBytes("st_asmvt");
            }
            return reByte;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
