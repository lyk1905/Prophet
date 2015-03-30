package com.tencent.tools;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by 永康 on 2015/3/30.
 */
public class GetLeadTime {
    public static List getData(String sn,Connection conn,int days)
            throws SQLException{
        String queryUT="select check_date from cone.checked_table where sn="+"'"+sn+"'"+" limit 1";
        ResultSet resUT=conn.createStatement().executeQuery(queryUT);
        List listR=new ArrayList<int[]>();
        if(resUT.next()){
            String timeUT=resUT.getString(1);
            String query="select Raw_Read_Error_Rate_Value,Spin_Up_Time_Value," +
                    "Reallocated_Sector_Ct_Value,Reallocated_Sector_Ct_Raw," +
                    "Seek_Error_Rate_Value,Spin_Retry_Count_Value," +
                    "Current_Pending_Sector_Raw from cone.distinctbad where sn="+"'"+sn+"'"+
                    " && time <="+"'"+timeUT+"'"+" order by time desc;";
            ResultSet res=conn.createStatement().executeQuery(query);
            List list=new ArrayList<int[]>();

            while(res.next()){
                int[] farray=new int[7];
                farray[0]=res.getInt(1);
                farray[1]=res.getInt(2);
                farray[2]=res.getInt(3);
                farray[3]=res.getInt(4);
                farray[4]=res.getInt(5);
                farray[5]=res.getInt(6);
                farray[6]=res.getInt(7);
                list.add(farray);
            }
            if(list.size()>days) {
                for (int i = 0; i < list.size() - days; i++) {
                    int[][] tmpArray = new int[2][];
                    tmpArray[0] = (int[]) list.get(i);
                    /*tmpArray[1]=(int[])list.get(i+1);
                    tmpArray[2]=(int[])list.get(i+2);
                    tmpArray[3]=(int[])list.get(i+3);
                    tmpArray[4]=(int[])list.get(i+4);
                    tmpArray[5]=(int[])list.get(i+5);
                    tmpArray[6]=(int[])list.get(i+6);*/
                    tmpArray[1] = (int[]) list.get(i + days);
                    int[] sevenDiff = new int[7];
                    sevenDiff[0] = tmpArray[0][0] - tmpArray[1][0];
                    sevenDiff[1] = tmpArray[0][1] - tmpArray[1][1];
                    sevenDiff[2] = tmpArray[0][2] - tmpArray[1][2];
                    sevenDiff[3] = tmpArray[0][3] - tmpArray[1][3];
                    sevenDiff[4] = tmpArray[0][4] - tmpArray[1][4];
                    sevenDiff[5] = tmpArray[0][5] - tmpArray[1][5];
                    sevenDiff[6] = tmpArray[0][6] - tmpArray[1][6];
                    int sumTmp = tmpArray[0][3] + tmpArray[0][6];
                    int sumSeven = sevenDiff[3] + sevenDiff[6];

                    int[] tmp = new int[12];
                    tmp[0] = tmpArray[0][0];
                    tmp[1] = tmpArray[0][1];
                    tmp[2] = tmpArray[0][2];
                    tmp[3] = sumTmp;
                    tmp[4] = tmpArray[0][4];
                    tmp[5] = tmpArray[0][5];
                    tmp[6] = sevenDiff[0];
                    tmp[7] = sevenDiff[1];
                    tmp[8] = sevenDiff[2];
                    tmp[9] = sumSeven;
                    tmp[10] = sevenDiff[4];
                    tmp[11] = sevenDiff[5];
                    listR.add(tmp);
                }
            }
            res.close();
        }
        resUT.close();
        return listR;
    }

    public static double getAverageLeadTime(Properties pps,Connection conn,int days,double threshold)
            throws IOException,SQLException{
        String sn;

        Model model=Model.load(new File(pps.getProperty("lrModel.path")));

        List<int[]> list=new ArrayList();
        double leadTime=0;
        int disk=0;
        BufferedReader br=new BufferedReader(new FileReader(pps.getProperty("lrSn.path")));
        while((sn=br.readLine())!=null){
            list=getData(sn,conn,days);
            if(list.size()<=0)
                break;
            FeatureNode[][] featureNodes=new FeatureNode[list.size()][list.get(0).length];
            for(int i=0;i<list.size();i++)
                for(int j=0;j<list.get(0).length;j++)
                    featureNodes[i][j]=new FeatureNode(j+1,(double)list.get(i)[j]);
            for(int i=featureNodes.length-1;i>=0;i--){
                if(com.tencent.tools.LrModelTest.getProbability(model,featureNodes[i])>threshold){
                    leadTime+=i;
                    //System.out.print(featureNodes.length+"--"+leadTime);
                    break;
                }
            }
            disk++;
        }
        double averageLeadTime=leadTime/disk;
        return averageLeadTime;
    }
}

