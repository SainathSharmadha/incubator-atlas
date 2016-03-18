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
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryTestBuilder {

    private List<ThreadGroup> userSessions;
    private HashTree mainTestPlanTree;

    public static QueryTestBuilder newInstance() {
         return new QueryTestBuilder();
    }

    public QueryTestBuilder withUserSessions(int numSessions) {
        LoopController loopController = new LoopController();
        userSessions = new ArrayList<>();
        loopController.setLoops(1);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();

        for (int i = 0; i < numSessions; i++) {
            ThreadGroup threadGroup = new ThreadGroup();
            threadGroup.setName("tg"+(i+1));
            setThreadGroupProperties(threadGroup, loopController);
            userSessions.add(threadGroup);
        }
        return this;
    }

    public QueryTestBuilder withTestPlan() throws IOException {
        mainTestPlanTree = new HashTree();
        TestPlan testPlan = new TestPlan("JMeter to simulate 5 users querying thrice parallely");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        for (ThreadGroup threadGroup : userSessions) {
            HashTree testHashTree = mainTestPlanTree.add(testPlan, threadGroup);
            setSamplers(testHashTree);
        }
        SaveService.saveTree(mainTestPlanTree,
                new FileOutputStream("/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/jmeter_api_sample.jmx"));
        return this;
    }

    public QueryTestBuilder withResultGenerator() {
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
        mainTestPlanTree.add(mainTestPlanTree.getArray()[0], logger);
        mainTestPlanTree.add(mainTestPlanTree.getArray()[0], csvlogger);

        return this;
    }

    public StandardJMeterEngine getEngine() {
        final String JMETER_HOME = "/Users//temp//SoftwareHWX//jmeter//apache-jmeter-2.13";
        File jmeterHome = new File(JMETER_HOME);
        String fileSeparator = System.getProperty("file.separator");
        String jmeterPropertiesPath = jmeterHome.getPath() + fileSeparator + "bin" + fileSeparator + "jmeter.properties";
        File jmeterProperties = new File(jmeterPropertiesPath);

        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        JMeterUtils.setJMeterHome(jmeterHome.getPath());
        JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();

        jmeter.configure(mainTestPlanTree);

        return jmeter;
    }

    private void setSamplers(HashTree hashtree) {
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

    private void setThreadGroupProperties(ThreadGroup threadGroup, LoopController loopController) {
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
    }

}
