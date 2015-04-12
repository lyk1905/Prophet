package com.tencent.tools;

import java.io.IOException;
import java.util.Properties;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_problem;

/**
 * Created by Robin on 2014/11/16.
 */

public class SvmModelTest {
    public static void getClassifyPrecison(Properties pps) throws IOException{
        svm_model model= svm.svm_load_model(pps.getProperty("svmModel.path"));
        svm_problem problem=getSvmProblem(pps.getProperty("testData.path"));
        int precise=0;
        for(int i=0;i<problem.y.length;i++){
            if(problem.y[i]==svm.svm_predict(model,problem.x[i]))
                precise++;
        }
        System.out.println("classify prediction precision is:"+precise/problem.y.length);
    }

    public static svm_problem getSvmProblem(String path){
        svm_problem problem=SvmModelTrain.initProblem(path);
        return problem;
    }

    public static void getClassifyFalseRate(Properties pps) throws IOException{
        svm_model model= svm.svm_load_model(pps.getProperty("svmModel.path"));
        svm_problem problem=getSvmProblem(pps.getProperty("testData.path"));
        int wrong=0;
        for(int i=0;i<problem.y.length;i++){
            if(problem.y[i]!=svm.svm_predict(model,problem.x[i]))
                wrong++;
        }
        System.out.println("classify prediction false rate is:"+wrong/problem.y.length);
    }
}
