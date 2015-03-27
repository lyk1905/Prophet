package com.tencent;

import com.tencent.db.DBtest;
import com.tencent.tools.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by geek on 2014/11/17.
 */
public class Use {
    public static void main(String[] args) throws SQLException,IOException{
        Properties pps=new Properties();
        DBtest.testConnection();
        pps.load(new FileInputStream("C:\\github\\Prophet\\src\\main\\resources\\" +
                "property/system.property"));
        System.out.println("classify predict loading...\ntraining data preparing...");

        //LrDataPerpare.lrTrainData(pps,7);


        LrPredict.predict(pps,5);
        //LrDataFormat.lrTestDataFormat(pps);
        //LeadTimePredict.getPredictLeadTime(pps);

        System.out.println("hello world!");
    }
}
