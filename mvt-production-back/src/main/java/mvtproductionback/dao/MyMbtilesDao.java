package mvtproductionback.dao;


import mvtproductionback.config.SqliteConnectMap;
import mvtproductionback.entity.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;

@Component
public class MyMbtilesDao {

    @Autowired
    SqliteConnectMap sqliteConnectMap;


    public byte[] getMvtFromMB(String mbName, int zoom, int x, int y) {
        try {
            String sql = "SELECT * FROM tiles WHERE zoom_level = " + zoom + " AND tile_column = " + x + " AND tile_row = " +y;

            byte[] bytes =null;
            Connection conn = sqliteConnectMap.getConnect(mbName);
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(sql);
            while (rs.next()) {
                bytes=rs.getBytes("tile_data");
            }
            conn.commit();
            state.close();

            return bytes;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }



    public boolean createMetadataTable(String connectKey) {
        String createMetadataTableSql = "CREATE TABLE metadata (" +
                "   name text," +
                "   value text" +
                ");";

        return execSql(connectKey, createMetadataTableSql);
    }


    public boolean createTilesTable(String connectKey) {
        String createTilesTableSql = "CREATE TABLE tiles (" +
                "   zoom_level integer," +
                "   tile_column integer," +
                "   tile_row integer," +
                "   tile_data blob" +
                ");" +
                "CREATE UNIQUE INDEX tile_index on tiles (zoom_level, tile_column, tile_row);";

        return execSql(connectKey, createTilesTableSql);
    }

    public boolean saveTileToMbtiles(String connectKey, int z, int x, int y, byte[] tileBytes) {
        //zoom_level z  tile_column x tile_row y
        try {
            PreparedStatement ps = null;

            String selectTileSql = "INSERT INTO tiles VALUES (?,?,?,?)";

            Connection conn = sqliteConnectMap.getConnect(connectKey);
            ps = conn.prepareStatement(selectTileSql);
            ps.setInt(1, z);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setBytes(4, tileBytes);

            boolean rs = ps.execute(); //true表示第一个返回值是一个ResultSet对象；false表示这是一个更新个数或者没有结果集 插入操作就返回的是false
            conn.commit();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertMetadata(String connectKey, Metadata meta) {
        String sql = MessageFormat.format(
                "INSERT INTO metadata VALUES " +
                        "(''name'',''{0}'')," +
                        "(''format'',''{1}'')," +
                        "(''description'',''{2}'')," +
                        "(''attribute'',''{3}'')," +
                        "(''version'',''{4}'')," +
                        "(''type'',''{5}'')," +
                        "(''bounds'',''{6}'')," +
                        "(''center'',''{7}'')," +
                        "(''minzoom'',''{8}'')," +
                        "(''maxzoom'',''{9}'')," +
                        "(''json'',''{10}'');",
                meta.getName(), meta.getFormat(), meta.getDescription(), meta.getAttribution(),
                meta.getVersion(),meta.getType(),meta.getBounds(),meta.getCenter(),
                meta.getMinzoom(),meta.getMaxzoom(),meta.getJson());
        System.out.println(sql);
        return execSql(connectKey, sql);
    }

    public boolean execSql(String connectKey, String sql) {
        try {
            Connection conn = sqliteConnectMap.getConnect(connectKey);
            Statement state = conn.createStatement();
            boolean rs = state.execute(sql);
            conn.commit();
            state.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
