package com.hwx.querygen;


import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.gui.action.Save;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.reporters.ResultSaver;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;

import org.apache.jorphan.collections.HashTree;

import java.io.*;

/**
 * Hello world!
 *
 */
public class App 
{
    final static String DOMAIN="localhost";
    final static String PORT="21000";

   // final static String DSL_FULL_TEMPLATE=DOMAIN+":"+PORT+DSL_TEMPLATE;

    public static void main( String[] args ) throws IOException {


        File jmeterHome = new File("/Users//temp//SoftwareHWX//jmeter//apache-jmeter-2.13");
        String slash = System.getProperty("file.separator");
GuidGenerator.getAllDBs();
        if (!jmeterHome.exists()) {
            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {
                StandardJMeterEngine jmeter = new StandardJMeterEngine();




                JMeterUtils.setJMeterHome(jmeterHome.getPath());
                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
                JMeterUtils.initLocale();

                HashTree MainTestPlanTree = new HashTree();

                for(Integer user=1;user<=5;user++) {
                    UHashTree userHashTree = new UHashTree();
                    userHashTree.setHashTree(MainTestPlanTree, "User_"+user+"Running 3 tests", "ThreadGroup_"+user);
                }
             /*   UHashTree userHashTree2=new UHashTree();
                userHashTree2.setHashTree(MainTestPlanTree,"User_2 Running 3 tests","User2");

                UHashTree userHashTree3=new UHashTree();
                userHashTree3.setHashTree(MainTestPlanTree,"User_3 Running 3 tests","User3");
*/


                // save generated test plan to JMeter's .jmx file format
                SaveService.saveTree(MainTestPlanTree, new FileOutputStream("/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/jmeter_api_sample.jmx"));

                //add Summarizer output to get test progress in stdout like:
                // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
                Summariser summer = null;


                String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
                if (summariserName.length() > 0) {
                    summer = new Summariser(summariserName);
                }


                // Store execution results into a .jtl file, we can save file as csv also
                String reportFile = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report.xml";
                String csvFile = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report.csv";


                ResultCollector logger = new ResultCollector(summer);
                logger.setFilename(reportFile);
                ResultCollector csvlogger = new ResultCollector(summer);
                csvlogger.setFilename(csvFile);
                MainTestPlanTree.add(MainTestPlanTree.getArray()[0], logger);
                MainTestPlanTree.add(MainTestPlanTree.getArray()[0], csvlogger);
                // Run Test Plan

                jmeter.configure(MainTestPlanTree);
                //jmeter.run();
                String path = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13";

                String command = path + "/bin/jmeter -n -t" + " " + path + "/jmeter_api_sample.jmx -l " + path + "/report.xml";
                Runtime.getRuntime().exec("rm " + path + "/report.xml");
                Runtime.getRuntime().exec(command);
                //Runtime.getRuntime().exec("cat "+path+"report.");

                System.out.println("Test completed. See " + jmeterHome + slash + "report.xml file for results");
                System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "jmeter_api_sample.jmx");
                System.exit(0);

            }
        }
        System.err.println("jmeterHome property is not set or pointing to incorrect location");
        System.exit(1);
    }
}
