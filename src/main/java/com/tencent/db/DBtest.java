package com.tencent.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by 永康 on 2014/12/22.
 */
public class DBtest {
    public static void  testConnection(){
        try {
            Connection conn = DBuc.getConnection();
            System.out.println("Data pool connected... continue...");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
