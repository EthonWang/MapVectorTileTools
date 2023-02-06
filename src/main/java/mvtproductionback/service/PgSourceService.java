package mvtproductionback.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import mvtproductionback.entity.AddPgDTO;
import mvtproductionback.config.PgConnectMap;
import mvtproductionback.entity.response.JsonResult;
import mvtproductionback.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @Author wyjq
 * @Date 2023/1/6
 */

@Service
public class PgSourceService {

    @Autowired
    PgConnectMap pgConnectMap;

    public JsonResult addPgSource(AddPgDTO addPgDTO) {
        try{
            String key =addPgDTO.getIp()+":"+addPgDTO.getPort()+"_"+addPgDTO.getPgName();

            if(pgConnectMap.isExits(key)){
                return  ResponseResult.error("pg已添加");
            }else if(pgConnectMap.setConnect(addPgDTO)){
                return ResponseResult.success("添加pg源成功");
            }else {
                return  ResponseResult.error("添加pg源失败");
            }

        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error("添加pg源失败");
        }
    }


    public JsonResult getPgSourceList() {
        try {
            return ResponseResult.success(pgConnectMap.getConnectList());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error("获取pg列表失败");
        }
    }

    public JsonResult getPgShpsInfo(String ip, String port, String pgName) {
        try {
            String key =ip+":"+port+"_"+pgName;
            Connection conn=pgConnectMap.getConnect(key);

            JSONArray shpsInfoArr=new JSONArray();
            List<String> shpTableList=getShpTableList(conn);

            for (int i = 0; i < shpTableList.size(); i++) {
                JSONObject shpInfo=new JSONObject();
                shpInfo.put("shpName",shpTableList.get(i));
                shpInfo.put("shpType",getShpType(conn,shpTableList.get(i)));
                shpInfo.put("bounds",getShpBox2D(conn,shpTableList.get(i)));
                shpInfo.put("shpAttrs",getShpAttrInfo(conn,shpTableList.get(i)));
                shpsInfoArr.add(shpInfo);
            }

            return ResponseResult.success(shpsInfoArr);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error("获取pg的shp信息失败");
        }
    }

    //获取pg库内的shp表
    public List<String> getShpTableList(Connection conn){
        try{
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT * FROM pg_tables where schemaname= 'public'");
            List<String> shpTableList=new ArrayList();
            while (rs.next()) {
                if(!"spatial_ref_sys".equals(rs.getString("tablename"))){
                    shpTableList.add(rs.getString("tablename"));
                }
            }
            rs.close();
            state.close();
            return shpTableList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //获取shp的类型
    public String getShpType(Connection conn,String tableName){
        try{
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT GeometryType(geom) FROM "+ tableName + " limit 1");

            String shpType=null;
            while (rs.next()) {
                shpType= rs.getString("geometrytype");
            }
            rs.close();
            return shpType;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Double>  getShpBox2D(Connection conn,String tableName) {

        try{
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT ST_Extent(geom) FROM "+ tableName + " ;");

            List<Double> bounds=new ArrayList<>();
            while (rs.next()) {
                String boxStr= rs.getString("st_extent");
                String coordStr=boxStr.substring(4,boxStr.length()-1); //1 0,10 20
                double xMin= Double.parseDouble(coordStr.split(",")[0].split(" ")[0]);
                double xMax= Double.parseDouble(coordStr.split(",")[1].split(" ")[0]);
                double yMin= Double.parseDouble(coordStr.split(",")[0].split(" ")[1]);
                double yMax= Double.parseDouble(coordStr.split(",")[1].split(" ")[1]);
                Double[] boundsArray={xMin,yMin,xMax,yMax};  //"bounds": [-180,-85,180,85],
                bounds=new ArrayList<>(Arrays.asList(boundsArray));
            }
            rs.close();
            state.close();
            return bounds;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List getShpAttrInfo(Connection conn,String tableName){
        try{
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT * FROM information_schema.columns WHERE table_name   = '"+tableName+"';");
            List attrArray=new ArrayList();
            while (rs.next()) {
                JSONObject o=new JSONObject();
                o.put("column_name",rs.getString("column_name"));
                o.put("data_type",rs.getString("data_type"));
                attrArray.add(o);
            }
            rs.close();
            state.close();
            return attrArray;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
