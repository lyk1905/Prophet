package com.tencent.tools;

import java.io.IOException;
import java.text.DecimalFormat;
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
        svm_problem problem=getSvmProblem(pps.getProperty("svmGoodTestData.path"));
        int precise=0;
        for(int i=0;i<problem.y.length;i++){

            Double result=svm.svm_predict(model,problem.x[i]);
            //System.out.println(problem.y[i]+"   "+result);
            if(result.equals(problem.y[i]))
                precise++;
            //System.out.println(precise);
        }
        double precision=precise/(double)problem.y.length;
        java.text.DecimalFormat dfd=new DecimalFormat("0.0000");
        System.out.println("classify prediction precision is:"+dfd.format(precision));
    }

    public static svm_problem getSvmProblem(String path){
        svm_problem problem=SvmModelTrain.initProblem(path);
        return problem;
    }

    public static void getClassifyFalseRate(Properties pps) throws IOException{
        svm_model model= svm.svm_load_model(pps.getProperty("svmModel.path"));
        svm_problem problem=getSvmProblem(pps.getProperty("svmFalseTestData.path"));
        int wrong=0;
        for(int i=0;i<problem.y.length;i++){
            if(problem.y[i]!=svm.svm_predict(model,problem.x[i]))
                wrong++;
        }
        //System.out.println(wrong);
        double falseRate=wrong/problem.y.length;
        java.text.DecimalFormat dfd=new DecimalFormat("0.0000");
        System.out.println("classify prediction false rate is:"+dfd.format(falseRate));
    }
}
