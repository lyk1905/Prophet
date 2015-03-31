package com.tencent.tools;

import com.tencent.db.DBuc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 * Created by 永康 on 2015/3/27.
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
        System.out.printf("%-15s", "False");
        System.out.printf("%-15s", "Accurate");
        System.out.printf("%-15s", "False rate");
        System.out.printf("%-15s", "Accurate rate");
        System.out.printf("%-15s\n","Average lead time");

        double da[]={0};
        java.text.DecimalFormat dfi=new DecimalFormat("0000");
        java.text.DecimalFormat dfd=new DecimalFormat("0.0000");

        double averageLeadTime=0;

        float threshold=0.1F;
        trainBegin =System.currentTimeMillis();
        while(threshold<1){
            da=LrModelTest.LrClassify(pps,threshold,days);
            threshold+=0.1;
            averageLeadTime=GetLeadTime.getAverageLeadTime(pps,conn,days,threshold);
            System.out.printf("%-15s",dfd.format(threshold));
            System.out.printf("%-15s", dfi.format(da[1]));
            System.out.printf("%-15s",dfi.format(da[2]));
            System.out.printf("%-15s",dfd.format(da[4]));
            System.out.printf("%-15s",dfd.format(da[3]));
            System.out.printf("%-15s\n",dfi.format(averageLeadTime));
        }
        trainEnd=System.currentTimeMillis();
        System.out.println("..................................................." +
                "..................................");
        double speed=(da[0]*9*1000)/(trainEnd-trainBegin);
        System.out.println("Average Speed is:"+dfi.format(speed)+
                " disks per second...");
        System.out.println("Time Accuracy:"+days+"days"+";\tpredicted disk number is:"+dfi.format(da[0]));
        conn.close();
    }
}
