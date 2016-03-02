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
import java.io.*;
import org.apache.jorphan.collections.HashTree;



public class UHTTPSampler {

 //   HTTPSampler example = new HTTPSampler();
    HTTPSampler getUHTTPSamplerQ2()
    {
        HTTPSampler examplecomSampler = new HTTPSampler();
        examplecomSampler.setDomain("localhost");
        examplecomSampler.setPort(21000);
        examplecomSampler.setPath(new Table_Given_Name().generateQuery());
        examplecomSampler.setMethod("GET");
        examplecomSampler.setName("Get the table given name");
        examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        return examplecomSampler;
    }

 HTTPSampler getUHTTPSamplerQ1()
 {
     DB_Given_Name dbgn=new DB_Given_Name();
     HTTPSampler examplecomSampler = new HTTPSampler();
     examplecomSampler.setDomain("localhost");
     examplecomSampler.setPort(21000);
     examplecomSampler.setPath(new DB_Given_Name().generateQuery());
     examplecomSampler.setMethod("GET");
     examplecomSampler.setName("Get details of a DB given name");
     examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
     examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
     return examplecomSampler;

 }
    HTTPSampler getUHTTPSamplerQ3()
    {

        HTTPSampler examplecomSampler = new HTTPSampler();
        examplecomSampler.setDomain("localhost");
        examplecomSampler.setPort(21000);
        examplecomSampler.setPath(new Get_All_Cols().generateQuery());
        examplecomSampler.setMethod("GET");
        examplecomSampler.setName("Get all columns of a table");
        examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        return examplecomSampler;

    }



}
