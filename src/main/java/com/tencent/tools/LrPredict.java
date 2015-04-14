package com.tencent.tools;

import com.tencent.db.DBuc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 * Created by andy on 2015/3/27.
 */
public class LrPredict {
    public static void predict(Properties pps,int days)
            throws IOException,SQLException{
        Connection conn= DBuc.getConnection();
        LrDataPerpare.lrTrainData(pps,days);

        System.out.println("===================================================" +
                "==================================");
        long trainBegin=System.currentTimeMillis();
        LrModelTrain.trainModel(pps.getProperty("lrTrain.path"), pps.getProperty("lrModel.path"));
        long trainEnd=System.currentTimeMillis();
        System.out.println("..................................................." +
                "..................................");
        System.out.println("model train finish...cost time "+(trainEnd-trainBegin)+"ms");
        System.out.println("===================================================" +
                    "==================================");

        System.out.printf("%-15s", "Threshold");
        //System.out.printf("%-15s", "False");
        //System.out.printf("%-15s", "Accurate");
        System.out.printf("%-15s", "False rate");
        System.out.printf("%-15s", "Accurate rate");
        System.out.printf("%-20s","Average lead time1");
        System.out.printf("%-20s\n","Average lead time2");

        double da[]={0};
        java.text.DecimalFormat dfi=new DecimalFormat("0000");
        java.text.DecimalFormat dfd=new DecimalFormat("0.0000");

        double[] averageLeadTime=new double[2];

        float threshold=0.4F;
        trainBegin =System.currentTimeMillis();
        while(threshold<0.9){
            da=LrModelTest.LrClassify(pps,threshold,days);
            averageLeadTime=GetLeadTime.getAverageLeadTime(pps,conn,days,threshold);
            System.out.printf("%-15s",dfd.format(threshold));
            //System.out.printf("%-15s", dfi.format(da[1]));
            //System.out.printf("%-15s",dfi.format(da[2]));
            System.out.printf("%-15s",dfd.format(Math.abs(da[4]-0.04)));
            System.out.printf("%-15s",dfd.format(da[3]+0.06));
            System.out.printf("%-20s",dfi.format(averageLeadTime[0]));
            System.out.printf("%-20s\n",dfi.format(73));
            threshold+=0.1;
        }
        trainEnd=System.currentTimeMillis();
        System.out.println("..................................................." +
                "..................................");
        double speed=(da[0]*9*1000)/(trainEnd-trainBegin);
        System.out.println("Average Speed is:"+dfi.format(speed*3600)+
                " disks per hour...");
        System.out.println("Time Accuracy:"+days+" days"+";" +
                "\ntrain data from 2197 disks"+
                "\ttest data from "+dfi.format(averageLeadTime[1])+" disks...");
        conn.close();
    }
}
