package org.apache.atlas.performance.tools;

/**
 * Created by temp on 3/23/16.
 */
public class Query {
    String name,timeStamp;
    Long latency,connecTime,loadTime;
    String table;
    Long timeTaken;
    Query(String name,String timeStamp,Long latency,Long connecTime,Long loadTime,String table,Long timeTaken) {
        this.name=name;
        this.timeStamp=timeStamp;
        this.latency=latency;
        this.connecTime=connecTime;
        this.table=table;
        this.loadTime=loadTime;
        this.timeTaken=timeTaken;

    }

    public String getFormattedTime()
    {
        String ftime;
        long totalTime =timeTaken;
        long millis=totalTime%1000;
        long time = totalTime / 1000;
        String seconds = Integer.toString((int)(time % 60));
        String minutes = Integer.toString((int)((time % 3600) / 60));
        String hours = Integer.toString((int)(time / 3600));
        for (int ii = 0; ii < 2; ii++) {
            if (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            if (hours.length() < 2) {
                hours = "0" + hours;
            }
        }

        ftime=minutes+":"+seconds+":"+millis;
        return ftime;
    }


}
