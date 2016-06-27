package org.apache.atlas.performance.tools.tables.time.calculator;


import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.atlas.performance.tools.PropertiesFileUtils;
import org.apache.atlas.performance.tools.tag.creator.TagCreator;
import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CalculateTime {

    static File regularTableFile, ctasTableFile, testPlanTablesFile;

    public static void calculateTime(File file, String tableFormat, boolean isCTAS) throws ConfigurationException, IOException {

        Table[] tables = new Table[PropertiesFileReader.getNumTables() + 1];
        BufferedReader buff = new BufferedReader(new FileReader(new File("/Users/temp/SoftwareHWX/atlass/apache-atlas-0.7-incubating-SNAPSHOT/logs/application.log")));
        String responseData;
        int count = 0;
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
                    tableNo = Integer.parseInt(tableNameMatcher.group(1));
                    if (tables[tableNo] == null) {
                        tables[tableNo] = new Table();
                        tables[tableNo].setTableno(tableNo);
                        tables[tableNo].setTableName(tableName);
                        tables[tableNo].setStartTime(startTime);
                        count++;
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
                            tables[tableNo].setEndTime(endTime);
                            tables[tableNo].setGuid(guid);
                        }
                    }
                } catch (org.json.JSONException e) {
                    continue;
                }

            }

        }

        if (!isCTAS) {
            File f = new File((PropertiesFileReader.getOutputDir() + "/tags-tables.txt"));
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));

            BufferedReader reader = new BufferedReader(new FileReader(PropertiesFileReader.getOutputDir() + "/tags-tables-temp.txt"));
            String tagInfo = "";

            for (int i = 0; (tagInfo = reader.readLine()) != null; i++) {
                if (tables[i] == null) {
                    System.out.println("null");
                }
                if (tables[i] != null)
                    bufferedWriter.write(tables[i].getTableno() + "," + tables[i].getTableName() + "," + tables[i].getGuid() + "," + tagInfo + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
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
            bufferedWriter.write(tableArrayList.get(randomNum).getTableno() + "," + tableArrayList.get(randomNum).getTableName() + "," + tableArrayList.get(randomNum).getGuid() + "," + tableArrayList.get(randomNum).getStartTime() + "," + tableArrayList.get(randomNum).getEndTime() + "\n");


        }
        bufferedWriter.flush();
        bufferedWriter.close();

    }


    public static void mixtables() throws IOException, ConfigurationException {

        BufferedReader regFileReader = new BufferedReader(new FileReader(regularTableFile));
        BufferedReader ctasFileReader = new BufferedReader(new FileReader(ctasTableFile));
        BufferedWriter testPlanWriter = new BufferedWriter(new FileWriter(testPlanTablesFile));
        Integer numTestPlanTables = PropertiesFileReader.getNumTestPlanTables();
        String tableInfo = "";
        for (int i = 0; i < numTestPlanTables; i++) {
            if (i % 2 == 0)
                tableInfo = regFileReader.readLine();
            else if ((tableInfo = ctasFileReader.readLine()) == null) {
                tableInfo = regFileReader.readLine();
            }

            testPlanWriter.write(tableInfo + "\n");
        }

        testPlanWriter.flush();
        testPlanWriter.close();
        regFileReader.close();
        ctasFileReader.close();


    }

    public static void getTestPlanTables() throws IOException, ConfigurationException {
        TagCreator.createTags();
        String database = PropertiesFileReader.getDatabase();
        String cluster = PropertiesFileReader.getCluster();
        regularTableFile = new File(PropertiesFileReader.getOutputDir() + "/mixedtables.txt");
        ctasTableFile = new File(PropertiesFileReader.getOutputDir() + "/mixedctastables.txt");
        testPlanTablesFile = new File(PropertiesFileReader.getOutputDir() + "/testplantables.txt");
        calculateTime(regularTableFile, String.format("%s.table_([0-9]*)@%s", database, cluster), false);
        calculateTime(ctasTableFile, String.format("%s.table_([0-9]*)_ctas@%s", database, cluster), true);
        mixtables();

    }


}
