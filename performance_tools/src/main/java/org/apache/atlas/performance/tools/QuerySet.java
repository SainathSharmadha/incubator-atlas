package org.apache.atlas.performance.tools;
import java.util.ArrayList;

/**
 * Created by temp on 3/23/16.
 */
public class QuerySet {
    static Integer numQueriesPerSet;
    public boolean isSuccess;
    public static void setNumQueriesPerSet(Integer numQueriesPerSet) {
        QuerySet.numQueriesPerSet = numQueriesPerSet;
    }

    ArrayList<Query> querySet=new ArrayList<Query>();

    public void addToQuerySet(Query query)
    {
        this.querySet.add(query);
    }
}
