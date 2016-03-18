package org.apache.atlas.performance.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by temp on 3/2/16.
 */
public class DB_Given_Name implements IQuery{
    final String TEMPLATE="hive_db+where+name+%3D+%27";
    //static final String PATH="/Users/temp/Documents/QueryGenerator";
    static ArrayList<String> databases=new ArrayList<String>();
    public static void getAllDBs() throws IOException {

         // TODO: 3/8/16 Made all static for now . Must dynamically get DBs at runtime
      /*  File dbs=new File("databases.txt");
        BufferedReader buff=new BufferedReader(new FileReader(dbs));
        String db="";
        while((db=buff.readLine())!=null)
        {
                databases.add(db);
        }
*/
        databases.add("default");

    }

    public String generateQuery()  {
        String query="";
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
            query=DSL_TEMPLATE+TEMPLATE+databases.get(randDB-1)+"%27";
        }
        else if (dbCount==0)
        {
            System.out.println("No DB found");
            query=DSL_TEMPLATE+TEMPLATE+"default"+"%27";
        }


         return query;
    }

}
