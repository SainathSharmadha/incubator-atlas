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
import java.util.ArrayList;


public class PropertiesFileReader {
    static Integer numTables, numQueriesPerSet, smallTablesLast, mediumTablesLast, largeTablesLast, numTags, numTestPlanTables;
    static Float smallTablePercentage, mediumTablePercentage, ctasTablePercentage, tagPercentage;
    static String atlasLogFile, database, cluster, outputDir, cpuFile, jmeterResponseFile, jmeterHome, jmeterPropertiesFile,domain;
    static String[] numUsers,numLoops;
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
        numUsers =propertiesConfiguration.getStringArray("num.users");
        numLoops = propertiesConfiguration.getStringArray("num.loops");
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
        File output = new File(outputDir);
        if (!output.exists()) {
            new File(outputDir).mkdir();
        }
        jmeterHome = (String) propertiesConfiguration.getProperty("jmeter.home");
        jmeterPropertiesFile = (String) propertiesConfiguration.getProperty("jmeter.properties");
        domain = (String) propertiesConfiguration.getProperty("domain");

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

    public static Integer[] getNumUsers() {
        Integer usersList[] = new Integer[numUsers.length];
        for (int i = 0; i < numUsers.length; i++) {
            usersList[i] = Integer.parseInt(numUsers[i]);
        }
        return usersList;

    }
    public static Integer[] getNumLoops() {
        Integer loopsList[]=new Integer[numLoops.length];
        for(int i=0;i<numLoops.length;i++){
            loopsList[i]=Integer.parseInt(numLoops[i]);
        }
        return  loopsList;
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

    public static Integer getNumTags() {
        return numTags;
    }

    public static Float getTagPercentage() {
        return tagPercentage;
    }


    public static Integer getNumTestPlanTables() {
        return numTestPlanTables;
    }

    public static String getAtlasLogFile() {
        return atlasLogFile;
    }

    public static String getDatabase() {
        return database;
    }

    public static String getCluster() {
        return cluster;
    }

    public static String getJmeterHome() {
        return jmeterHome;
    }

    public static String getJmeterPropertiesFile() {
        return jmeterPropertiesFile;
    }

    public static String getDomain() {
        return domain;
    }
}
