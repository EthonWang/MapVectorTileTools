package org.example.service;//package service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.MyMbtilesDao;
import org.example.dao.MyPgDao;
import org.example.entity.CacheLayersDTO;
import org.example.entity.CreateCacheTaskDTO;
import org.example.entity.Metadata;
import org.example.utils.MvtUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLInput;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * @Description
 * @Author wyjq
 * @Date 2023/1/6
 */

@Slf4j
public class MvtService {


    public static boolean createTileCache(CreateCacheTaskDTO taskDTO, String outMbtilesPath) {
        try {

            Connection pgConn = getPgConnection(taskDTO.getIp(), taskDTO.getPort(), taskDTO.getPgName(),
                    taskDTO.getUserName(), taskDTO.getPassword());
            if (pgConn == null) {
                log.error("pg数据库链接失败");
                return false;
            }

            JSONObject taskInfo = getTaskInfo(taskDTO.getLayers());

            if (!createMbtilesFile(outMbtilesPath)) {
                log.error("数据库名称已存在");
                return false;
            }

            File file = new File(outMbtilesPath);
            Connection sqliteConn = getSqliteConnection(file.getAbsolutePath());
            if (sqliteConn == null) {
                log.error("sqlite数据库链接失败");
                return false;
            }

            if (!initSqliteTable(sqliteConn, taskDTO.getTaskName())) {
                log.error("初始化数据库表失败");
                return false;
            }

            if (!beginFillMetadata(sqliteConn, taskInfo, taskDTO)) {
                log.error("mbtiles元数据填写失败");
            }

            beginProcess(pgConn, sqliteConn, taskInfo, taskDTO);

            System.out.println("--创建瓦片缓存成功");

            pgConn.close();
            sqliteConn.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建瓦片缓存任务失败");
            return false;
        }
    }

