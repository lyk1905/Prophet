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
        pps.load(new FileInputStream("G:\\code\\svn\\Prophet\\src\\main\\resources\\" +
                "property/system.property"));
        //LrDataFormat.getDataBySn(pps);
        //LrModelTrain.TrainModel("E:/data/lrtraindata.txt", "E:/data/lrmodel.txt");
        //LrModelTest.LrClassify(pps);
        //LrDataFormat.lrTestDataFormat(pps);
        //LeadTimePredict.getPredictLeadTime(pps);
        DBtest.testConnection();
        System.out.println("hello world!");
    }
}
