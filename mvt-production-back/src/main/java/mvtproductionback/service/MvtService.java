package mvtproductionback.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import mvtproductionback.config.SqliteConnectMap;
import mvtproductionback.dao.MyMbtilesDao;
import mvtproductionback.dao.MyPgDao;
import mvtproductionback.entity.CacheLayersDTO;
import mvtproductionback.entity.CreateCacheTaskDTO;
import mvtproductionback.entity.Metadata;
import mvtproductionback.entity.response.JsonResult;
import mvtproductionback.util.MvtUtils;
import mvtproductionback.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
@Service
public class MvtService {


    @Value("${dataStorePath}")
    String dataStorePath;

    @Autowired
    SqliteConnectMap sqliteConnectMap;

    @Autowired
    MyMbtilesDao myMbtilesDao;

    @Autowired
    MyPgDao myPgDao;


    public void getMbtilesMvt(String mbName, int zoom, int x, int y, HttpServletResponse response) {
        try {

            byte[] mvtByte = myMbtilesDao.getMvtFromMB(mbName, zoom, x, y);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Content-type", "application/vnd.mapbox-vector-tile");
            String mtvFileName = String.format("%d_%d_%d.mvt", zoom, x, y);
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(mtvFileName.getBytes("UTF-8"), "iso-8859-1"));
            OutputStream os = response.getOutputStream();
            os.write(mvtByte);
            log.info("getMvt:" + zoom + " " + x + " " + y);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public JsonResult createTileCache(CreateCacheTaskDTO createCacheTaskDTO) {
        try {

            JSONObject taskInfo = getTaskInfo(createCacheTaskDTO.getLayers());

            if (!createMbtilesAndConnect(createCacheTaskDTO.getName())) {
                return ResponseResult.error("数据库名称已存在");
            }


            if (!initSqliteTable(createCacheTaskDTO.getName())) {
                return ResponseResult.error("初始化数据库表失败");
            }

            beginFillMetadata(taskInfo, createCacheTaskDTO);

            beginProcess(taskInfo, createCacheTaskDTO);

            return ResponseResult.success("创建瓦片缓存成功");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("创建瓦片缓存失败");
        }

    }

    //获取缓存任务基本信息及预处理
    public JSONObject getTaskInfo(List<CacheLayersDTO> vectorLayers) {
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
    public List<Double> getMaxBounds(JSONArray boundsArray) {
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

    public boolean createMbtilesAndConnect(String taskName) throws IOException {
        File mbtilesFile = new File(dataStorePath, taskName + ".mbtiles");
        if (mbtilesFile.exists()) {
            return false;
        } else {
            mbtilesFile.createNewFile();
            sqliteConnectMap.setConnect(taskName, mbtilesFile.getAbsolutePath());
        }
        return true;
    }

    public boolean initSqliteTable(String taskName) {

        if (!myMbtilesDao.createMetadataTable(taskName)) {
            return false;
        }

        if (!myMbtilesDao.createTilesTable(taskName)) {
            return false;
        }
        return true;
    }

    public boolean beginProcess(JSONObject taskInfo, CreateCacheTaskDTO createCacheTaskDTO) {
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

            log.info("----zoom: " + zoom + " x: " + xTileMin + "-" + xTileMax + " y: " + yTileMin + "-" + yTileMax + ";");

            for (int x = xTileMin; x <= xTileMax; x++) {
                for (int y = yTileMin; y <= yTileMax; y++) {
                    //todo 可以加上范围判断
                    HashMap<String, Double> env = MvtUtils.tileToEnvelope(zoom, x, y);
                    List<CacheLayersDTO> withinLayers = getWithinLayers(createCacheTaskDTO.getLayers(),env);
                    String sql = MvtUtils.envelopeToSQLMulti(withinLayers, env);
                    byte[] mvtByte = myPgDao.getMvtFromPgSource(
                            createCacheTaskDTO.getIp(),
                            createCacheTaskDTO.getPort(),
                            createCacheTaskDTO.getPgName(),
                            sql);

                    log.info("处理坐标：" + zoom + "," + x + "," + y + ": " + sql);
                    log.info("byte size：" + mvtByte.length);
                    if (mvtByte.length != 0) {
                        boolean re = myMbtilesDao.saveTileToMbtiles(createCacheTaskDTO.getName(), zoom, x, y, mvtByte);
                    }
                }
            }

        }
        log.info("处理完成：" + createCacheTaskDTO.getName());
        return true;
    }


    public boolean beginFillMetadata(JSONObject taskInfo, CreateCacheTaskDTO createCacheTaskDTO) {
        try {
            String taskName = createCacheTaskDTO.getName();

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
                for (int j = 0; j < attrs.size(); j++) {
                    JSONObject attr = attrs.get(j);
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

            myMbtilesDao.insertMetadata(taskName, meta);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<CacheLayersDTO> getWithinLayers(List<CacheLayersDTO> layers, HashMap<String, Double> env) {
        List<Double> tileBounds=new ArrayList<>();
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

    private boolean isIntersect(List<Double> tileBounds, List<Double> mercBounds) {
        double p1x=Math.max(tileBounds.get(0),mercBounds.get(0));
        double p1y=Math.max(tileBounds.get(1),mercBounds.get(1));
        double p2x=Math.min(tileBounds.get(2),mercBounds.get(2));
        double p2y=Math.min(tileBounds.get(3),mercBounds.get(3));
        return p2x >= p1x && p2y >= p1y;
    }

}
