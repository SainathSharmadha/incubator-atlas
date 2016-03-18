package org.apache.atlas.performance.tools;

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
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {

        final String ATLAS_SERVER="localhost";
        final Integer ATLAS_PORT=21000;
        final String JMETER_HOME="/Users//temp//SoftwareHWX//jmeter//apache-jmeter-2.13";
        final String TABLE_LIST_FILE="/Users/temp/TableGen/tables.txt";
        final String DSL_TABLE_TEMPLATE="/api/atlas/discovery/search/dsl?query=hive_table+where+name%3D%27";
        final String JMX_OUTPUT_FILE="/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/jmeter_api_sample.jmx";
        final String RESULT_XML_FILE="/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report.xml";
        final String single_quote="%27";
        File jmeterHome = new File(JMETER_HOME);
        String slash = System.getProperty("file.separator");

        File tablesFile=new File(TABLE_LIST_FILE);
        BufferedReader buff=new BufferedReader(new FileReader(tablesFile));

        if (jmeterHome.exists()) {

            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {
                StandardJMeterEngine jmeter = new StandardJMeterEngine();

                JMeterUtils.setJMeterHome(jmeterHome.getPath());
                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                JMeterUtils.initLogging();
                JMeterUtils.initLocale();

                HashTree MainTestPlanTree = new HashTree();


                TestPlan testPlan = new TestPlan("JMeter Test Plan");
                testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
                testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
                testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

                LoopController loopController = new LoopController();
                loopController.setLoops(1);
                loopController.setFirst(true);
                loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
                loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
                loopController.initialize();

                ThreadGroup threadGroup = new ThreadGroup();
                threadGroup.setName("Thread Group 1");
                threadGroup.setNumThreads(1);
                threadGroup.setRampUp(1);
                threadGroup.setSamplerController(loopController);
                threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
                threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
                String table = "";
                HashTree uhashTree1 = new HashTree();
                uhashTree1 = MainTestPlanTree.add(testPlan, threadGroup);
                HTTPSampler examplecomSampler1;


                /* Generates all the table querying API under 1 thread group as different HTTP samplers. Each Sampler for 1 table. Doing this and running
                * jmeter, produces JSON response for all tables in 1 XML file which can be parsed at 1 shot to get all Guids*/
                while ((table = buff.readLine()) != null) {
                    examplecomSampler1 = new HTTPSampler();
                    examplecomSampler1.setDomain(ATLAS_SERVER);
                    examplecomSampler1.setPort(ATLAS_PORT);
                    String query = DSL_TABLE_TEMPLATE + table.toLowerCase() + single_quote;
                    examplecomSampler1.setPath(query);
                    examplecomSampler1.setMethod("GET");
                    examplecomSampler1.setName("Get details of a Table given name");
                    examplecomSampler1.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
                    examplecomSampler1.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
                    uhashTree1.add(examplecomSampler1);

                }
                SaveService.saveTree(MainTestPlanTree, new FileOutputStream(JMX_OUTPUT_FILE));


                Summariser summer = null;


                String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
                if (summariserName.length() > 0) {
                    summer = new Summariser(summariserName);
                }


                //String reportFile = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report.xml";
                // String csvFile = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report.csv";


                ResultCollector logger = new ResultCollector(summer);
                logger.setFilename(RESULT_XML_FILE);
                //ResultCollector csvlogger = new ResultCollector(summer);
                //csvlogger.setFilename(csvFile);
                MainTestPlanTree.add(MainTestPlanTree.getArray()[0], logger);
                //MainTestPlanTree.add(MainTestPlanTree.getArray()[0], csvlogger);


                jmeter.configure(MainTestPlanTree);


                String command = JMETER_HOME + "/bin/jmeter -n -t" + " " + JMETER_HOME + "/jmeter_api_sample.jmx -l " + JMETER_HOME + "/report.xml";
                Runtime.getRuntime().exec("rm " + JMETER_HOME + "/report.xml");
                Runtime.getRuntime().exec(command);


                System.out.println("Test completed. See " + jmeterHome + slash + "report.xml file for results");
                System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "jmeter_api_sample.jmx");
           /*
           report.xml takes time to be generated completely. If immediately accessed,throws FileNotFound Exception.
           *//*
                   try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                GuidParser parser=new GuidParser();
                parser.parse();
            }


        }




        }

}

