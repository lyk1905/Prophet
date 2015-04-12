package com.tencent.tools;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by andy on 2015/4/11.
 */
public class ClassifyPrediction {
    public static void classify(Properties pps) throws IOException {
        SvmModelTrain.trainModel(pps);
        SvmModelTest.getClassifyPrecison(pps);
        SvmModelTest.getClassifyFalseRate(pps);
    }
}
