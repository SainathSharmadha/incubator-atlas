package org.apache.atlas.performance.tools;


import org.apache.atlas.performance.tools.table.generator.TableGenerator;
import org.apache.atlas.performance.tools.tables.time.calculator.CalculateTime;
import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;

public class TestSuiteRunner {
    static void runPreDataCreationSuite() throws IOException, ConfigurationException {
        TableGenerator tableGenerator=new TableGenerator();
        tableGenerator.generateOutputFile();

    }

    static void runPostDataCreationSuite() throws IOException, ConfigurationException {
        CalculateTime.getTestPlanTables();


    }

public static void main(String args[]) throws IOException, ConfigurationException {
    String perfConfDir = args[0];
    System.setProperty("atlas.perf.dir", perfConfDir);
    PropertiesFileReader.readPropertiesFile();
    PropertiesFileUtils.calculateFromPropertiesFile();
    runPreDataCreationSuite();
    runPostDataCreationSuite();


}
}
