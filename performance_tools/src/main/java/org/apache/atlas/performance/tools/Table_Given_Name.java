package org.apache.atlas.performance.tools;

import java.util.Random;

/**
 * Created by temp on 3/2/16.
 */
public class Table_Given_Name implements IQuery {
String template="hive_table+where+name%3D%27";

    public String generateQuery() {
        Random randomGenerator = new Random();
        Integer randTable=1;
            Random r = new Random();
            int Low = 1;
            int High = 100;
            randTable = r.nextInt(High - Low) + Low;
        System.out.println(DSL_TEMPLATE+template+"default.table_"+randTable+"@cluster1%27");
        return DSL_TEMPLATE+template+"default.table_"+randTable+"@cluster1%27";
    }
}
