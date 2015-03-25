package com.tencent.tools;

import com.tencent.db.DBuc;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Created by geek on 2014/11/15.
 */
public class SvmDataFormat {

    //从db中取出预先选取的磁盘smart作为训练数据
    public static void svmDataFormate(Properties pps) throws Exception{
        Connection conn = DBuc.getConnection();

        BufferedReader br=new BufferedReader(new FileReader(pps.getProperty("badsn.path")));
        PrintWriter pw=new PrintWriter(new FileWriter(pps.getProperty("trainData.path")));
        String sn;
        int num=0;
        while((sn=br.readLine())!=null){
            //System.out.println(sn);
            getSmartDataBySn(sn,pw,conn);
            num++;
        }
        //取等量的好盘smart数据，5天*8条per day
        readFromGood(pps,pw,num*5*8);
        br.close();
        pw.close();
        conn.close();
    }

    //根据sn，从db中取出对应的故障盘smart数据，规整后转存到指定的训练数据文件中
    public static void getSmartDataBySn(String sn,PrintWriter pw,Connection conn) throws Exception{
        String queryUT="select create_time - interval 4 day from bad_disk_sn where disk_sn="+"'"+sn+"'"+" limit 1";
        String queryDT="select create_time + interval 1 day from bad_disk_sn where disk_sn="+"'"+sn+"'"+" limit 1";
        ResultSet resD=conn.createStatement().executeQuery(queryUT);
        ResultSet resU=conn.createStatement().executeQuery(queryDT);
        if(resU.next()&&resD.next()){
            String up_time=resU.getDate(1).toString();
            String down_time=resD.getDate(1).toString();
            String query="select * from smart_bad where sn="+"'"+sn+"'"+
                    "&& time between '"+down_time+"' and '"+up_time+"'";
            ResultSet res=conn.createStatement().executeQuery(query);
            while(res.next()){
                float read_error=(100-res.getFloat(4))/100;
                float spin_time=(100-res.getFloat(5))/100;
                float re_sec_value=(100-res.getFloat(6))/100;
                float re_sec_raw=res.getFloat(7);
                float seek_error=(100-res.getFloat(8))/100;
                float spin_retry=((100-res.getFloat(9))/100)*5;
                float shut_down=(100-res.getFloat(10))/100;
                float power_cycle=(100-res.getFloat(11))/100;
                float power_hour=(100-res.getFloat(12))/100;
                float offline=(100-res.getFloat(13))/100;
                float temper=(100-res.getFloat(14))/100;
                float pend_value=(100-res.getFloat(15))/100;
                Float pend_raw=res.getFloat(16);
                re_sec_raw=(re_sec_raw+pend_raw)/4000;
                pw.println(-1 + " " + read_error + " " + spin_time + " " + re_sec_value + " " + re_sec_raw + " " + seek_error + " "
                        + spin_retry + " " + shut_down + " " + power_cycle+" "+power_hour + " " +offline+ " " +temper+" "+ pend_value );
            }
        }
        pw.flush();
    }

    //从已知好盘的smart数据里选指定条数作为训练数据
    private static void readFromGood(Properties pps,PrintWriter pw,int num) throws IOException {
        BufferedReader br=new BufferedReader(new FileReader(pps.getProperty("goodSmart.path")));
        for(int i=0;i<=num;i++){
            pw.println(br.readLine());
        }
        pw.flush();
    }

}

