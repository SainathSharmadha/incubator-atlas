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
package org.apache.atlas.performance.tools.tables.time.calculator;


import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.atlas.performance.tools.PropertiesFileUtils;
import org.apache.atlas.performance.tools.tag.creator.TagCreator;
import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CalculateTime {

    static File regularTableFile, ctasTableFile, testPlanTablesFile,resultsFile;
    static BufferedWriter resultWriter;

    public static void calculateTime(File file, String tableFormat, boolean isCTAS) throws ConfigurationException, IOException, ParseException {
        boolean ctasTablesPresent=false;
        Integer firstTable,lastTable;
        firstTable=0;
        lastTable=0;
        Table[] tables = new Table[PropertiesFileReader.getNumTables() + 1];
        BufferedReader buff = new BufferedReader(new FileReader(new File(PropertiesFileReader.getAtlasLogFile())));
        String responseData;
        String datePattern = "(([2][0-9][0-9][0-9]-[0-2][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9]),([0-9]*))";
        String tableNamePattern = "\"(.*)\"";
        String strStartPattern = String.format("%s(.*)Read message:(.*)\"MANAGED_TABLE\",\"name\":%s,\"createTime\"(.*)\"", datePattern, tableNamePattern);
        String strEndPattern = datePattern + "(.*)Sending message for topic ATLAS_ENTITIES: (.*)\\(KafkaNotification:(.*)";
        Pattern startPattern = Pattern.compile(strStartPattern);
        Pattern endPattern = Pattern.compile(strEndPattern);
        String tableName, startTime;
        Integer tableNo;
        Pattern tablePattern = Pattern.compile(tableFormat);
        Matcher startMatcher, endMatcher, tableNameMatcher;
        ArrayList<Table> tableArrayList = new ArrayList<Table>();
        while ((responseData = buff.readLine()) != null) {
            startMatcher = startPattern.matcher(responseData);
            endMatcher = endPattern.matcher(responseData);
            if (startMatcher.find()) {
                startTime = startMatcher.group(1);
                tableName = startMatcher.group(6);
                tableNameMatcher = tablePattern.matcher(tableName);
                if (tableNameMatcher.find()) {
                    ctasTablesPresent=true;
                    tableNo = Integer.parseInt(tableNameMatcher.group(1));
                    if (tables[tableNo] == null) {
                        if(firstTable==0)
                            firstTable=tableNo;
                        tables[tableNo] = new Table();
                        tables[tableNo].setTableno(tableNo);
                        tables[tableNo].setTableName(tableName);
                        tables[tableNo].setStartTime(startTime);
                        tableArrayList.add(tables[tableNo]);
                    }

                }
            } else if (endMatcher.find()) {

                String endTime = endMatcher.group(1);
                String jsonMessage = endMatcher.group(5);

                JSONObject obj = new JSONObject(jsonMessage);

                try {
                    String guid = "";
                    String type = obj.getJSONObject("message").getJSONObject("entity").getString("typeName");
                    if (type.equals("hive_table")) {
                        JSONObject objId = obj.getJSONObject("message").getJSONObject("entity").getJSONObject("id");
                        guid = objId.getString("id");
                        JSONObject objValue = obj.getJSONObject("message").getJSONObject("entity").getJSONObject("values");
                        String table = objValue.getString("name");
                        tableNameMatcher = tablePattern.matcher(table);
                        if (tableNameMatcher.find()) {
                            tableNo = Integer.parseInt(tableNameMatcher.group(1));
                            if(tables[tableNo].getEndTime()==null) {
                                lastTable = tableNo;
                                tables[tableNo].setEndTime(endTime);
                                tables[tableNo].setGuid(guid);
                            }
                        }
                    }
                } catch (org.json.JSONException e) {
                    continue;
                }

            }

        }
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        if (ctasTablesPresent==false)
            return;
        if (!isCTAS) {
            File f = new File((PropertiesFileReader.getOutputDir() + "/tags-tables.txt"));
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
            BufferedReader reader = new BufferedReader(new FileReader(PropertiesFileReader.getOutputDir() + "/tags-tables-temp.txt"));
            String tagInfo = "";
            int ntables=PropertiesFileUtils.getNumTablesToTag();
            for (int i = 1; (tagInfo = reader.readLine()) != null; i++) {
                if (i>ntables) {
                    // since there is no way to add multiple tags to a table by 1 POST request as of now
                    i=1;
                }
                if (tables[i] != null)
                    bufferedWriter.write(tables[i].getTableno() + "," + PropertiesFileReader.getDatabase()+"."+tables[i].getTableName() +"@"+PropertiesFileReader.getCluster()+ "," + tables[i].getGuid() + "," + tagInfo + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            getTimeTakenToProcess(tables);
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        Integer lastSTable;
        Integer lastMTable;
        Integer lastLTable;
        Integer numTestPlanTables;
        if (!isCTAS) {
            lastSTable = PropertiesFileUtils.getSmallTables();
            lastMTable = PropertiesFileUtils.getMediumTables() + lastSTable;
            lastLTable = PropertiesFileUtils.getNumTables();
        } else {
            lastSTable = PropertiesFileUtils.getSmallCtasTables();
            lastMTable = PropertiesFileUtils.getMediumCtasTables() + lastSTable;
            lastLTable = PropertiesFileUtils.getNumCtasTables();
            Collections.sort(tableArrayList, new Comparator<Table>(){
                public int compare(Table s1, Table s2) {
                    return s1.getTableno()-s2.getTableno();
                }
            });
        }
        numTestPlanTables = PropertiesFileReader.getNumTestPlanTables();
        int llim = 0, ulim = 0;
        for (int i = 0; i < numTestPlanTables; i++) {
            if (i % 3 == 0) {
                llim = 0;
                ulim = lastSTable;
            } else if (i % 3 == 1) {
                llim = lastSTable;
                ulim = lastMTable;
            } else {
                llim = lastMTable;
                ulim = lastLTable;
            }
            int randomNum = new Random().nextInt((ulim - llim)) + llim;
            bufferedWriter.write(tableArrayList.get(randomNum).getTableno() + "," + PropertiesFileReader.getDatabase()+"."+tableArrayList.get(randomNum).getTableName()+"@" +PropertiesFileReader.getCluster()+ "," + tableArrayList.get(randomNum).getGuid() + "," + tableArrayList.get(randomNum).getStartTime() + "," + tableArrayList.get(randomNum).getEndTime() + "\n");

        }

        String stime=tables[firstTable].getStartTime();
        String etime=tables[lastTable].getEndTime();

        calculateDifference(stime,etime);
        bufferedWriter.flush();
        bufferedWriter.close();
    }


    public static void getTimeTakenToProcess(Table[] tables) throws ParseException, IOException {
        System.out.println("length"+tables.length);
        Integer[] splitList = PropertiesFileReader.getNumSplits();
        Integer n=0;
        Integer numTables=tables.length;
        for(int i=0;i<splitList.length;i++) {
            n=splitList[i];
            n=(n*numTables)/100;
           resultWriter.write((n-1)+"th table : "+tables[n-1].getEndTime()+"\n");

            calculateDifference(tables[1].getStartTime(),tables[n-1].getEndTime());

        }
        resultWriter.flush();
    }



    public static void mixtables() throws IOException, ConfigurationException {

        BufferedReader regFileReader = new BufferedReader(new FileReader(regularTableFile));
        BufferedReader ctasFileReader = new BufferedReader(new FileReader(ctasTableFile));
        BufferedWriter testPlanWriter = new BufferedWriter(new FileWriter(testPlanTablesFile));
        Integer numTestPlanTables = PropertiesFileReader.getNumTestPlanTables();
        String tableInfo = "";
        for (int i = 0; i < numTestPlanTables; i++) {
            System.out.println("i ="+i);
            if (i % 2 == 0) {
                tableInfo = regFileReader.readLine();
                ctasFileReader.readLine();
            }
            else {
                if((tableInfo = ctasFileReader.readLine()) == null)
                    tableInfo = regFileReader.readLine();
                else
                    regFileReader.readLine();
            }

            testPlanWriter.write(tableInfo + "\n");
        }

        testPlanWriter.flush();
        testPlanWriter.close();
        regFileReader.close();
        ctasFileReader.close();



    }

    public static void getTestPlanTables() throws IOException, ConfigurationException, ParseException {
        /* Create tag file (Guid of table to be tagged,tag name,tag attribute) ready for jmeter input*/
        TagCreator.createTags();
        String database = PropertiesFileReader.getDatabase();
        String cluster = PropertiesFileReader.getCluster();
        regularTableFile = new File(PropertiesFileReader.getOutputDir() + "/mixedtables.txt");
        ctasTableFile = new File(PropertiesFileReader.getOutputDir() + "/mixedctastables.txt");
        testPlanTablesFile = new File(PropertiesFileReader.getOutputDir() + "/testplantables.txt");
        /* Results file contains duration and other important information regarding writes*/
        resultsFile=new File(PropertiesFileReader.getOutputDir() + "/results.txt");
        resultWriter=new BufferedWriter(new FileWriter(resultsFile));
        /*evaluate regular tables*/
        calculateTime(regularTableFile, String.format("table_([0-9]*)", database, cluster), false);
        /* evaluate CTAS tablea*/
        calculateTime(ctasTableFile, String.format("table_([0-9]*)_ctas", database, cluster), true);
        /* mix Regular and CTAS tables snd store it in a file ready for jmeter input*/
        mixtables();

        resultWriter.flush();

    }


   public  static void calculateDifference(String startTime,String endTime) throws ParseException, IOException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

        Date d1 = null;
        Date d2 = null;

            d1 = format.parse(startTime);
            d2 = format.parse(endTime);

        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
       resultWriter.write(diffHours+" hours "+diffMinutes+" mins "+diffSeconds+" secs");

    }

}
