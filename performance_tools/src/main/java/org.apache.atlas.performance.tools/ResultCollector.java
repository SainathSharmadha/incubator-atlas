package org.apache.atlas.performance.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by temp on 4/5/16.
 */
public class ResultCollector {
    int usersCnt;
    int loopCount;
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Long> small = new ArrayList<Long>();
    ArrayList<Long> medium = new ArrayList<Long>();
    ArrayList<Long> large = new ArrayList<Long>();
    ArrayList<ArrayList<Query>> lists=new ArrayList<ArrayList<Query>>();
    Integer numQueriesPerSet = QuerySet.numQueriesPerSet;
    int small_e ;
    int med_e ;
    int large_e ;
    String cpuUsageFile,responseDataFile;

    ResultCollector(int usersCnt,int loopCount,int small_e,int med_e,int large_e,String cpuUsageFile,String responseDataFile){
        this.usersCnt=usersCnt;
        this.loopCount=loopCount;
        this.small_e=small_e;
        this.med_e=med_e;
        this.large_e=large_e;
        this.cpuUsageFile=cpuUsageFile;
        this.responseDataFile=responseDataFile;
    }

    void getResults() throws IOException, SAXException, ParserConfigurationException {
        readFiles();
        analyzeQuerySetsBySize();
        analyzeQueriesBySize();
        findCPUConsumption();
    }

    private void readFiles() throws ParserConfigurationException, IOException, SAXException {
        File file;
        BufferedReader buff;
        User user;
        for (int i = 1; i <= usersCnt; i++) {

            user = new User("Atlas user 1-" + i);

            file = new File("src/main/Users/Atlas users 1-" + i + ".xml");
            parseFile(file,user);
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
        String timestamp = element.getAttribute("ts");
        String queryName = element.getAttribute("lb");
        Long connectTime = Long.parseLong(element.getAttribute("ct"));
        Long loadTime = Long.parseLong(element.getAttribute("t"));
        String response = element.getAttribute("rc");

        Long timeTaken = connectTime + loadTime;
        String responseData = element.getElementsByTagName("java.net.URL").item(0).getTextContent();
        String table = "";
        String cluster = "erie-perf-test-cluster";
        String pattern = "(.*)default.(table_([0-9]*)(|_ctas))@" + cluster + "(.*)";
        Pattern r = Pattern.compile(pattern);


        Matcher m = r.matcher(responseData);
        if (m.find()) {
            table = m.group(3);
        }


        Query query = new Query(queryName, timestamp, latency, connectTime, loadTime, table, timeTaken);

    return query;
    }
private void parseFile(File file,User user) throws ParserConfigurationException, IOException, SAXException {
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


    private void analyzeQuerySetsBySize(){


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
        System.out.println("Small : " + small.size()+"\n");
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
        System.out.println("medium :" + medium.size());
        for (i = 0; i < medium.size(); i++) {
            mt = mt + medium.get(i);
            mtimes.add(medium.get(i));
        }
        System.out.println("large :" + large.size());
        for (i = 0; i < large.size(); i++) {
            lt = lt + large.get(i);
            ltimes.add(large.get(i));
        }

        System.out.println("\n Small tables");
        System.out.println("Small Average Time" + TimeUtils.getFormattedTime(st / Long.valueOf(stimes.size())));
        StatisticsEvaluator.findPercentile(stimes,1);
        System.out.println("Medium");
        System.out.println("Medium Average Time" + TimeUtils.getFormattedTime(mt / Long.valueOf(mtimes.size())));
        StatisticsEvaluator.findPercentile(mtimes,1);
        System.out.println("Large");
        System.out.println("Large Average Time" + TimeUtils.getFormattedTime(lt / Long.valueOf(ltimes.size())));
        StatisticsEvaluator.findPercentile(ltimes,1);

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



        }
    }


    private void findCPUConsumption() throws IOException {
        File cpuDataFile=new File(cpuUsageFile);
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



