package org.example.utils;

import com.alibaba.fastjson.JSONObject;
import org.example.entity.CacheLayersDTO;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/2/13
 **/
public class MvtUtils {
    public static void test(){
        System.out.println("test okkkk");
    }

    public static HashMap<String,Double> tileToEnvelope(int zoom, int x, int y){
        double worldMercMax = 20037508.3427892;
        double worldMercMin = -1 * worldMercMax;
        double worldMercSize = worldMercMax - worldMercMin;

        double worldTileSize=Math.pow(2,zoom);
        double tileMercSize = worldMercSize / worldTileSize;

        HashMap<String, Double> env = new HashMap<String, Double>();
        env.put("xmin", worldMercMin + tileMercSize * x);
        env.put("xmax", worldMercMin + tileMercSize * (x+ 1));
        env.put("ymin", worldMercMax - tileMercSize * (y + 1));
        env.put("ymax", worldMercMax - tileMercSize * y);
        return  env;
    }

    public static String envelopeToBoundsSQL(HashMap<String,Double> env){
        int DENSIFY_FACTOR = 4;
        env.put("segSize",(env.get("xmax")-env.get("xmin"))/DENSIFY_FACTOR);
        String sqlTemp=String.format("ST_Segmentize(ST_MakeEnvelope(%f, %f, %f, %f, 3857),%f)",env.get("xmin"),env.get("ymin"),env.get("xmax"),env.get("ymax"),env.get("segSize"));
        return sqlTemp;
    }

    public static String envelopeToSQLMulti(List<CacheLayersDTO> layersInfoList, HashMap<String,Double> env){
        String envSql=envelopeToBoundsSQL(env);
        String multiMvtSql="select( ";
        for(int i=0;i<layersInfoList.size();i++){
            String shpTableName=layersInfoList.get(i).getShpName();
            List<String> attrList=  layersInfoList.get(i).getField();
            String attrNames=" ";
            for(int j=0;j<attrList.size();j++){
                attrNames=attrList.get(j)+" ";
            }
            multiMvtSql=multiMvtSql+getSingleMvtSql(shpTableName,attrNames,envSql,"4326");
            if(i!=layersInfoList.size()-1){
                multiMvtSql=multiMvtSql+" || ";
            }
        }

        multiMvtSql=multiMvtSql+" )as st_asmvt";
        return multiMvtSql;
    }

    public static String getSingleMvtSql(String shpTableName,String attrNames,String envSql,String srid){

        String singleMvtSql= MessageFormat.format("(WITH" +
                        " bounds AS ( SELECT {0} AS geom, {0}::box2d AS b2d)," +
                        " mvtgeom AS (" +
                        " SELECT ST_AsMVTGeom(ST_Transform(t.{1}, 3857), bounds.b2d) AS geom, {2} " +
                        " FROM {3} t, bounds" +
                        " WHERE ST_Intersects(t.{1}, ST_Transform(bounds.geom, {4}))" +
                        " )" +
                        " SELECT ST_AsMVT(mvtgeom.* , ''{3}'' ) FROM mvtgeom)" ,
                envSql,"geom",attrNames,shpTableName,srid);
        return singleMvtSql;
    }

    public static JSONObject getTileRange(int zoom, double xMinMerc, double yMinMerc, double xMaxMerc, double yMaxMerc){
        double worldMercMax = 20037508.3427892;
        double worldMercMin = -1 * worldMercMax;
        double worldMercSize = worldMercMax - worldMercMin;

        double worldTileSize=Math.pow(2,zoom);
        double tileMercSize = worldMercSize / worldTileSize;

        int xTileMin= (int) Math.floor((xMinMerc-worldMercMin)/tileMercSize);
        int yTileMin= (int) Math.floor((worldMercMax-yMaxMerc)/tileMercSize);
        int xTileMax= (int) Math.floor((xMaxMerc-worldMercMin)/tileMercSize);
        int yTileMax= (int) Math.floor((worldMercMax-yMinMerc)/tileMercSize);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("xTileMin",xTileMin);
        jsonObject.put("yTileMin",yTileMin);
        jsonObject.put("xTileMax",xTileMax);
        jsonObject.put("yTileMax",yTileMax);

        return jsonObject;
    }

    public static List<Double> wgs84ToWebMerc(double lon,double lat){
        List<Double> lonlat=new ArrayList<>();
        double lonMerc=lon*20037508.34/180;
        double latMerc=20037508.34*Math.log(Math.tan((90+lat)*Math.PI/360))/Math.PI;
        lonlat.add(lonMerc);
        lonlat.add(latMerc);
        return lonlat;
    }

}