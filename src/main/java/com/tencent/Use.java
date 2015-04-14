package com.tencent;

import com.tencent.db.DBtest;
import com.tencent.db.DBuc;
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
        System.out.println("===================================================" +
                "==================================");
        DBtest.testConnection();
        System.out.println("===================================================" +
                "==================================");
        //System.out.println(System.getProperty("user.dir"));
        pps.load(new FileInputStream("src\\main\\resources\\" +
                "property/system.property"));
        System.out.println("classify predict loading...\ntraining data preparing...");

        ClassifyPrediction.classify(pps);

        System.out.println("false rate=false alarm/all good\nprecision=predicted failures/all failures");

        System.out.println("false rate test data is from three disk groups,each has 3000 good disks");
        System.out.println("precision test data is from 3000 bad disks");

        System.out.println("===================================================" +
                "==================================");

        //LrDataPerpare.lrTrainData(pps,7);

        System.out.println("Disk Failure Time Predict Loading Data...");
        GetLeadTime.getLrSn(pps,DBuc.getConnection());
        for(int i=3;i<=10;i+=2){
            LrPredict.predict(pps,i);
        }
        //LrDataFormat.lrTestDataFormat(pps);
        //LeadTimePredict.getPredictLeadTime(pps);



        //GetLeadTime.getAverageLeadTime(pps,DBuc.getConnection(),7,0.6);
        System.out.println("===================================================" +
                "==================================");

    }
}
