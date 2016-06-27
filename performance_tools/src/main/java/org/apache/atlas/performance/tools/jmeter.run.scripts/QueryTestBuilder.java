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
        System.out.println(PropertiesFileReader.getJmeterHome());
        jmeterHome=new File(PropertiesFileReader.getJmeterHome());
        jmeterProperties=new File(PropertiesFileReader.getJmeterPropertiesFile());
        JMeterUtils.setJMeterHome(jmeterHome.getPath());
        JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
        System.out.println(jmeterProperties.getPath());
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();
        return this;
    }


    public QueryTestBuilder withUserSessions(Integer nUsers,Integer nLoops) {

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

        this.nLoops=nLoops;
        this.nUsers=nUsers;


        return this;
    }

    private AuthManager getAuthorizationManager() {
        AuthManager authManager = new AuthManager();
        authManager.setProperty(TestElement.TEST_CLASS, AuthManager.class.getName());
        authManager.setProperty(TestElement.GUI_CLASS, AuthPanel.class.getName());
        Authorization authorization = new Authorization();
        authorization.setName("Atlas Login");
        authorization.setURL("http://"+PropertiesFileReader.getDomain()+":21000");
        authorization.setUser("admin");
        authorization.setPass("admin");
        authManager.setName("Authorization");
        authManager.addAuth(authorization);
        return authManager;
    }
    public QueryTestBuilder withTestPlan(String testplan) throws IOException {
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
        if(testplan.equals("Post Tags")) {
            System.out.println("Posting tags");
            qr.getPostTags();
            resultFile="PostTagsResponse.xml";
        }
        else if (testplan.equals("Associate Tags")){
            System.out.println("Associating tags to entites");
            qr.getAssociateTagsQueries();
            resultFile="AssociateTagsResponse.xml";

        }
        else if (testplan.equals("Test User Queries")){
            System.out.println("Testing user queries");
            qr.testUserQueries();
            resultFile=String.format("ResponseData-%du-%dl.xml",nUsers,nLoops);

        }

        return this;

    }

    public QueryTestBuilder withResultGenerator() throws IOException {
        SaveService.saveTree(testPlanTree, new FileOutputStream(PropertiesFileReader.getOutputDir()+"/TestPlan.jmx"));
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }
       String reportFile = PropertiesFileReader.getOutputDir()+"/"+resultFile;

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