    public static Connection getPgConnection(String ip, String port, String dbName, String userName, String password) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection pgConn;
            String connectUrl = "jdbc:postgresql://" + ip + ":" + port + "/" + dbName + "?stringtype=unspecified";
            pgConn = DriverManager.getConnection(connectUrl, userName, password);
            return pgConn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getSqliteConnection(String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn;
            String connectUrl = "jdbc:sqlite:" + path;
            conn = DriverManager.getConnection(connectUrl, null, null);
            conn.setAutoCommit(false);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //获取缓存任务基本信息及预处理
    public static JSONObject getTaskInfo(List<CacheLayersDTO> vectorLayers) {
        JSONObject taskInfo = new JSONObject();

        JSONArray tableNameArray = new JSONArray();
        JSONArray boundsArray = new JSONArray();
        JSONArray minZoomArray = new JSONArray();
        JSONArray maxZoomArray = new JSONArray();

        //List<CacheLayersDTO> vectorLayers = createCacheDTO.getLayers();

        for (int i = 0; i < vectorLayers.size(); i++) {
            CacheLayersDTO layer = vectorLayers.get(i);
            List<Double> bounds = layer.getBounds();
            List<Double> leftBottomPoint = MvtUtils.wgs84ToWebMerc(bounds.get(0), bounds.get(1));
            List<Double> rightTopPoint = MvtUtils.wgs84ToWebMerc(bounds.get(2), bounds.get(3));
            List<Double> mercBounds = new ArrayList<>();
            mercBounds.add(leftBottomPoint.get(0));
            mercBounds.add(leftBottomPoint.get(1));
            mercBounds.add(rightTopPoint.get(0));
            mercBounds.add(rightTopPoint.get(1));
            layer.setMercBounds(mercBounds);

            tableNameArray.add(vectorLayers.get(i).getShpName());
            boundsArray.add(vectorLayers.get(i).getBounds());
            minZoomArray.add(vectorLayers.get(i).getMinzoom());
            maxZoomArray.add(vectorLayers.get(i).getMaxzoom());
        }

        taskInfo.put("tableNameArray", tableNameArray);
        taskInfo.put("boundsArray", boundsArray);
        taskInfo.put("minZoomArray", minZoomArray);
        taskInfo.put("maxZoomArray", maxZoomArray);


        List<Double> maxBounds = getMaxBounds(boundsArray);
        int minZoomTotal = Collections.min(minZoomArray.toJavaList(Integer.class));
        int maxZoomTotal = Collections.max(maxZoomArray.toJavaList(Integer.class));

        taskInfo.put("maxBounds", maxBounds);
        taskInfo.put("minZoomTotal", minZoomTotal);
        taskInfo.put("maxZoomTotal", maxZoomTotal);


        return taskInfo;
    }

    //获取所有图层的并集的最大范围
    public static List<Double> getMaxBounds(JSONArray boundsArray) {
        JSONArray xMins = new JSONArray();
        JSONArray yMins = new JSONArray();
        JSONArray xMaxs = new JSONArray();
        JSONArray yMaxs = new JSONArray();
        for (int i = 0; i < boundsArray.size(); i++) {
            JSONArray bound = boundsArray.getJSONArray(i);
            xMins.add(bound.get(0));
            yMins.add(bound.get(1));
            xMaxs.add(bound.get(2));
            yMaxs.add(bound.get(3));
        }
        List<Double> maxBounds = new ArrayList<>();
        maxBounds.add(Collections.min(xMins.toJavaList(Double.class)));
        maxBounds.add(Collections.min(yMins.toJavaList(Double.class)));
        maxBounds.add(Collections.max(xMaxs.toJavaList(Double.class)));
        maxBounds.add(Collections.max(yMaxs.toJavaList(Double.class)));
        return maxBounds;
    }

    public static boolean createMbtilesFile(String outMbtilesPath) {
        try {
            File mbtilesFile = new File(outMbtilesPath);
            if (mbtilesFile.exists()) {
                return false;
            } else {
                mbtilesFile.getParentFile().mkdirs();
                mbtilesFile.createNewFile();

//                sqliteConnectMap.setConnect(taskName, mbtilesFile.getAbsolutePath());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean initSqliteTable(Connection pgConn, String taskName) {

        if (!MyMbtilesDao.createMetadataTable(pgConn)) {
            return false;
        }

        if (!MyMbtilesDao.createTilesTable(pgConn)) {
            return false;
        }
        return true;
    }

    public static boolean beginProcess(Connection pgConn, Connection sqliteConn, JSONObject taskInfo, CreateCacheTaskDTO createCacheTaskDTO) {
        int minZoomTotal = taskInfo.getInteger("minZoomTotal");
        int maxZoomTotal = taskInfo.getInteger("maxZoomTotal");
        List<Double> maxBounds = (List<Double>) taskInfo.get("maxBounds");

        //bounds wgs84转webMerc
        List<Double> leftBottomPoint = MvtUtils.wgs84ToWebMerc(maxBounds.get(0), maxBounds.get(1));
        List<Double> rightTopPoint = MvtUtils.wgs84ToWebMerc(maxBounds.get(2), maxBounds.get(3));


        //从最小zoom开始
        for (int zoom = minZoomTotal; zoom <= maxZoomTotal; zoom++) {
            JSONObject xyRange = MvtUtils.getTileRange(zoom, leftBottomPoint.get(0), leftBottomPoint.get(1), rightTopPoint.get(0), rightTopPoint.get(1));
            int xTileMin = xyRange.getInteger("xTileMin");
            int yTileMin = xyRange.getInteger("yTileMin");
            int xTileMax = xyRange.getInteger("xTileMax");
            int yTileMax = xyRange.getInteger("yTileMax");

            System.out.println("--开始处理 zoom: " + zoom + ", x_range: " + xTileMin + "-" + xTileMax + ", y_range: " + yTileMin + "-" + yTileMax);

            for (int x = xTileMin; x <= xTileMax; x++) {
                for (int y = yTileMin; y <= yTileMax; y++) {
                    HashMap<String, Double> env = MvtUtils.tileToEnvelope(zoom, x, y);
                    List<CacheLayersDTO> withinLayers = getWithinLayers(createCacheTaskDTO.getLayers(), env);
                    String sql = MvtUtils.envelopeToSQLMulti(withinLayers, env);
                    byte[] mvtByte = MyPgDao.getMvtFromPgSource(
                            pgConn,
                            createCacheTaskDTO.getIp(),
                            createCacheTaskDTO.getPort(),
                            createCacheTaskDTO.getPgName(),
                            sql);

                    DecimalFormat df = new DecimalFormat("0.000");
                    System.out.println("--瓦片坐标：zoom " + zoom + ", x " + x + ", y " + y + ", 瓦片大小：" + df.format(mvtByte.length / 1024.0) + "KB");
                    if (mvtByte.length != 0) {
                        boolean re = MyMbtilesDao.saveTileToMbtiles(sqliteConn, zoom, x, y, mvtByte);
                    }
                }
            }

        }
        System.out.println("--处理完成：" + createCacheTaskDTO.getTaskName());
        return true;
    }

    public static boolean beginFillMetadata(Connection sqliteConn, JSONObject taskInfo, CreateCacheTaskDTO createCacheTaskDTO) {
        try {
            String taskName = createCacheTaskDTO.getTaskName();

            Metadata meta = new Metadata();
            meta.setName(taskName);
            meta.setBounds(taskInfo.getString("maxBounds")
                    .replace("[", "").replace("]", ""));
            meta.setCenter("118,32");
            meta.setMinzoom(taskInfo.getString("minZoomTotal"));
            meta.setMaxzoom(taskInfo.getString("maxZoomTotal"));

            List<JSONObject> vector_layers = new ArrayList<>();
            List<CacheLayersDTO> layersList = createCacheTaskDTO.getLayers();
            for (int i = 0; i < layersList.size(); i++) {
                CacheLayersDTO layer = layersList.get(i);
                JSONObject vector_layer = new JSONObject();
                vector_layer.put("id", layer.getShpName());
                vector_layer.put("minzoom", layer.getMinzoom());
                vector_layer.put("maxzoom", layer.getMaxzoom());
                List<String> filedSelected = layer.getField();

                JSONObject filed = new JSONObject();
                List<JSONObject> attrs = layer.getShpAttrs();
                for (JSONObject attr : attrs) {
                    String name = attr.getString("column_name");
                    String type = attr.getString("data_type");
                    if (filedSelected.contains(name))
                        filed.put(name, type);
                }
                vector_layer.put("field", filed);
                vector_layers.add(vector_layer);
            }

            JSONObject metaJsonObject = new JSONObject();
            metaJsonObject.put("vector_layers", vector_layers);
            meta.setJson(metaJsonObject.toJSONString());

            MyMbtilesDao.insertMetadata(sqliteConn, meta);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static List<CacheLayersDTO> getWithinLayers(List<CacheLayersDTO> layers, HashMap<String, Double> env) {
        List<Double> tileBounds = new ArrayList<>();
        tileBounds.add(env.get("xmin"));
        tileBounds.add(env.get("ymin"));
        tileBounds.add(env.get("xmax"));
        tileBounds.add(env.get("ymax"));

        List<CacheLayersDTO> withinLayers = new ArrayList<>();
        for (CacheLayersDTO layer : layers) {
            List<Double> mercBounds = layer.getMercBounds();
            if (isIntersect(tileBounds, mercBounds)) {
                withinLayers.add(layer);
            }
        }
        return withinLayers;
    }

    private static boolean isIntersect(List<Double> tileBounds, List<Double> mercBounds) {
        double p1x = Math.max(tileBounds.get(0), mercBounds.get(0));
        double p1y = Math.max(tileBounds.get(1), mercBounds.get(1));
        double p2x = Math.min(tileBounds.get(2), mercBounds.get(2));
        double p2y = Math.min(tileBounds.get(3), mercBounds.get(3));
        return p2x >= p1x && p2y >= p1y;
    }

}
