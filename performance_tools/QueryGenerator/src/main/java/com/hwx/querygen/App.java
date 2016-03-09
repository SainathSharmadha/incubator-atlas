package com.hwx.querygen;

/**
 * Hello world!
 *
 */

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
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class App
{
    public static void main( String[] args ) throws IOException {

        final String DOMAIN="localhost";
        final  String PORT="21000";
        final  String PATH="/Users//temp//SoftwareHWX//jmeter//apache-jmeter-2.13";
        File jmeterHome = new File(PATH);
        String slash = System.getProperty("file.separator");

        if (jmeterHome.exists()) {
            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {
                StandardJMeterEngine jmeter = new StandardJMeterEngine();

                JMeterUtils.setJMeterHome(jmeterHome.getPath());
                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                JMeterUtils.initLogging();
                JMeterUtils.initLocale();

                HashTree MainTestPlanTree = new HashTree();
                TestPlan testPlan=new TestPlan("JMeter to simulate 5 users querying thrice parallely");
                testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
                testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
                testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

                LoopController loopController = new LoopController();
                loopController.setLoops(1);
                loopController.setFirst(true);
                loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
                loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
                loopController.initialize();

                ThreadGroup tg1=new ThreadGroup();
                tg1.setName("tg1");
                setThreadGroupProperties(tg1,loopController);
                ThreadGroup tg2=new ThreadGroup();
                tg2.setName("tg2");
                setThreadGroupProperties(tg2,loopController);
                ThreadGroup tg3=new ThreadGroup();
                tg3.setName("tg3");
                setThreadGroupProperties(tg3,loopController);
                ThreadGroup tg4=new ThreadGroup();
                setThreadGroupProperties(tg4,loopController);
                tg4.setName("tg4");
                ThreadGroup tg5=new ThreadGroup();
                setThreadGroupProperties(tg5,loopController);
                tg5.setName("tg5");

                HashTree uhashTree1=new HashTree();
                uhashTree1=MainTestPlanTree.add(testPlan,tg1);
                setSamplers(uhashTree1);

                HashTree uhashTree2=new HashTree();
                uhashTree2=MainTestPlanTree.add(testPlan,tg2);
                setSamplers(uhashTree2);

                HashTree uhashTree3=new HashTree();
                uhashTree3=MainTestPlanTree.add(testPlan,tg3);
                setSamplers(uhashTree3);


                HashTree uhashTree4=new HashTree();
                uhashTree4=MainTestPlanTree.add(testPlan,tg4);
                setSamplers(uhashTree4);

                HashTree uhashTree5=new HashTree();
                uhashTree5=MainTestPlanTree.add(testPlan,tg5);
                setSamplers(uhashTree5);




                SaveService.saveTree(MainTestPlanTree, new FileOutputStream("/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/jmeter_api_sample.jmx"));


                Summariser summer = null;


                String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
                if (summariserName.length() > 0) {
                    summer = new Summariser(summariserName);
                }



                String reportFile = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report.xml";
                String csvFile = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report.csv";


                ResultCollector logger = new ResultCollector(summer);
                logger.setFilename(reportFile);
                ResultCollector csvlogger = new ResultCollector(summer);
                csvlogger.setFilename(csvFile);
                MainTestPlanTree.add(MainTestPlanTree.getArray()[0], logger);
                MainTestPlanTree.add(MainTestPlanTree.getArray()[0], csvlogger);


                jmeter.configure(MainTestPlanTree);
                String path = "/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13";

                jmeter.run();


                System.out.println("Test completed. See " + jmeterHome + slash + "report.xml file for results");
                System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "jmeter_api_sample.jmx");
                System.exit(0);

            }
    }
        System.err.println("jmeterHome property is not set or pointing to incorrect location");
        System.exit(1);
    }
    static void setThreadGroupProperties(ThreadGroup threadGroup,LoopController loopController)
    {
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
    }

    static void setSamplers(HashTree hashtree)
    {
        HTTPSampler examplecomSampler = new HTTPSampler();
        examplecomSampler.setDomain("localhost");
        examplecomSampler.setPort(21000);
        examplecomSampler.setPath(new DB_Given_Name().generateQuery());
        examplecomSampler.setMethod("GET");
        examplecomSampler.setName("Get details of a DB given name");
        examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        hashtree.add(examplecomSampler);

        HTTPSampler examplecomSampler1 = new HTTPSampler();
        examplecomSampler1.setDomain("localhost");
        examplecomSampler1.setPort(21000);
        examplecomSampler1.setPath(new Table_Given_Name().generateQuery());
        examplecomSampler1.setMethod("GET");
        examplecomSampler1.setName("Get details of a Table given name");
        examplecomSampler1.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        examplecomSampler1.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        hashtree.add(examplecomSampler1);

        HTTPSampler examplecomSampler2 = new HTTPSampler();
        examplecomSampler2.setDomain("localhost");
        examplecomSampler2.setPort(21000);
        examplecomSampler2.setPath(new Get_All_Cols().generateQuery());
        examplecomSampler2.setMethod("GET");
        examplecomSampler2.setName("Get all columns of a table");
        examplecomSampler2.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        examplecomSampler2.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        hashtree.add(examplecomSampler2);
    }
}
