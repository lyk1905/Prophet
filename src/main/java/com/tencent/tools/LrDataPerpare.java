package com.tencent.tools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by 永康 on 2015/3/27.
 */
public class LrDataPerpare {
    public static void lrTrainData(Properties pps,int days) throws IOException,SQLException{
        LrDataFormat.getDataBySn(pps,days);
        LrDataFormat.merge(pps,pps.getProperty("lrTrain.path"));
    }
}
