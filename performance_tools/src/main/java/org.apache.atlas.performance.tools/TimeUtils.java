package org.apache.atlas.performance.tools;

/**
 * Created by temp on 4/20/16.
 */
public class TimeUtils {

    public static String getFormattedTime(Long timeTaken){
        String ftime="";
        long totalTime =timeTaken;
        long millis=totalTime%1000;
        long time = totalTime / 1000;
        String seconds = Integer.toString((int)(time % 60));
        String minutes = Integer.toString((int)((time % 3600) / 60));
        String hours = Integer.toString((int)(time / 3600));

      /*  for (int ii = 0; ii < 2; ii++) {

            if (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            if (hours.length() < 2) {
                hours = "0" + hours;
            }
        }*/

        if(!minutes.equals("0"))
            ftime = ftime +minutes+" mins ";
        if(!seconds.equals("0"))
            ftime = ftime +seconds+ " secs ";
        ftime=ftime+millis+ " ms ";
        return ftime;
    }
}
