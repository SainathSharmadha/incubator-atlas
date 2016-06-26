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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.io.IOException;


public class PropertiesFileReader {
    static Integer numTables, numUsers, numLoops, numQueriesPerSet, smallTablesLast, mediumTablesLast, largeTablesLast, numTags, numTestPlanTables;
    static Float smallTablePercentage, mediumTablePercentage, ctasTablePercentage, tagPercentage;
    static String atlasLogFile, database, cluster, outputDir, cpuFile, jmeterResponseFile,datePattern;
    static PropertiesConfiguration propertiesConfiguration;

    public static void readPropertiesFile() throws ConfigurationException, IOException {

        File confFile = new File(System.getProperty("atlas.perf.dir"));
        propertiesConfiguration = new PropertiesConfiguration(confFile);
        numTables = Integer.parseInt((String) propertiesConfiguration.getProperty("num.tables"));
        smallTablePercentage = Float.parseFloat((String) propertiesConfiguration.getProperty("small.table.percentage"));
        mediumTablePercentage = Float.parseFloat((String) propertiesConfiguration.getProperty("medium.table.percentage"));
        ctasTablePercentage = Float.parseFloat((String) propertiesConfiguration.getProperty("ctas.table.percentage"));
        tagPercentage = Float.parseFloat((String) propertiesConfiguration.getProperty("tag.percentage"));
        outputDir = (String) propertiesConfiguration.getProperty("output.file.dir");
        numUsers = Integer.parseInt((String) propertiesConfiguration.getProperty("num.users"));
        numLoops = Integer.parseInt((String) propertiesConfiguration.getProperty("num.loops"));
        numQueriesPerSet = Integer.parseInt((String) propertiesConfiguration.getProperty("num.queries.per.set"));
        smallTablesLast = Integer.parseInt((String) propertiesConfiguration.getProperty("small.tables.end"));
        mediumTablesLast = Integer.parseInt((String) propertiesConfiguration.getProperty("medium.tables.end"));
        largeTablesLast = Integer.parseInt((String) propertiesConfiguration.getProperty("large.tables.end"));
        numTags = Integer.parseInt((String) propertiesConfiguration.getProperty("num.tags"));
        cpuFile = (String) propertiesConfiguration.getProperty("cpu.consumption.file");
        jmeterResponseFile = (String) propertiesConfiguration.getProperty("jmeter.response.file");
        atlasLogFile = (String) propertiesConfiguration.getProperty("atlas.log.file");
        database = (String) propertiesConfiguration.getProperty("database");
        cluster = (String) propertiesConfiguration.getProperty("cluster");
        numTestPlanTables = Integer.parseInt((String) propertiesConfiguration.getProperty("num.test.plan.tables"));
        datePattern=(String) propertiesConfiguration.getProperty("datePattern");
        File output=new File(outputDir);
        if (!output.exists()) {
            new File(outputDir).mkdir();
        }
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

    public static Integer getNumUsers() {
        return numUsers;
    }

    public static Integer getNumLoops() {
        return numLoops;
    }

    public static Integer getNumQueriesPerSet() {
        return numQueriesPerSet;
    }

    public static Integer getSmallTablesLast() {
        return smallTablesLast;
    }

    public static Integer getMediumTablesLast() {
        return mediumTablesLast;
    }

    public static Integer getLargeTablesLast() {
        return largeTablesLast;
    }

    public static String getCpuFile() {
        return cpuFile;
    }

    public static String getJmeterResponseFile() {
        return jmeterResponseFile;
    }

    public static PropertiesConfiguration getPropertiesConfiguration() {
        return propertiesConfiguration;
    }

    public static Integer getNumTags() {return numTags;}

    public static Float getTagPercentage() {return tagPercentage;}


    public static Integer getNumTestPlanTables() {return numTestPlanTables;}

    public static String getAtlasLogFile() {return atlasLogFile;}

    public static String getDatabase() {return database;}

    public static String getCluster() {return cluster;}
}
