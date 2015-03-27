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

    public static double[] LrClassify(Properties pps,float threshold,int days) throws IOException {
        Problem positiveProblem=getLrProblem(pps.getProperty("positiveTestData.path"));
        Problem passiveProblem=getLrProblem(pps.getProperty("passiveTestData.path"));
        de.bwaldvogel.liblinear.Model model=de.bwaldvogel.liblinear.Model.
                load(new FileReader(pps.getProperty("lrModel.path")));
        int positivePrecise=0,passivePrecise=0;
        double predict=0.0;
        for(int i=0;i<positiveProblem.l;i++){
            predict=getProbability(model,(FeatureNode[])positiveProblem.x[i]);
            //System.out.println("predict:"+predict+"\n");
            if(predict>=threshold)
                positivePrecise++;
        }
        for(int i=0;i<passiveProblem.l;i++){
            predict=getProbability(model,(FeatureNode[])passiveProblem.x[i]);
            if(predict>=threshold)
                passivePrecise++;
        }
        double da[]=new double[5];
        da[0]=positiveProblem.l+passiveProblem.l;
        da[1]=positivePrecise;
        da[2]=passivePrecise;
        da[3]=(double)passivePrecise/passiveProblem.l;
        da[4]=(double)positivePrecise/positiveProblem.l;
        /*
        System.out.println("Total  number of  disks:"+(positiveProblem.l+passiveProblem.l);
        System.out.println("\nThreshold is:"+threshold+"    Time accuracy:"+days+" days");
        System.out.println("False predict number:"+positivePrecise);
        System.out.println("Accurate predict number:"+passivePrecise);
        System.out.println("Accurate alarm rate is:"+(double)passivePrecise/passiveProblem.l);
        System.out.println("False alarm rate is:"+(double)positivePrecise/positiveProblem.l);
        System.out.println("..................................................." +
                "..................................");
        **/

        return da;
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
