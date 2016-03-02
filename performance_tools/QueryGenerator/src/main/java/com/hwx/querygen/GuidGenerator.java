package com.hwx.querygen;

import com.mongodb.DB;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by temp on 3/3/16.
 */
public class GuidGenerator {

    static ArrayList<String> databases = new ArrayList<String>();

    public static void getAllDBs() throws IOException {


       /* Runtime.getRuntime().exec("sh listDB.sh");
        System.out.println("Script Executed");
        try {
            //thread to sleep for the specified number of milliseconds
            Thread.sleep(20000);
        } catch ( java.lang.InterruptedException ie) {
            System.out.println(ie);
        }*/
        File dbs = new File("databases.txt");
        BufferedReader buff = new BufferedReader(new FileReader(dbs));
        String db = "";
        while ((db = buff.readLine()) != null) {
            databases.add(db);
        }

        File tables = new File("tables.txt");
        buff = new BufferedReader(new FileReader(tables));
        String table = "";

      //  Runtime.getRuntime().exec("sh listAllTables.sh");



        File jmeterHome = new File("/Users//temp//SoftwareHWX//jmeter//apache-jmeter-2.13");
        String slash = System.getProperty("file.separator");

        if (jmeterHome.exists()) {

            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {
                //JMeter Engine
                StandardJMeterEngine jmeter = new StandardJMeterEngine();

                //JMeter initialization (properties, log levels, locale, etc)
                JMeterUtils.setJMeterHome(jmeterHome.getPath());
                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
                JMeterUtils.initLocale();

                // JMeter Test Plan, basically JOrphan HashTree
                HashTree testPlanTree = new HashTree();


                HTTPSampler examplecomSampler = new HTTPSampler();
                examplecomSampler.setDomain("localhost");
                examplecomSampler.setPort(21000);

        //        examplecomSampler.setPath("/api/atlas/discovery/search/dsl?query=hive_table+where+name%3D%27default.table_5@cluster1%27");
                examplecomSampler.setMethod("GET");
                examplecomSampler.setName("Get all tables of all db default");
                examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
                examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());




                // Loop Controller
                LoopController loopController = new LoopController();
                loopController.setLoops(1);
                loopController.setFirst(true);
                loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
                loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
                loopController.initialize();




                ThreadGroup threadGroup = new ThreadGroup();
                threadGroup.setName("Sample Thread Group");
                threadGroup.setNumThreads(1);
                threadGroup.setRampUp(1);
                threadGroup.setSamplerController(loopController);
                threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
                threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());


                TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");

                testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
                testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
                testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());


                HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
String query="";
                while ((table = buff.readLine()) != null) {
                    query="/api/atlas/discovery/search/dsl?query=hive_table+where+name%3D%27default."+table+"@cluster1%27";
                    examplecomSampler.setPath(query);
                    threadGroupHashTree.add(examplecomSampler);
                }






                SaveService.saveTree(testPlanTree, new FileOutputStream("/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/jmeter_table_sample.jmx"));

                //add Summarizer output to get test progress in stdout like:
                // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
                Summariser summer = null;


                String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
                if (summariserName.length() > 0) {
                    summer = new Summariser(summariserName);
                }


                // Store execution results into a .jtl file, we can save file as csv also
                String reportFile = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report1.jtl";
                String csvFile = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report1.csv";


                ResultCollector logger = new ResultCollector(summer);
                logger.setFilename(reportFile);
                ResultCollector csvlogger = new ResultCollector(summer);
                csvlogger.setFilename(csvFile);
                testPlanTree.add(testPlanTree.getArray()[0], logger);
                testPlanTree.add(testPlanTree.getArray()[0], csvlogger);
                // Run Test Plan
                jmeter.configure(testPlanTree);
                //jmeter.run();
                String path = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13";

                String command = path + "/bin/jmeter -n -t" + " " + path + "/jmeter_api_sample.jmx -l " + path + "/report.jtl";
                Runtime.getRuntime().exec("rm " + path + "/report.jtl");
                Runtime.getRuntime().exec(command);
                //Runtime.getRuntime().exec("cat "+path+"report.jtl");
              /*  File f=new File(path+"/report.jtl");
                BufferedReader buff=new BufferedReader(new FileReader(f));
                String line="";
                while((line=buff.readLine())!=null) {
                System.out.print(line);
                }*/

                System.out.println("Test completed. See " + jmeterHome + slash + "report.jtl file for results");
                System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "jmeter_api_sample.jmx");
                System.exit(0);

            }
        }

        System.err.println("jmeterHome property is not set or pointing to incorrect location");
        System.exit(1);

    }
}