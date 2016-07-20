package org.apache.atlas.performance.tools;


import org.apache.atlas.performance.tools.jmeter.run.scripts.QueryRunner;
import org.apache.atlas.performance.tools.response.data.parser.JMeterResponseCollector;
import org.apache.atlas.performance.tools.table.generator.TableGenerator;
import org.apache.atlas.performance.tools.tables.time.calculator.CalculateTime;
import org.apache.commons.configuration.ConfigurationException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static org.apache.atlas.performance.tools.TestSuiteRunner.cleanResultFolder;

public class TestSuiteRunner {
    static void runPreDataCreationSuite() throws IOException, ConfigurationException {
        TableGenerator tableGenerator = new TableGenerator();
        tableGenerator.generateOutputFile();

    }

    static void runPostDataCreationSuite() throws IOException, ConfigurationException, InterruptedException, ParserConfigurationException, SAXException, ParseException, TransformerException {
        CalculateTime.getTestPlanTables();
        QueryRunner.run();
        JMeterResponseCollector.collectResults();

    }

    public static void main(String args[]) throws IOException, ConfigurationException, InterruptedException, ParserConfigurationException, SAXException, ParseException, TransformerException {
        String perfConfDir = args[0];
        System.setProperty("atlas.perf.dir", perfConfDir);
        PropertiesFileReader.readPropertiesFile();
        PropertiesFileUtils.calculateFromPropertiesFile();
       cleanResultFolder();
        runPreDataCreationSuite();
        //runPostDataCreationSuite();

    }

    static void cleanResultFolder(){
      File resultFolder=new File(PropertiesFileReader.getOutputDir());
        File[] files=resultFolder.listFiles();
        for(int i=0;i<files.length;i++){
            files[i].delete();
        }

    }
}
