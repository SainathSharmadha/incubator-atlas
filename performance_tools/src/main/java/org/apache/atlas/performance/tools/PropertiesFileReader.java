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

package org.apache.atlas.performance.tools;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;

public class PropertiesFileReader {

    static Integer numTables;
    static Float smallTablePercentage,mediumTablePercentage,ctasTablePercentage;
    static String outputDir;

    static void readPropertiesFile() throws ConfigurationException {

        File confFile=new File("incubator-atlas/performance_tools/src/main/java/org/apache/atlas/performance/tools/conf/atlas-perf-test.properties");

        PropertiesConfiguration propertiesConfiguration=new PropertiesConfiguration(confFile);

        numTables=Integer.parseInt((String)propertiesConfiguration.getProperty("num.tables"));
        smallTablePercentage=Float.parseFloat((String)propertiesConfiguration.getProperty("small.table.percentage"));
        mediumTablePercentage=Float.parseFloat((String)propertiesConfiguration.getProperty("medium.table.percentage"));
        ctasTablePercentage=Float.parseFloat((String)propertiesConfiguration.getProperty("ctas.table.percentage"));
        outputDir=(String)propertiesConfiguration.getProperty("output.file.dir");

    }


    public static Integer getNumTables() {
        return numTables;
    }

    public static Float getSmallTablePercentage() {
        return smallTablePercentage;
    }

    public static Float getMediumTablePercentage() {
        return mediumTablePercentage;
    }

    public static Float getCtasTablePercentage() {
        return ctasTablePercentage;
    }

    public static String getOutputDir() {
        return outputDir;
    }
}
