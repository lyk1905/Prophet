package com.tencent.tools;

/**
 * Created by Robin on 2014/11/15.
 */

import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by coneliu on 2014/11/14.
 */
public class LrModelTest {

    public static void LrClassify(Properties pps) throws IOException {
        Problem problem=getLrProblem(pps.getProperty("positiveTestData.path"));
        de.bwaldvogel.liblinear.Model model=de.bwaldvogel.liblinear.Model.
                load(new FileReader(pps.getProperty("lrModel.path")));
        int precise=0,predict_result=0;
        double predict=0.0;
        double threshold=0.5;
        for(int i=0;i<problem.l;i++){
            predict=getProbability(model,(FeatureNode[])problem.x[i]);
            //System.out.println("predict:"+predict+"\n");
            if(predict<=0.6)
                precise++;
        }
        System.out.println("precision:"+precise+"\nproblem.l:"+problem.l);
        System.out.println("\nthe logistic regression classify precision is:"+(double)precise/problem.l);
    }

    private static Problem getLrProblem(String data) throws FileNotFoundException,IOException{
        Problem problem=new Problem();
        BufferedReader br=new BufferedReader(new FileReader(data));
        String line;
        String[] arrayTmp;

        ArrayList<double[]> list=new ArrayList<double[]>();
        ArrayList<String> listDouble=new ArrayList<String>();
        while((line=br.readLine())!=null){
            //System.out.println("line:"+line+"\n");
            arrayTmp=line.split(" ");
            double[] doubles=new double[arrayTmp.length-1];
            for(int i=1;i<arrayTmp.length;i++)
                doubles[i - 1] = Double.parseDouble(arrayTmp[i]);
            listDouble.add(arrayTmp[0]);
            list.add(doubles);
        }
        double[] labels=new double[listDouble.size()];
        for(int i=0;i<listDouble.size();i++)
            labels[i]=Double.parseDouble(listDouble.get(i));

        problem.l=labels.length;
        problem.n=list.get(0).length;

        FeatureNode[][] featureNodes=new FeatureNode[problem.l][problem.n];
        for(int i=0;i<problem.l;i++)
            for(int j=0;j<problem.n;j++)
                featureNodes[i][j]=new FeatureNode(j+1,(double)list.get(i)[j]);
        problem.x=featureNodes;
        problem.y=labels;

        return problem;
    }
	//返回分类概率
	public static double getProbability(Model model,FeatureNode[] feature) throws IOException {
        double[] test={0,0};
        double prediction= Linear.predictProbability(model, feature, test);
        return test[0];
    }
}
