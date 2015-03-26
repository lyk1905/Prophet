package com.tencent.db;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by jsonliu on 2014/5/20.
 * 定义与数据库连接的相关接口核函数
 */
public class DBCPool {
    private static Properties setProperties(String propertyPath){
        Properties pp=new Properties();
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(propertyPath);
            pp.load(fis);
            fis.close();
        }catch (Exception fne){
            fne.printStackTrace();
        }
        return pp;
    }
    public static DataSource setupDataSource() {
        Properties properties=setProperties("C:\\github\\Prophet\\src\\main\\resources\\property\\jdbc.property");
        BasicDataSource ds = new BasicDataSource();
        try {
            ds.setDriverClassName(properties.getProperty("jdbc.driverClassName").trim());
            ds.setUrl(properties.getProperty("jdbc.url").trim());
            ds.setUsername(properties.getProperty("jdbc.username").trim());
            ds.setPassword(properties.getProperty("jdbc.password").trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ds.setMaxIdle(5);
        ds.setPoolPreparedStatements(true);
        return ds;
    }
}
