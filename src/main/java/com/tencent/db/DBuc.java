package com.tencent.db;

import java.sql.*;

/**
 * Created by coneliu on 2014/5/20.
 */
public class DBuc {
    public static Connection getConnection() throws SQLException{
        Connection conn=new DBCPool().setupDataSource().getConnection();
        return conn;
    }

    public static ResultSet executeQuery(String sql) throws SQLException{
        Statement state=getConnection().createStatement();
        ResultSet res=state.executeQuery(sql);
        return res;
    }

    public static void get96h() throws SQLException{

        Connection conn = getConnection();
        PreparedStatement preState=conn.prepareStatement("select * from baidu_smart where disk_num=?");

    }
}
