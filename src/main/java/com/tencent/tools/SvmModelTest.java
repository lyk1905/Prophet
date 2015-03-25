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
    public static void SvmClassify(Properties pps) throws IOException{
        svm_model model= svm.svm_load_model(pps.getProperty("svmModel.path"));
        svm_problem problem=getSvmProblem(pps.getProperty("testData.path"));
        int precise=0;
        for(int i=0;i<problem.y.length;i++){
            if(problem.y[i]==svm.svm_predict(model,problem.x[i]))
                precise++;
        }
        System.out.println("the svm classify precision is:"+precise/problem.y.length);
    }
    public static svm_problem getSvmProblem(String path){
        svm_problem problem=null;
        return problem;
    }
}
