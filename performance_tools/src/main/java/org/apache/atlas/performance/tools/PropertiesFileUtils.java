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

    public static void main(String args) throws ConfigurationException, IOException {
        String perfConfDir = args;
        System.setProperty("atlas.perf.dir", perfConfDir);
        PropertiesFileReader.readPropertiesFile();
        calculateFromPropertiesFile();
    }
}
