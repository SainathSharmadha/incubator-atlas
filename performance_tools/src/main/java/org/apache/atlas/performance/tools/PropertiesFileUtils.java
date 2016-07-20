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

import java.io.IOException;

public class PropertiesFileUtils {
    static Integer numTables, smallTables, mediumTables, largeTables, numCtasTables, smallCtasTables, mediumCtasTables, largeCtasTables, numTablesToTag;

    public static Integer getSmallTables() {
        return smallTables;
    }

    public static Integer getMediumTables() {
        return mediumTables;
    }

    public static Integer getLargeTables() {
        return largeTables;
    }

    public static Integer getSmallCtasTables() {
        return smallCtasTables;
    }

    public static Integer getMediumCtasTables() {
        return mediumCtasTables;
    }

    public static Integer getLargeCtasTables() {
        return largeCtasTables;
    }

    public static Integer getNumTables() {
        return numTables;
    }

    public static Integer getNumCtasTables() {
        return numCtasTables;
    }

    public static Integer getNumTablesToTag() {
        return numTablesToTag;
    }

    public static void calculateFromPropertiesFile() {
        numTables = PropertiesFileReader.getNumTables();
        Float smallTablePercentage = PropertiesFileReader.getSmallTablePercentage();
        Float mediumTablePercentage = PropertiesFileReader.getMediumTablePercentage();
        Float ctasTablePercentage = PropertiesFileReader.getCtasTablePercentage();
        smallTables = (int) ((smallTablePercentage / 100) * numTables);
        mediumTables = (int) ((mediumTablePercentage / 100) * numTables);
        numCtasTables = (int) ((ctasTablePercentage / 100) * numTables);
        smallCtasTables = (int) ((smallTablePercentage / 100) * numCtasTables);
        mediumCtasTables = (int) ((mediumTablePercentage / 100) * numCtasTables);
        Float tagPercentage = PropertiesFileReader.getTagPercentage();
        numTablesToTag = (int) (numTables * (tagPercentage / 100));
    }

    public static void changeNumTaggedTables(Integer nEntries) throws ConfigurationException {
PropertiesFileReader.propertiesConfiguration.setProperty("num.entries.tag.file",nEntries);
        PropertiesFileReader.propertiesConfiguration.save();
    }

    public static void main(String args) throws ConfigurationException, IOException {
        String perfConfDir = args;
        System.setProperty("atlas.perf.dir", perfConfDir);
        PropertiesFileReader.readPropertiesFile();
        calculateFromPropertiesFile();
    }
}
