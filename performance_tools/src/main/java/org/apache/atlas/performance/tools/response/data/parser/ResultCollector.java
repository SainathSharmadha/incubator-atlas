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
    static int prevTable=0;
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Long> small = new ArrayList<Long>();
    ArrayList<Long> medium = new ArrayList<Long>();
    ArrayList<Long> large = new ArrayList<Long>();
    ArrayList<ArrayList<Query>> lists=new ArrayList<ArrayList<Query>>();
    Integer numQueriesPerSet = QuerySet.numQueriesPerSet;
    int small_e ;
    int med_e ;
    int large_e ;
    String outputDir= PropertiesFileReader.getOutputDir();

    ResultCollector(int usersCnt,int loopCount,int small_e,int med_e,int large_e){
        this.usersCnt=usersCnt;
        this.loopCount=loopCount;
        this.small_e=small_e;
        this.med_e=med_e;
        this.large_e=large_e;
    }

    void getResults() throws IOException, SAXException, ParserConfigurationException, ParseException {
         readFiles();
         analyzeQuerySetsBySize();
         analyzeQueriesBySize();
         findTotalTime();
         findCPUConsumption();
    }

    private void findTotalTime() throws IOException, SAXException, ParserConfigurationException, ParseException {
parseEndSamplerFile(new File(outputDir+"/EndSamplers.xml"));
    }


    private void readFiles() throws ParserConfigurationException, IOException, SAXException {
        File file;
        BufferedReader buff;
        User user;
        for (int i = 1; i <= usersCnt; i++) {

            user = new User("Atlas user 1-" + i);

            file = new File(outputDir+"/Users/Atlas users 1-" + i + ".xml");
            parseUserFile(file,user);
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
        Long connectTime = Long.parseLong(element.getAttribute("ct"));
        Long loadTime = Long.parseLong(element.getAttribute("t"));
        String response = element.getAttribute("rc");

        Long timeTaken = connectTime + loadTime;


        String responseData = element.getElementsByTagName("java.net.URL").item(0).getTextContent();
        String table = "";
        String cluster = "erie-perf-test-cluster";
        String pattern = "(.*)default.(table_([0-9]*)(|_ctas))@" + cluster + "(.*)";
        String pattern2="(.*)(table_([0-9]*))(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(responseData);
        if (m.find()) {
            table = m.group(3);
        }
        else
            table=Integer.toString(prevTable);
        prevTable=Integer.parseInt(table);
        Query query = new Query(queryName, timestamp, latency, connectTime, loadTime, table, timeTaken);

    return query;
    }


private void parseUserFile(File file,User user) throws ParserConfigurationException, IOException, SAXException {
        QuerySet querySet;

        Query query;

        Document doc=buildDocument(file);


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
                    query=constructQueryFromElement(element);
                    querySet.addToQuerySet(query);
                }
            }
            users.add(user);
        }

private void parseEndSamplerFile(File file) throws ParserConfigurationException, IOException, SAXException, ParseException {
    Document doc=buildDocument(file);
    NodeList nList = doc.getElementsByTagName("httpSample");
    Element element;
    Node firstNode,lastNode;
    Query firstQuery,lastQuery;
    firstNode=nList.item(0);
    element=(Element)firstNode;
    firstQuery=constructQueryFromElement(element);
    lastNode=nList.item(1);
    element=(Element)lastNode;
    lastQuery=constructQueryFromElement(element);
    Long startTimeStamp=firstQuery.timeStamp;
    Long lastTimeStamp=lastQuery.timeStamp;
    Long duration=lastQuery.timeTaken;
    Long endTime=lastTimeStamp+duration;
    Timestamp t2=new Timestamp(endTime);
    Timestamp t1=new Timestamp(startTimeStamp);
    System.out.println("\n\n Total time taken for test : \t"+TimeUtils.getFormattedTime(t2.getTime()-t1.getTime()));
}

    private void analyzeQuerySetsBySize() throws IOException {



        ArrayList<Long> timeQuerySet = new ArrayList<Long>();
        int i = 0;
        int jk = 0;

        lists=new ArrayList<ArrayList<Query>>();
        for(int y=0;y<numQueriesPerSet;y++) {
            ArrayList<Query> list=new ArrayList<Query>();
            lists.add(list);
        }
        User userr;
        for (i = 0; i < users.size(); i++) {
             userr = users.get(i);
            Query query=null;
            for (int j = 0; j < userr.querySets.length; j++) {
                jk=0;
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

                tableno=Integer.parseInt(query.table);
                switch(getTableSize(tableno)){
                    case 0: {
                        small.add(tPerQuerySet);
                        break;
                    }

                    case 1:{
                        medium.add(tPerQuerySet);
                        break;
                    }

                    case 2:{
                        large.add(tPerQuerySet);
                        break;
                    }

                }

            }

        }

        printResultsOfQuerySets();


    }


