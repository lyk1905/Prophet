package com.tencent.tools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by 永康 on 2015/3/27.
 */
public class LrPredict {
    public static void predict(Properties pps,int days)
            throws IOException,SQLException{

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

        float threshold=0.1F;
        while(threshold<1){
            LrModelTest.LrClassify(pps,threshold,days);
            threshold+=0.05;
        }
    }
}
