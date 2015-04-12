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
        System.out.println(System.getProperty("user.dir"));
        pps.load(new FileInputStream("src\\main\\resources\\" +
                "property/system.property"));
        System.out.println("classify predict loading...\ntraining data preparing...");

        //LrDataPerpare.lrTrainData(pps,7);
/*
        for(int i=3;i<=10;i++){
            LrPredict.predict(pps,i);
        }
        //LrDataFormat.lrTestDataFormat(pps);
        //LeadTimePredict.getPredictLeadTime(pps);
*/
        SvmModelTrain.trainModel(pps);
        SvmModelTest.getClassifyFalseRate(pps);
        SvmModelTest.getClassifyPrecison(pps);
        System.out.println("******************************************************");

    }
}
