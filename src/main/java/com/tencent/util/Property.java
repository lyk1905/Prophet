package com.tencent.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by coneliu on 2014/7/12.
 */
public class Property {
    public static Properties getProperty(String filePath){
        Properties pps=new Properties();
        FileInputStream fis=null;
        try{
            fis=new FileInputStream(filePath);
            pps.load(fis);
            fis.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return pps;
    }
}
