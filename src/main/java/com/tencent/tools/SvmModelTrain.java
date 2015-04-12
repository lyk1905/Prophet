package com.tencent.tools;

/**
 * Created by Robin on 2014/11/16.
 */
import com.tencent.util.ModelSerialize;
import libsvm.svm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import libsvm.*;
/**
 * Created by coneliu on 2014/9/14.
 */

public class SvmModelTrain {

    /*
    从property中读取参数，根据参数中指定的个路径，找到训练数据，并训练
    出模型，将模型存储在本地；
     */
    public static void trainModel(Properties pps) throws IOException{
        svm_problem prob=initProblem(pps.getProperty("svmTrainData.path"));
        System.out.println(pps.getProperty("svmTrainData.path"));
        svm_parameter para=setParaAsDefault(12);
        svm_model model=svm.svm_train(prob, para);
        save_model(pps.getProperty("svmModel.path"),model);
        //ModelSerialize.saveSvmModel(pps.getProperty("svmModel.path"),model);
    }

    /*
    存储训练的SVM模型，（存储路径，模型）
     */
    public static void save_model(String filePath,svm_model model) throws IOException {
        svm.svm_save_model(filePath,model);
    }

    public static svm_problem initProblem(String filePath){

        svm_problem prob=new svm_problem();
        String line;
        int i=0;
        List<String> list1=new ArrayList();
        List<svm_node[]> list2=new ArrayList();
        try{
            System.out.println(filePath);
            BufferedReader br=new BufferedReader(new FileReader(filePath));
            while((line=br.readLine())!=null){

                String[] tmpS=line.split(" ");
                list1.add(tmpS[0]);
                svm_node[] nodeArray=new svm_node[tmpS.length-1];
                for(int j=1;j<tmpS.length;j++){

                    svm_node tmpNode=new svm_node();
                    tmpNode.index=j;
                    tmpNode.value=Double.parseDouble(tmpS[j]);
                    nodeArray[j-1]=tmpNode;
                }
                list2.add(nodeArray);
                i++;
            }
            br.close();
            prob.l=i;
        }catch(Exception e){
            e.printStackTrace();
        }
        prob.y=new double[list1.size()];
        prob.x=new svm_node[list1.size()][];
        for(i=0;i<list1.size();i++){
            prob.y[i]=Double.parseDouble(list1.get(i));
            prob.x[i]=list2.get(i);
        }

        return prob;
    }

    public static svm_parameter setParaAsDefault(int scale){
        svm_parameter param = new svm_parameter();
        // default values
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 1.0/(double)scale;    // 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        return param;
    }

}

