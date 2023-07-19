package org.example.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/2/13
 **/

public class MyPgDao {
    public static byte[] getMvtFromPgSource(Connection con,String ip, String port,String pgName,String sql) {
        try{
            byte[] reByte =null;
//            Connection con = pgConnectMap.getConnect(ip + ":" + port+"_"+pgName);
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
