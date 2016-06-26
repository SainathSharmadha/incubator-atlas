package org.apache.atlas.performance.tools.jmeter.run.scripts;

import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.AuthManager;
import org.apache.jmeter.protocol.http.control.Authorization;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.AuthPanel;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
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
import org.apache.jorphan.collections.ListedHashTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QueryTestBuilder {
    private ListedHashTree testPlanTree;
    private TestPlan testPlan;
    ListedHashTree threadGroupHashTree;
    private ThreadGroup threadGroup;
    private StandardJMeterEngine jmeter;
    private File jmeterHome,jmeterProperties;
    private File jmxOutputFile;
    private String resultFile;
    public LoopController loopController;
    public Integer nUsers,nLoops;

    public QueryTestBuilder withJmeterInitialized(){

        jmeter = new StandardJMeterEngine();
        jmeterHome=new File("/Users//temp//SoftwareHWX//jmeter//apache-jmeter-2.13");
        jmeterProperties=new File(jmeterHome.getPath()+"//bin//jmeter.properties");
        JMeterUtils.setJMeterHome(jmeterHome.getPath());
        JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
        System.out.println(jmeterProperties.getPath());
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();
        return this;
    }
    private HeaderManager getHeaderManager() {
        HeaderManager headerManager = new HeaderManager();
        Header header = new Header();
        header.setName("Content-Type");
        header.setValue("application/json");
        headerManager.add(header);
        headerManager.setProperty(HeaderManager.GUI_CLASS, HeaderPanel.class.getName());
        headerManager.setProperty(HeaderManager.TEST_CLASS, HeaderManager.class.getName());
        return headerManager;
    }

    public QueryTestBuilder withUserSessions(Integer nLoops,Integer nUsers) {

        loopController = new LoopController();
        loopController.setLoops(nLoops);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();

        threadGroup=new ThreadGroup();
        threadGroup.setName("Atlas Users");
        threadGroup.setNumThreads(nUsers);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        threadGroup.setSamplerController(loopController);


        return this;
    }



    private AuthManager getAuthorizationManager() {
        AuthManager authManager = new AuthManager();
        authManager.setProperty(TestElement.TEST_CLASS, AuthManager.class.getName());
        authManager.setProperty(TestElement.GUI_CLASS, AuthPanel.class.getName());
        Authorization authorization = new Authorization();
        authorization.setName("Atlas Login");
        authorization.setURL("http://localhost:21000");
        authorization.setUser("admin");
        authorization.setPass("admin");
        authManager.setName("Authorization");
        authManager.addAuth(authorization);
        return authManager;
    }
    public QueryTestBuilder withTestPlan() throws IOException {
        testPlanTree = new ListedHashTree();
        testPlan = new TestPlan("Create JMeter Script From Java Code");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());
        AuthManager authManager=getAuthorizationManager();
        testPlanTree.add(testPlan);
        threadGroupHashTree = (ListedHashTree)testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(authManager);
        QueryRepository qr=new QueryRepository(threadGroupHashTree);
        return this;

    }

    public QueryTestBuilder withResultGenerator() throws IOException {
        SaveService.saveTree(testPlanTree, new FileOutputStream(PropertiesFileReader.getOutputDir()+"/TestPlan.jmx"));
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }
        String reportFile = PropertiesFileReader.getOutputDir()+"/"+PropertiesFileReader.getJmeterResponseFile();

        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(reportFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);
        return this;
    }


    public StandardJMeterEngine getEngine() throws IOException {
        jmeter.configure(testPlanTree);
        return jmeter;
    }





}
