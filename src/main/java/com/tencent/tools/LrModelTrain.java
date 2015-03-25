package com.tencent.tools;

import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.SolverType;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Linear;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.Writer;
import java.io.FileWriter;
/**
 * Created by geekliu on 2014/11/15.
 */
public class LrModelTrain {
    private static Problem getLrProblem(String trainData) throws FileNotFoundException,IOException {
        Problem problem=new Problem();
        BufferedReader br=new BufferedReader(new FileReader(trainData));
        String line;
        String[] arrayTmp;
        ArrayList<double[]> list=new ArrayList<double[]>();
        ArrayList<String> listDouble=new ArrayList<String>();
        while((line=br.readLine())!=null){
            arrayTmp=line.split(" ");
            double[] doubles=new double[arrayTmp.length-1];
            for(int i=1;i<arrayTmp.length;i++)
                doubles[i - 1] = Double.parseDouble(arrayTmp[i]);
            listDouble.add(arrayTmp[0]);
            list.add(doubles);
        }
        double[] lables=new double[listDouble.size()];
        for(int i=0;i<listDouble.size();i++)
            lables[i]=Double.parseDouble(listDouble.get(i));

        problem.l=lables.length;
        problem.n=list.get(0).length;

        FeatureNode[][] featureNodes=new FeatureNode[problem.l][problem.n];
        for(int i=0;i<problem.l;i++)
            for(int j=0;j<problem.n;j++)
                featureNodes[i][j]=new FeatureNode(j+1,(double)list.get(i)[j]);
        problem.x=featureNodes;
        problem.y=lables;

        return problem;
    }
    public static void TrainModel(String trainData,String modelStorePath) throws IOException{
        SolverType solver = SolverType.L2R_LR; // -s 0
        double C = 1.0;    // cost of constraints violation
        double eps = 0.01; // stopping criteria

        Parameter parameter = new Parameter(solver, C, eps);
        Problem problem=getLrProblem(trainData);
        Model model = Linear.train(problem, parameter);
        Writer writer=new FileWriter(modelStorePath);
        Linear.saveModel(writer,model);
    }
}
