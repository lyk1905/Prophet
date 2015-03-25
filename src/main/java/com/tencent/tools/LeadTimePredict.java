package com.tencent.tools;

import com.tencent.db.DBuc;
import de.bwaldvogel.liblinear.FeatureNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by geek on 2014/12/9.
 */
public class LeadTimePredict {
    static double predict=0;                   //能够取到相应数据进行预测的磁盘的块数（并不是每一块盘都采集到了规定需要的数据）
    public static void getPredictLeadTime(Properties pps) throws IOException,SQLException{
        BufferedReader br=new BufferedReader(new FileReader(pps.getProperty("lrSn.path")));
        de.bwaldvogel.liblinear.Model model=de.bwaldvogel.liblinear.Model.load(new FileReader(pps.getProperty("lrModel.path")));
        Connection conn=DBuc.getConnection();
        String sn;
        double precise=0;
        double time=0;
        double seven=0;
        while((sn=br.readLine())!=null){
            int leadTimeMess[]=getLeadTimeBySn(conn,model,sn);

            if(leadTimeMess!=null) {
                precise++;
                time+=leadTimeMess[0];
                System.out.println(sn+"lead time is:"+leadTimeMess[0]+"*****"+"data length:"+leadTimeMess[1]);
                if(leadTimeMess[0]<7)
                    seven++;
            }
        }
        System.out.println("we have predict "+predict+" times,"+"precise is:"+precise/predict+",average lead time is:"+time/precise);
        System.out.println("predict in 7 days times:"+seven+",precise is:"+seven/predict);
    }
    //准确预测时返回预测的lead time和取样数据的天数，没有预测出时返回‘null’
    public static int[] getLeadTimeBySn(Connection conn,de.bwaldvogel.liblinear.Model model,String sn) throws SQLException,IOException{
        int[] leadTime=null;
        FeatureNode[][] nodess=getFeatureBySn(conn,sn);
        double predict;
        for(int i=0;i<nodess.length;i++){
            predict=LrModelTest.getProbability(model,nodess[nodess.length-1-i]);
            if(predict>0.805){
                leadTime=new int[2];
                leadTime[0]=nodess.length-i-1;     //lead time
                leadTime[1]=nodess.length-1;         //取样数据的长度（取故障点前天数）
                break;
            }
        }
        return leadTime;
    }
    //返回对应sn的数据。数据选取方法和Lr训练时相同
    public static FeatureNode[][] getFeatureBySn(Connection conn,String sn) throws SQLException{
        String queryUT="select threhold_time from cone.threhold_check where sn="+"'"+sn+"'"+" limit 1";
        ResultSet resUT=conn.createStatement().executeQuery(queryUT);
        List<int[]> nodeList=new ArrayList<int[]>();
        if(resUT.next()) {
            String timeUT = resUT.getString(1);
            String query = "select Raw_Read_Error_Rate_Value,Spin_Up_Time_Value," +
                    "Reallocated_Sector_Ct_Value,Reallocated_Sector_Ct_Raw," +
                    "Seek_Error_Rate_Value,Spin_Retry_Count_Value," +
                    "Current_Pending_Sector_Raw from cone.distinctbad where sn=" + "'" + sn + "'" +
                    " && time <=" + "'" + timeUT + "'" + " order by time desc;";
            ResultSet res = conn.createStatement().executeQuery(query);
            List list = new ArrayList<int[]>();
            while (res.next()) {
                int[] farray = new int[7];
                farray[0] = res.getInt(1);
                farray[1] = res.getInt(2);
                farray[2] = res.getInt(3);
                farray[3] = res.getInt(4);
                farray[4] = res.getInt(5);
                farray[5] = res.getInt(6);
                farray[6] = res.getInt(7);
                list.add(farray);
            }
            if (list.size() >= 14) {
                predict++;
                for (int i = 0; i < list.size() - 7; i++) {
                    int[] nodes = new int[12];
                    int[][] tmpArray = new int[8][];
                    tmpArray[0] = (int[]) list.get(i);
                    tmpArray[1] = (int[]) list.get(i + 1);
                    tmpArray[2] = (int[]) list.get(i + 2);
                    tmpArray[3] = (int[]) list.get(i + 3);
                    tmpArray[4] = (int[]) list.get(i + 4);
                    tmpArray[5] = (int[]) list.get(i + 5);
                    tmpArray[6] = (int[]) list.get(i + 6);
                    tmpArray[7] = (int[]) list.get(i + 7);
                    int[] sevenDiff = new int[7];
                    sevenDiff[0] = tmpArray[0][0] - tmpArray[7][0];
                    sevenDiff[1] = tmpArray[0][1] - tmpArray[7][1];
                    sevenDiff[2] = tmpArray[0][2] - tmpArray[7][2];
                    sevenDiff[3] = tmpArray[0][3] - tmpArray[7][3];
                    sevenDiff[4] = tmpArray[0][4] - tmpArray[7][4];
                    sevenDiff[5] = tmpArray[0][5] - tmpArray[7][5];
                    sevenDiff[6] = tmpArray[0][6] - tmpArray[7][6];
                    int sumTmp = tmpArray[0][3] + tmpArray[0][6];
                    int sumSeven = sevenDiff[3] + sevenDiff[6];

                    nodes[0] = tmpArray[0][0];
                    nodes[1] = tmpArray[0][1];
                    nodes[2] = tmpArray[0][2];
                    nodes[3] = sumTmp;
                    nodes[4] = tmpArray[0][4];
                    nodes[5] = tmpArray[0][5];
                    nodes[6] = sevenDiff[0];
                    nodes[7] = sevenDiff[1];
                    nodes[8] = sevenDiff[2];
                    nodes[9] = sumSeven;
                    nodes[10] = sevenDiff[4];
                    nodes[11] = sevenDiff[5];

                    nodeList.add(nodes);
                }
            }
        }
        FeatureNode[][] nodess=new FeatureNode[nodeList.size()][12];
        for(int i=0;i<nodeList.size();i++){
            int[] tmp=nodeList.get(i);
            for(int j=0;j<12;j++){
                nodess[i][j]=new FeatureNode(j+1,(double)tmp[j]);
            }
        }
        return nodess;
    }
}
