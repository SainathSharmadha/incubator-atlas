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

package org.apache.atlas.performance.tools.table.generator;

import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.atlas.performance.tools.PropertiesFileUtils;
import org.apache.commons.configuration.ConfigurationException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TableGenerator {
    BufferedWriter bufferedWriter;

    final String[] HIVE_DATA_TYPES = {"TINYINT", "SMALLINT", "BIGINT", "INT", "FLOAT", "DOUBLE", "DECIMAL", "TIMESTAMP", "DATE", "STRING", "VARCHAR(10)", "CHAR(10)", "BOOLEAN", "BINARY"};

    private String getDatatype() {
        int randomNum = new Random().nextInt(HIVE_DATA_TYPES.length);
        String dataType = HIVE_DATA_TYPES[randomNum];
        return dataType;
    }

    /* start to end is the range of tables. columns is then number of columns */
    void generateTables(int start, int end, int columns) throws IOException {
        String datatype;
        String strTable = "table_", strColumn = "_col_";
        String str = "", createStatement = "";
        for (int i = start; i <= end; i++) {
            for (int col = 1; col <= columns; col++) {
                datatype = getDatatype();
                if (col == 1)
                    str = String.format("%s%d%s%d %s", strTable, i, strColumn, col, datatype);
                else
                    str = String.format("%s,%s%d%s%d %s", str, strTable, i, strColumn, col, datatype);

            }
            createStatement = String.format("create table %s%d(%s)", strTable, i, str);
            bufferedWriter.write(createStatement + "\n");

        }

    }

    /* start - end is the range of number of tables. tableStart - tableEnd is the range of tables
     * example : start = 1
     *           end = 100 ,
     *           tableStart= 1500
     *           tableEnd=2000
     *           100 tables within range 15000 to 2000 will be generated */
    void generateCtasTables(int start, int end, int tableStart, int tableEnd) throws IOException {
        ArrayList<Integer> tables = new ArrayList<Integer>();
        String str;
        int randTable = 0;
        for (int i = start; i <= end; ) {
            randTable = new Random().nextInt((tableEnd - tableStart) + 1) + tableStart;
            if (!tables.contains(randTable)) {
                tables.add(randTable);
                str = String.format("create table table_%d_ctas as select * from table_%d", randTable, randTable);
                bufferedWriter.write(str + "\n");
                i++;
            }
        }


    }

    public void generateOutputFile() throws IOException, ConfigurationException {


        Integer numTables = PropertiesFileUtils.getNumTables();
        String outputDir = PropertiesFileReader.getOutputDir();


        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists())
            outputDirFile.mkdir();
        String regularFile = String.format("%s/%s%d%s", outputDir, "tables-", numTables, ".txt");
        String ctasFile = String.format("%s/%s%d%s", outputDir, "tables-", numTables, "-ctas.txt");
        new File(regularFile).createNewFile();
        new File(ctasFile).createNewFile();


        Integer smallTables, mediumTables;
        smallTables = PropertiesFileUtils.getSmallTables();
        mediumTables = PropertiesFileUtils.getMediumTables();
        bufferedWriter = new BufferedWriter(new FileWriter(regularFile));
        generateTables(1, smallTables, 10);
        generateTables(smallTables + 1, smallTables + mediumTables, 50);
        generateTables(smallTables + mediumTables + 1, numTables, 100);

        bufferedWriter.flush();
        bufferedWriter.close();


        Integer numCtasTables, smallCtasTables, mediumCtasTables;
        numCtasTables = PropertiesFileUtils.getNumCtasTables();
        smallCtasTables = PropertiesFileUtils.getSmallCtasTables();
        mediumCtasTables = PropertiesFileUtils.getMediumCtasTables();
        bufferedWriter = new BufferedWriter(new FileWriter(ctasFile));
        generateCtasTables(1, smallCtasTables, 1, smallTables);
        generateCtasTables(smallCtasTables + 1, smallCtasTables + mediumCtasTables, smallTables + 1, smallTables + mediumTables);
        generateCtasTables(smallCtasTables + mediumCtasTables + 1, numCtasTables, smallTables + mediumTables + 1, numTables);

        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static void main(String[] args) throws IOException, ConfigurationException {
        String perfConfDir = args[0];
        System.setProperty("atlas.perf.dir", perfConfDir);
        PropertiesFileReader.readPropertiesFile();
        PropertiesFileUtils.main(perfConfDir);
        TableGenerator tableGenerator = new TableGenerator();
        tableGenerator.generateOutputFile();

    }

}
