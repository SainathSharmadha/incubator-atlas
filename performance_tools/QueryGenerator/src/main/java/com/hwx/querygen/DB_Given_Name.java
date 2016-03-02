package com.hwx.querygen;

import com.thoughtworks.xstream.mapper.SystemAttributeAliasingMapper;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by temp on 3/2/16.
 */
public class DB_Given_Name implements IQuery{
    String template="hive_db+where+name+%3D+%27";
    static final String PATH="/Users/temp/Documents/QueryGenerator";
    static ArrayList<String> databases=new ArrayList<String>();
    public static void getAllDBs() throws IOException {


       /* Runtime.getRuntime().exec("sh listDB.sh");
        System.out.println("Script Executed");
        try {
            //thread to sleep for the specified number of milliseconds
            Thread.sleep(20000);
        } catch ( java.lang.InterruptedException ie) {
            System.out.println(ie);
        }*/
        File dbs=new File("databases.txt");
        BufferedReader buff=new BufferedReader(new FileReader(dbs));
        String db="";
        while((db=buff.readLine())!=null)
        {
                databases.add(db);
        }


    }

    public String generateQuery()  {
        try {
            getAllDBs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer dbCount=databases.size();
        Random randomGenerator = new Random();
        Integer randDB=1;
        if(dbCount>1) {
            Random r = new Random();
            int Low = 1;
            int High = dbCount;
            randDB = r.nextInt(High - Low) + Low;
        }
        else if (dbCount==0)
        {
            System.out.println("No DB found");
        }
        //Integer randDB=randomGenerator.nextInt(dbCount);
        HTTPSampler examplecomSampler = new HTTPSampler();
        examplecomSampler.setDomain("localhost");
         return DSL_TEMPLATE+template+databases.get(randDB-1)+"%27";
    }

}
