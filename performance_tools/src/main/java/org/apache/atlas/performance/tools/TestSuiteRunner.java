/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        String test=args[1];
        System.setProperty("atlas.perf.dir", perfConfDir);
        PropertiesFileReader.readPropertiesFile();
        PropertiesFileUtils.calculateFromPropertiesFile();
        cleanResultFolder();
        if(test.equals("pretest"))
        runPreDataCreationSuite();
        else
        runPostDataCreationSuite();

    }

    static void cleanResultFolder() {
        File resultFolder = new File(PropertiesFileReader.getOutputDir());
        File[] files = resultFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }

    }
}
