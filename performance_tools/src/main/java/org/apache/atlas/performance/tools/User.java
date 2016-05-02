package org.apache.atlas.performance.tools;

import java.util.ArrayList;

/**
 * Created by temp on 3/23/16.
 */
public class User {
    static Integer loopCount;
    String userName;
    QuerySet[] querySets;
    User(String userName) {
        this.userName=userName;
        querySets = new QuerySet[loopCount];
        for(int i=0;i<loopCount;i++){
            querySets[i]=new QuerySet();
        }
    }
}
