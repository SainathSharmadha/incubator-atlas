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

package org.apache.atlas.performance.tools.response.data.parser;

import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultCollector {
    int usersCnt;
    int loopCount;
    static int prevTable = 0;
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Long> small = new ArrayList<Long>();
    ArrayList<Long> medium = new ArrayList<Long>();
    ArrayList<Long> large = new ArrayList<Long>();
    ArrayList<ArrayList<Query>> lists = new ArrayList<ArrayList<Query>>();
    Integer numQueriesPerSet = QuerySet.numQueriesPerSet;
    int small_e;
    int med_e;
    int large_e;
    String outputDir = PropertiesFileReader.getOutputDir();
    FileWriter writer;
    ResultWriter resultWriter;

    ResultCollector(int usersCnt, int loopCount, int small_e, int med_e, int large_e, ResultWriter resultWriter) {
        this.usersCnt = usersCnt;
        this.loopCount = loopCount;
        this.small_e = small_e;
        this.med_e = med_e;
        this.large_e = large_e;
        this.resultWriter = resultWriter;
    }

    void getResults() throws IOException, SAXException, ParserConfigurationException, ParseException {
        readFiles();
        analyzeQuerySetsBySize();
        analyzeQueriesBySize();
        findTotalTime();
        findCPUConsumption();
    }

    private void findTotalTime() throws IOException, SAXException, ParserConfigurationException, ParseException {
        parseEndSamplerFile(new File(outputDir + "/EndSamplers.xml"));
    }


    private void readFiles() throws ParserConfigurationException, IOException, SAXException {
        File file;
        User user;
        String uname, filename;
        for (int i = 1; i <= usersCnt; i++) {
            uname = String.format("Atlas user 1-%d", i);
            user = new User(uname);
            filename = String.format("%s/Users/Atlas users 1-%d.xml", outputDir, i);
            file = new File(filename);
            parseUserFile(file, user);
        }

    }

    private Document buildDocument(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private Query constructQueryFromElement(Element element) {
        Long latency = Long.parseLong(element.getAttribute("lt"));
        Long timestamp = Long.parseLong(element.getAttribute("ts"));
        String queryName = element.getAttribute("lb");
        // Long connectTime = Long.parseLong(element.getAttribute("ct"));
        Long connectTime = 0L;
        Long loadTime = Long.parseLong(element.getAttribute("t"));
        String response = element.getAttribute("rc");

        Long timeTaken = connectTime + loadTime;


        String responseData = element.getElementsByTagName("java.net.URL").item(0).getTextContent();
        String table = "";
        String cluster = "cluster1";
        String strPattern = "(.*)default.(table_([0-9]*)(|_ctas))@" + cluster + "(.*)";
        Pattern pattern = Pattern.compile(strPattern);
        Matcher m = pattern.matcher(responseData);
        if (m.find()) {
            table = m.group(3);
        } else
            table = Integer.toString(prevTable);
        prevTable = Integer.parseInt(table);
        Query query = new Query(queryName, timestamp, latency, connectTime, loadTime, table, timeTaken);

        return query;
    }


    private void parseUserFile(File file, User user) throws ParserConfigurationException, IOException, SAXException {
        QuerySet querySet;

        Query query;

        Document doc = buildDocument(file);


        NodeList nList = doc.getElementsByTagName("httpSample");
        Integer queryCntr = 0;
        Integer querySetCntr = 0;

        for (int temp = 0; temp < nList.getLength(); temp++) {
            queryCntr++;
            if (queryCntr > numQueriesPerSet) {
                queryCntr = 1;
                querySetCntr++;
            }
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                querySet = user.querySets[querySetCntr];
                Element element = (Element) nNode;
                query = constructQueryFromElement(element);
                querySet.addToQuerySet(query);
            }
        }
        users.add(user);
    }

    private void parseEndSamplerFile(File file) throws ParserConfigurationException, IOException, SAXException, ParseException {
        Document doc = buildDocument(file);
        NodeList nList = doc.getElementsByTagName("httpSample");
        Element element;
        Node firstNode, lastNode;
        Query firstQuery, lastQuery;
        firstNode = nList.item(0);
        element = (Element) firstNode;
        firstQuery = constructQueryFromElement(element);
        lastNode = nList.item(1);
        element = (Element) lastNode;
        lastQuery = constructQueryFromElement(element);
        Long startTimeStamp = firstQuery.timeStamp;
        Long lastTimeStamp = lastQuery.timeStamp;
        Long duration = lastQuery.timeTaken;
        Long endTime = lastTimeStamp + duration;
        Timestamp t2 = new Timestamp(endTime);
        Timestamp t1 = new Timestamp(startTimeStamp);
        resultWriter.writeToFile(String.format("\n\n Total time taken for test : \t %s", TimeUtils.getFormattedTime(t2.getTime() - t1.getTime())));
    }

    private void analyzeQuerySetsBySize() throws IOException {

        ArrayList<Long> timeQuerySet = new ArrayList<Long>();
        int i = 0;
        int jk = 0;

        lists = new ArrayList<ArrayList<Query>>();
        for (int y = 0; y < numQueriesPerSet; y++) {
            ArrayList<Query> list = new ArrayList<Query>();
            lists.add(list);
        }
        User userr;
        for (i = 0; i < users.size(); i++) {
            userr = users.get(i);
            Query query = null;
            for (int j = 0; j < userr.querySets.length; j++) {
                jk = 0;
                ArrayList list = userr.querySets[j].querySet;
                long tPerQuerySet = 0;
                Iterator<Query> iter = list.iterator();
                int tableno = 0;

                while (iter.hasNext()) {
                    query = iter.next();
                    lists.get(jk).add(query);
                    jk++;
                    tPerQuerySet += query.timeTaken;
                }

                tableno = Integer.parseInt(query.table);
                switch (getTableSize(tableno)) {
                    case 0: {
                        small.add(tPerQuerySet);
                        break;
                    }

                    case 1: {
                        medium.add(tPerQuerySet);
                        break;
                    }

                    case 2: {
                        large.add(tPerQuerySet);
                        break;
                    }

                }

            }

        }

        printResultsOfQuerySets();


    }


    private Integer getTableSize(int tableno) {
        int size = 0;
        if ((tableno >= 0) && (tableno <= small_e))
            size = 0;
        else if ((tableno > small_e) && (tableno <= med_e))
            size = 1;
        else if ((tableno > med_e) && (tableno <= large_e))
            size = 2;

        return size;
    }

    private void printResultsOfQuerySets() throws IOException {
        resultWriter.writeToFile(String.valueOf(String.format("Number of Small Tables : %d\n", small.size())));
        Long st = Long.valueOf(0);
        Long mt = Long.valueOf(0);
        Long lt = Long.valueOf(0);
        ArrayList<Long> stimes = new ArrayList<Long>();
        ArrayList<Long> mtimes = new ArrayList<Long>();
        ArrayList<Long> ltimes = new ArrayList<Long>();
        int i = 0;
        for (i = 0; i < small.size(); i++) {
            st = st + small.get(i);
            stimes.add(small.get(i));
        }

        resultWriter.writeToFile(String.valueOf(String.format("Number of Medium tables :%d\n", medium.size())));
        for (i = 0; i < medium.size(); i++) {
            mt = mt + medium.get(i);
            mtimes.add(medium.get(i));
        }
        FileWriter fw = resultWriter.writer;
        resultWriter.writeToFile(String.valueOf(String.format("Number of Large tables :%d \n ", large.size())));
        for (i = 0; i < large.size(); i++) {
            lt = lt + large.get(i);
            ltimes.add(large.get(i));
        }
        resultWriter.writeToFile(String.valueOf("Statistics of Small tables : \n"));

        printStatistics(StatisticsEvaluator.findPercentile(stimes, 1));
        resultWriter.writeToFile(String.valueOf("Statistics of Medium tables : \n"));
        if (mtimes.size() == 0) {
            resultWriter.writeToFile("No medium sized tables");
        } else {

            printStatistics(StatisticsEvaluator.findPercentile(mtimes, 1));
        }
        resultWriter.writeToFile("Large tables : \n ");
        if (ltimes.size() == 0) {
            resultWriter.writeToFile("No large size tables");
        } else {
            printStatistics(StatisticsEvaluator.findPercentile(ltimes, 1));
        }
    }

    void printStatistics(ArrayList<String> statistics) throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Min");
        list.add("Max");
        list.add("Avg");
        list.add("10th");
        list.add("25th");
        list.add("50th");
        list.add("75th");
        list.add("90th");
        list.add("95th");

        for (int i = 0; i < statistics.size(); i++) {
            resultWriter.writeToFile(String.valueOf(String.format("%s\t\t%s\n\n", list.get(i), statistics.get(i))));

        }
    }


