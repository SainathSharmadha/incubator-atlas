package org.apache.atlas.performance.tools;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by temp on 4/20/16.
 */
public class JMeterResponseCollector {
    private static Integer nusers,nloops,numQueriesPerSet,small_e,medium_e,large_e;
    private static String cpuUsageFile,responseFile;
    static void getConfiguredValues(){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("performance_tools/src/main/java/org.apache.atlas.performance.tools/resources/config.properties");
            prop.load(input);
            nusers=Integer.parseInt(prop.getProperty("nusers"));
            nloops=Integer.parseInt(prop.getProperty("nloops"));
            numQueriesPerSet=Integer.parseInt(prop.getProperty("numQueriesPerSet"));
            small_e=Integer.parseInt(prop.getProperty("small_e"));
            medium_e=Integer.parseInt(prop.getProperty("medium_e"));
            large_e=Integer.parseInt(prop.getProperty("large_e"));
            cpuUsageFile=prop.getProperty("cpuconsumpFile");
            responseFile=prop.getProperty("responseFile");


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String args[]) throws IOException, TransformerException, SAXException, ParserConfigurationException {

        getConfiguredValues();
        FileBuilder.createFiles(nusers);
        User.loopCount=nloops;
        QuerySet.setNumQueriesPerSet(numQueriesPerSet);
        ResultCollector rs=new ResultCollector(nusers,nloops,small_e,medium_e,large_e,cpuUsageFile,responseFile);
        rs.getResults();













    }
}
