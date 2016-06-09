/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.atlas.performance.tools.response.data.parser;

import java.util.ArrayList;
import java.util.Collections;

public class StatisticsEvaluator {


    static Long findnthPercentile(ArrayList<Long> times, float n) {


        int size = times.size();
        float nper = (n/100)*(size);
        int iRank=(int)nper/1;

        Float fRank=nper%1;
        Float percentile;

        if(fRank==0f)
            percentile=times.get(iRank).floatValue();
        else
            percentile=fRank*(times.get(iRank)-times.get(iRank-1))+times.get(iRank-1);

        return percentile.longValue();
    }


    static ArrayList<String> findPercentile(ArrayList<Long> times,Integer type) {

        Collections.sort(times);

        long sum=0;
        for(int i=0;i<times.size();i++){
            sum=sum+times.get(i);
        }

        float[] percentiles={10f,25f,50f,75f,90f,95f};

        ArrayList<String> perc=new ArrayList<String>();
        if(type==1) {
            System.out.println("Min  \t" + TimeUtils.getFormattedTime(Collections.min(times)));
            System.out.println("Max  \t" + TimeUtils.getFormattedTime(Collections.max(times)));
            System.out.println("Avg  \t" + TimeUtils.getFormattedTime(sum/times.size()));
            perc.add(TimeUtils.getFormattedTime(Collections.min(times)));
            perc.add(TimeUtils.getFormattedTime(Collections.max(times)));
            perc.add(TimeUtils.getFormattedTime(sum/times.size()));
            for (int n = 0; n < percentiles.length; n++) {
                System.out.println((int)percentiles[n] + "th \t  "+ TimeUtils.getFormattedTime(findnthPercentile(times, percentiles[n])) + "\n");

                perc.add(TimeUtils.getFormattedTime(findnthPercentile(times, percentiles[n])));

            }
        }

        else
        {
            System.out.println("Min  \t" + Collections.min(times));
            System.out.println("Max  \t" +Collections.max(times));
            System.out.println("Avg  \t" + TimeUtils.getFormattedTime(sum/times.size()));

            for (int n = 0; n < percentiles.length; n++) {
                System.out.println(percentiles[n] + "th \t" + findnthPercentile(times, percentiles[n]) + "\n");

            }
        }
return perc;
    }

}
