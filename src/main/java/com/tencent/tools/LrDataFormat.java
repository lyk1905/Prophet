package com.tencent.tools;

import com.tencent.db.DBuc;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by geek on 2014/11/15.
 */
public class LrDataFormat {
    static int disk_num=0;
    //根据sn，提取所对应的smart数据
    public static void getDataBySn(Properties pps) throws SQLException,IOException {
        Connection conn = DBuc.getConnection();
        PrintWriter pwf=new PrintWriter(new FileWriter(pps.getProperty("lrFalse.path")));
        PrintWriter pwt=new PrintWriter(new FileWriter(pps.getProperty("lrTrue.path")));
        BufferedReader br=new BufferedReader(new FileReader(pps.getProperty("lrSn.path")));
        String sn;
        while((sn=br.readLine())!=null){
            System.out.println(sn);
            for10dayTrainData(pwf,pwt,conn,sn);
        }
        pwf.close();
        pwt.close();
        conn.close();
    }

    //取出10天所对应的smart数据
    public static void for10dayTrainData(PrintWriter pwf,PrintWriter pwt,Connection conn,String sn) throws SQLException{

        String queryUT="select threhold_time from cone.threhold_check where sn="+"'"+sn+"'"+" limit 1";
        ResultSet resUT=conn.createStatement().executeQuery(queryUT);
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
            if(list.size()>=14){
                for(int i=0;i<list.size()-7;i++){
                    int[][] tmpArray=new int[8][];
                    tmpArray[0]=(int[])list.get(i);
                    tmpArray[1]=(int[])list.get(i+1);
                    tmpArray[2]=(int[])list.get(i+2);
                    tmpArray[3]=(int[])list.get(i+3);
                    tmpArray[4]=(int[])list.get(i+4);
                    tmpArray[5]=(int[])list.get(i+5);
                    tmpArray[6]=(int[])list.get(i+6);
                    tmpArray[7]=(int[])list.get(i+7);
                    int[] sevenDiff=new int[7];
                    sevenDiff[0]=tmpArray[0][0]-tmpArray[7][0];
                    sevenDiff[1]=tmpArray[0][1]-tmpArray[7][1];
                    sevenDiff[2]=tmpArray[0][2]-tmpArray[7][2];
                    sevenDiff[3]=tmpArray[0][3]-tmpArray[7][3];
                    sevenDiff[4]=tmpArray[0][4]-tmpArray[7][4];
                    sevenDiff[5]=tmpArray[0][5]-tmpArray[7][5];
                    sevenDiff[6]=tmpArray[0][6]-tmpArray[7][6];
                    int sumTmp=tmpArray[0][3]+tmpArray[0][6];
                    int sumSeven=sevenDiff[3]+sevenDiff[6];
                    if(i<7){
                        pwf.println(1+" "+tmpArray[0][0]+" "+tmpArray[0][1]+" "+tmpArray[0][2]+" "+sumTmp+
                                " "+tmpArray[0][4]+" "+tmpArray[0][5]+" "+sevenDiff[0]+" "+sevenDiff[1]+" "+sevenDiff[2]+" "+sumSeven+
                                " "+sevenDiff[4]+" "+sevenDiff[5]);
                    }else{
                        pwt.println(0+" "+tmpArray[0][0]+" "+tmpArray[0][1]+" "+tmpArray[0][2]+" "+sumTmp+
                                " "+tmpArray[0][4]+" "+tmpArray[0][5]+" "+sevenDiff[0]+" "+sevenDiff[1]+" "+sevenDiff[2]+" "+sumSeven+
                                " "+sevenDiff[4]+" "+sevenDiff[5]);
                    }
                }
            }
        }
        pwf.flush();
        pwt.flush();
    }

    //将label为‘1’和‘-1’的训练样本合并
    public static void merge(Properties pps) throws IOException{
        BufferedReader brt=new BufferedReader(new FileReader(pps.getProperty("lrTrue.path")));
        BufferedReader brf=new BufferedReader(new FileReader(pps.getProperty("lrFalse.path")));
        PrintWriter lrpw=new PrintWriter(new FileWriter(pps.getProperty("testData.path")));
        String tmpt=null,tmpf=null;
        while(((tmpt=brt.readLine())!=null)||((tmpf=brf.readLine())!=null)){
            if(tmpt!=null)
                lrpw.println(tmpt);
            if(tmpf!=null)
                lrpw.println(tmpf);
        }
    }

    //从数据规整测试数据的样本，得到每块磁盘在故障点前不同时间点的数据
    public static void lrTestDataFormat(Properties pps) throws SQLException,IOException{

        Connection conn=DBuc.getConnection();
        PrintWriter tpw=new PrintWriter(pps.getProperty("lrtestdata.path"));
        BufferedReader br=new BufferedReader(new FileReader(pps.getProperty("lrSn.path")));
        String sn;
        while((sn=br.readLine())!=null){
            System.out.println(sn);
            forLrTestData(tpw, conn, sn);
        }
        conn.close();
        br.close();
        tpw.close();
    }
    public static void forLrTestData(PrintWriter tpw,Connection conn,String sn) throws SQLException{
        String queryUT="select threhold_time from cone.threhold_check where sn="+"'"+sn+"'"+" limit 1";
        ResultSet resUT=conn.createStatement().executeQuery(queryUT);
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
            if(list.size()>=14){
                for(int i=0;i<list.size()-7;i++){
                    int[][] tmpArray=new int[8][];
                    tmpArray[0]=(int[])list.get(i);
                    tmpArray[1]=(int[])list.get(i+1);
                    tmpArray[2]=(int[])list.get(i+2);
                    tmpArray[3]=(int[])list.get(i+3);
                    tmpArray[4]=(int[])list.get(i+4);
                    tmpArray[5]=(int[])list.get(i+5);
                    tmpArray[6]=(int[])list.get(i+6);
                    tmpArray[7]=(int[])list.get(i+7);
                    int[] sevenDiff=new int[7];
                    sevenDiff[0]=tmpArray[0][0]-tmpArray[7][0];
                    sevenDiff[1]=tmpArray[0][1]-tmpArray[7][1];
                    sevenDiff[2]=tmpArray[0][2]-tmpArray[7][2];
                    sevenDiff[3]=tmpArray[0][3]-tmpArray[7][3];
                    sevenDiff[4]=tmpArray[0][4]-tmpArray[7][4];
                    sevenDiff[5]=tmpArray[0][5]-tmpArray[7][5];
                    sevenDiff[6]=tmpArray[0][6]-tmpArray[7][6];
                    int sumTmp=tmpArray[0][3]+tmpArray[0][6];
                    int sumSeven=sevenDiff[3]+sevenDiff[6];

                    tpw.println(disk_num+" "+tmpArray[0][0]+" "+tmpArray[0][1]+" "+tmpArray[0][2]+" "+sumTmp+
                            " "+tmpArray[0][4]+" "+tmpArray[0][5]+" "+sevenDiff[0]+" "+sevenDiff[1]+" "+sevenDiff[2]+" "+sumSeven+
                            " "+sevenDiff[4]+" "+sevenDiff[5]);
                }
                disk_num++;
            }
        }
        tpw.flush();
    }
}
