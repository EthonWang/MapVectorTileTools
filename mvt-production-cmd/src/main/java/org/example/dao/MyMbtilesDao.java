package org.example.dao;


import org.example.entity.Metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.MessageFormat;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/2/13
 **/

public class MyMbtilesDao {

    public static boolean createMetadataTable(Connection con) {
        String createMetadataTableSql = "CREATE TABLE metadata (" +
                "   name text," +
                "   value text" +
                ");";

        return execSql(con, createMetadataTableSql);
    }


    public static boolean createTilesTable(Connection con) {
        String createTilesTableSql = "CREATE TABLE tiles (" +
                "   zoom_level integer," +
                "   tile_column integer," +
                "   tile_row integer," +
                "   tile_data blob" +
                ");" +
                "CREATE UNIQUE INDEX tile_index on tiles (zoom_level, tile_column, tile_row);";

        return execSql(con, createTilesTableSql);
    }

    public static boolean saveTileToMbtiles(Connection con, int z, int x, int y, byte[] tileBytes) {
        //zoom_level z  tile_column x tile_row y
        try {
            PreparedStatement ps = null;

            String selectTileSql = "INSERT INTO tiles VALUES (?,?,?,?)";

//            Connection conn = sqliteConnectMap.getConnect(connectKey);
            ps = con.prepareStatement(selectTileSql);
            ps.setInt(1, z);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setBytes(4, tileBytes);

            boolean rs = ps.execute(); //true表示第一个返回值是一个ResultSet对象；false表示这是一个更新个数或者没有结果集 插入操作就返回的是false
            con.commit();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertMetadata(Connection con, Metadata meta) {
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
                meta.getVersion(), meta.getType(), meta.getBounds(), meta.getCenter(),
                meta.getMinzoom(), meta.getMaxzoom(), meta.getJson());
//        System.out.println(sql);
        return execSql(con, sql);
    }

    public static boolean execSql(Connection con, String sql) {
        try {
//            Connection conn = sqliteConnectMap.getConnect(connectKey);
            Statement state = con.createStatement();
            boolean rs = state.execute(sql);
            con.commit();
            state.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
