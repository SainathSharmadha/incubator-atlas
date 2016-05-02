package org.apache.atlas.performance.tools;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by temp on 4/13/16.
 */
public class StatisticsEvaluator {


    static Long findnthPercentile(ArrayList<Long> times, float n) {
        int size = times.size();
        float nper = (n/100)*(size);
        int iRank=(int)nper/1;

        Float fRank=nper%1;
        Float percentile;

        if(fRank==0f)
            percentile=times.get(iRank-1).floatValue();
        else
            percentile=fRank*(times.get(iRank)-times.get(iRank-1))+times.get(iRank-1);

        return percentile.longValue();
    }


    static void findPercentile(ArrayList<Long> times,Integer type) {

        Collections.sort(times);
        float[] percentiles={10f,25f,50f,75f,90f,95f};

        ArrayList<String> perc=new ArrayList<String>();
        if(type==1) {
            System.out.println("Min  \t" + TimeUtils.getFormattedTime(Collections.min(times)));
            System.out.println("Max  \t" + TimeUtils.getFormattedTime(Collections.max(times)));


            for (int n = 0; n < percentiles.length; n++) {
                System.out.println((int)percentiles[n] + "th \t  " + TimeUtils.getFormattedTime(findnthPercentile(times, percentiles[n])) + "\n");

            }
        }

        else
        {
            System.out.println("Min  \t" + Collections.min(times));
            System.out.println("Max  \t" +Collections.max(times));
            for (int n = 0; n < percentiles.length; n++) {
                System.out.println(percentiles[n] + "th \t" + findnthPercentile(times, percentiles[n]) + "\n");

            }
        }

    }

}