private Integer getTableSize(int tableno){
int size=0;
    if ((tableno >= 0) && (tableno <= small_e))
        size=0;
    else if ((tableno > small_e) && (tableno <= med_e))
        size=1;
    else if ((tableno > med_e) && (tableno <= large_e))
        size=2;

return size;
}

    private void printResultsOfQuerySets(){
        System.out.println("Number of Small Tables : " + small.size()+"\n");
        Long st = Long.valueOf(0);
        Long mt = Long.valueOf(0);
        Long lt = Long.valueOf(0);
        ArrayList<Long> stimes = new ArrayList<Long>();
        ArrayList<Long> mtimes = new ArrayList<Long>();
        ArrayList<Long> ltimes = new ArrayList<Long>();
        int i=0;
        for (i = 0; i < small.size(); i++) {
            st = st + small.get(i);
            stimes.add(small.get(i));
        }
        System.out.println("Number of Medium tables :" + medium.size());
        for (i = 0; i < medium.size(); i++) {
            mt = mt + medium.get(i);
            mtimes.add(medium.get(i));
        }
        System.out.println("Number of Large tables :" + large.size()+"\n");
        for (i = 0; i < large.size(); i++) {
            lt = lt + large.get(i);
            ltimes.add(large.get(i));
        }
        System.out.println("Small tables : \n");

        StatisticsEvaluator.findPercentile(stimes,1);
        System.out.println("Medium tables : \n");
        if(mtimes.size()==0){
            System.out.println("No medium sized tables");
        }
        else {

            StatisticsEvaluator.findPercentile(mtimes, 1);
        }
        System.out.println("Large tables : \n ");
        if(ltimes.size()==0){
            System.out.println("No large size tables");
        }
        else {


            StatisticsEvaluator.findPercentile(ltimes, 1);
        }
    }


/* Prints statistics for each query */

    public void analyzeQueriesBySize(){
        ArrayList<Long> qsmall;
        ArrayList<Long> qmedium;
        ArrayList<Long> qlarge;
        for(int y=0;y<QuerySet.numQueriesPerSet;y++){
            System.out.println("Query "+(y+1)+"\n");
            qsmall=new ArrayList<Long>();
            qmedium=new ArrayList<Long>();
            qlarge=new ArrayList<Long>();
            Query query;
            Query prevquery=null;
            String qname=null;
            int tableno=0;
            for(int k=0;k<lists.get(y).size();k++){

                query=lists.get(y).get(k);
                qname=query.name;
                if((y==1)||(qname.equals("Get details of a table"))) {
                    prevquery = lists.get(y - 1).get(k);
                }
                if(y!=1)
                    tableno=Integer.parseInt(query.table);
                else
                    tableno=Integer.parseInt(prevquery.table);
                if ((tableno >= 0) && (tableno <= small_e)) {
                    qsmall.add(query.timeTaken);
                }
                else if ((tableno > small_e) && (tableno <= med_e))
                    qmedium.add(query.timeTaken);
                else if ((tableno > med_e) && (tableno <= large_e))
                    qlarge.add(query.timeTaken);

            }
            System.out.println(qname +"\n small");
            ArrayList<String> smalls=StatisticsEvaluator.findPercentile(qsmall,1);
            ArrayList<String> mids=new ArrayList<String>();
            ArrayList<String> larges=new ArrayList<String>();
            if(qmedium.size()==0){
                System.out.println("No medium sized tables");
            }
            else {
                System.out.println("medium");
                mids = StatisticsEvaluator.findPercentile(qmedium, 1);
            }
            if(qlarge.size()==0){
                System.out.println("No large sized tables");
            }
            else {
                System.out.println("large");
                larges = StatisticsEvaluator.findPercentile(qlarge, 1);
            }
            ArrayList<String> list=new ArrayList<String>();
            list.add("Min");
            list.add("Max");
            list.add("Avg");
            list.add("10th");
            list.add("25th");
            list.add("50th");
            list.add("75th");
            list.add("90th");
            list.add("95th");
            System.out.println("\t\tSmall\t\t\t Medium\t\t\t Large");
            for(int i=0;i<smalls.size();i++){
                System.out.println(list.get(i)+"\t\t"+smalls.get(i)+"\t"+mids.get(i)+"\t"+larges.get(i)+"\n");
            }

        }
    }


    static  void findCPUConsumption() throws IOException {

        File cpuDataFile=new File(PropertiesFileReader.getCpuFile());
        BufferedReader cpubuff=new BufferedReader(new FileReader(cpuDataFile));
        String line="";
        String splits[];
        ArrayList<Long> cpuList=new ArrayList<Long>();
        Pattern p=Pattern.compile("(.*)root(.*)S\\|(.*)java(.*)");
        Matcher m;
        while((line=cpubuff.readLine())!=null){
            line=line.trim();
            line = line.replaceAll(" +", "|");
            m=p.matcher(line);

            if(m.find()) {
                splits = m.group(3).split("\\|");
                Float cpuVal = Float.parseFloat(splits[0]);
                if (cpuVal >= 100) {
                    cpuList.add(cpuVal.longValue());
                }
            }

        }
        Long sum=Long.valueOf(0);
        for(int i=0;i<cpuList.size();i++){
            sum=sum+cpuList.get(i);
        }

        System.out.println("CPU consumption : \nAverage       :    " + sum/cpuList.size()+"\n");
        Collections.sort(cpuList);
        StatisticsEvaluator.findPercentile(cpuList,0);



    }

}



