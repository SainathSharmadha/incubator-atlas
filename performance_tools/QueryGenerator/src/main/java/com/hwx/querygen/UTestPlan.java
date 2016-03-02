package com.hwx.querygen;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;

/**
 * Created by temp on 3/2/16.
 */
public class UTestPlan {
    TestPlan testPlan;

    UTestPlan(String testPlanName)
    {
        testPlan=new TestPlan(testPlanName);



    }

    public TestPlan getTestPlan() {

        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());
        return testPlan;
    }
}
