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

package org.apache.atlas.performance.tools;


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
