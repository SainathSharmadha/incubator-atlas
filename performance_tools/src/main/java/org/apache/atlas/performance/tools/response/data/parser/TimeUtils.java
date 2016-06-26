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

public class TimeUtils {

    public static String getFormattedTime(Long timeTaken){
        String ftime="";
        long totalTime =timeTaken;
        long millis=totalTime%1000;
        long time = totalTime / 1000;
        String seconds = Integer.toString((int)(time % 60));
        String minutes = Integer.toString((int)((time % 3600) / 60));
        String hours = Integer.toString((int)(time / 3600));

        if(!minutes.equals("0"))
            ftime = String.format("%s%s mins",ftime,minutes);
        if(!seconds.equals("0"))
            ftime = String.format("%s%s secs",ftime,seconds);
        ftime=String.format("%s%s ms",ftime,millis);
        return ftime;
    }
}
