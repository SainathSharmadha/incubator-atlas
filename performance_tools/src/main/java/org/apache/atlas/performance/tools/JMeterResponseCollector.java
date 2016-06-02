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

            input = new FileInputStream("performance_tools/src/main/java/org/apache/atlas/performance/tools/resources/config.properties");
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
        FileBuilder fileBuilder=new FileBuilder();
        //fileBuilder.createFiles(nusers,responseFile);
        User.loopCount=nloops;
        QuerySet.setNumQueriesPerSet(numQueriesPerSet);
        ResultCollector rs=new ResultCollector(nusers,nloops,small_e,medium_e,large_e,cpuUsageFile,responseFile);
        rs.getResults();













    }
}
