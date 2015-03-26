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

        //LrDataFormat.getDataBySn(pps);
        System.out.println("===================================================" +
                "==================================");
        long trainBegin=System.currentTimeMillis();
        LrModelTrain.trainModel("F:/data/lrtraindata.txt", "F:/data/lrmodel.txt");
        long trainEnd=System.currentTimeMillis();
        System.out.println("..................................................." +
                "..................................");
        System.out.println("model train finish...cost time "+(trainEnd-trainBegin)+"ms");
        System.out.println("===================================================" +
                "==================================");
        LrModelTest.LrClassify(pps);
        //LrDataFormat.lrTestDataFormat(pps);
        //LeadTimePredict.getPredictLeadTime(pps);

        System.out.println("hello world!");
    }
}