/* Prints statistics for each query */

    public void analyzeQueriesBySize() throws IOException {
        ArrayList<Long> qsmall;
        ArrayList<Long> qmedium;
        ArrayList<Long> qlarge;
        for (int y = 0; y < QuerySet.numQueriesPerSet; y++) {
            String.format("Query %d \n", (y + 1));
            qsmall = new ArrayList<Long>();
            qmedium = new ArrayList<Long>();
            qlarge = new ArrayList<Long>();
            Query query;
            String qname = null;
            int tableno = 0;
            for (int k = 0; k < lists.get(y).size(); k++) {

                query = lists.get(y).get(k);
                qname = query.name;
                tableno = Integer.parseInt(query.table);

                if ((tableno >= 0) && (tableno <= small_e)) {
                    qsmall.add(query.timeTaken);
                } else if ((tableno > small_e) && (tableno <= med_e))
                    qmedium.add(query.timeTaken);
                else if ((tableno > med_e) && (tableno <= large_e))
                    qlarge.add(query.timeTaken);

            }
            resultWriter.writeToFile(String.format("Query : %s", qname));
            ArrayList<String> smalls = StatisticsEvaluator.findPercentile(qsmall, 1);
            ArrayList<String> mids = new ArrayList<String>();
            ArrayList<String> larges = new ArrayList<String>();
            if (qmedium.size() == 0) {
                resultWriter.writeToFile("No medium sized tables");
            } else {

                mids = StatisticsEvaluator.findPercentile(qmedium, 1);
            }
            if (qlarge.size() == 0) {
                resultWriter.writeToFile("No large sized tables");
            } else {

                larges = StatisticsEvaluator.findPercentile(qlarge, 1);
            }
            ArrayList<String> list = new ArrayList<String>();
            list.add("Min");
            list.add("Max");
            list.add("Avg");
            list.add("10th");
            list.add("25th");
            list.add("50th");
            list.add("75th");
            list.add("90th");
            list.add("95th");
            resultWriter.writeToFile("\t\tSmall\t\t\tMedium\t\t\tLarge");
            for (int i = 0; i < smalls.size(); i++) {
                resultWriter.writeToFile(String.valueOf(String.format("%s\t\t%s\t\t%s\t\t%s\n\n", list.get(i), smalls.get(i), mids.get(i), larges.get(i))));
            }
        }
    }


    static void findCPUConsumption() throws IOException {

        File cpuDataFile = new File(PropertiesFileReader.getCpuFile());
        try {
            BufferedReader cpubuff = new BufferedReader(new FileReader(cpuDataFile));

            String line = "";
            String splits[];
            ArrayList<Long> cpuList = new ArrayList<Long>();
            Pattern p = Pattern.compile("(.*)root(.*)S\\|(.*)java(.*)");
            Matcher m;
            while ((line = cpubuff.readLine()) != null) {
                line = line.trim();
                line = line.replaceAll(" +", "|");
                m = p.matcher(line);

                if (m.find()) {
                    splits = m.group(3).split("\\|");
                    Float cpuVal = Float.parseFloat(splits[0]);
                    if (cpuVal >= 100) {
                        cpuList.add(cpuVal.longValue());
                    }
                }

            }
            Long sum = Long.valueOf(0);
            for (int i = 0; i < cpuList.size(); i++) {
                sum = sum + cpuList.get(i);
            }

            String.format("CPU consumption : \nAverage       :    %d\n", sum / cpuList.size());
            Collections.sort(cpuList);
            StatisticsEvaluator.findPercentile(cpuList, 0);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Please enter a vaild cpu consumption file in atlas-perf-test.properties");
        }


    }

}



