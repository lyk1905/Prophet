package com.tencent.tools;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by andy on 2015/4/11.
 */
public class ClassifyPrediction {
    public static void classify(Properties pps) throws IOException {
        SvmModelTrain.trainModel(pps);
        SvmModelTest.getClassifyFalseRate(pps,pps.getProperty("svmFalseTestData00.path"));
        SvmModelTest.getClassifyFalseRate(pps, pps.getProperty("svmFalseTestData01.path"));
        SvmModelTest.getClassifyFalseRate(pps, pps.getProperty("svmFalseTestData02.path"));
        SvmModelTest.getClassifyPrecison(pps);
    }
}
