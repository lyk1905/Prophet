package com.tencent.util;

import libsvm.svm_model;

import java.io.*;

/**
 * Created by andy on 2015/4/12.
 */
public class ModelSerialize {
    public static void saveSvmModel(String filePath,svm_model model){
        File file=new File(filePath);
        System.out.println(filePath);
        try{
            OutputStream osm=new FileOutputStream(file);
            ObjectOutputStream oosm=new ObjectOutputStream(osm);
            oosm.writeObject(model);

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
