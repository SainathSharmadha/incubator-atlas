package hwx.guidgen.com;

/**
 * Created by temp on 3/16/16.
 */
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GuidParser {
    File guidfile;
    FileWriter writer;
    final String  RESULT_XML_FILE;
    File xmlFile ;
    DocumentBuilderFactory dbFactory ;
    DocumentBuilder dBuilder ;
    Document doc;
    public GuidParser() throws IOException {
        guidfile = new File("Guids.txt");
        guidfile.createNewFile();
        writer = new FileWriter(guidfile);
        RESULT_XML_FILE="/Users/temp/SoftwareHWX/jmeter/apache-jmeter-2.13/report.xml";
    }

void initialize() throws ParserConfigurationException, IOException, SAXException {
    xmlFile = new File(RESULT_XML_FILE);
    dbFactory = DocumentBuilderFactory.newInstance();
    dBuilder = dbFactory.newDocumentBuilder();
    doc = dBuilder.parse(xmlFile);
}
    public void parse(){
        try {
            /* report.xml takes time to be created and generated*/
            Thread.sleep(100000);

try {
    /* If report.xml is not completely generated*/
    initialize();

}
catch(org.xml.sax.SAXParseException e)
{
    System.out.print(e.getMessage()+" \n Report.xml is not generated completely .Resuming after a minute");

    Thread.sleep(100000);

    initialize();
}

            doc.getDocumentElement().normalize();


            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("httpSample");
            String table="";
            String guid="";


            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


                    String responseData= eElement.getElementsByTagName("responseData").item(0).getTextContent();


                    String pattern="\\{\"requestId\":\"(.*)\",\"query\":\"hive_table where name='(.*)'\",\"queryType\":\"dsl\",\"count\":1,\"results\":\\[\\{\"\\$typeName\\$\":\"hive_table\",\"\\$id\\$\":\\{\"id\":\"(.*)\",\"\\$typeName\\$\":\"hive_table\",\"version\":0\\}(.*)";
                    Pattern r = Pattern.compile(pattern);


                    Matcher m = r.matcher(responseData);
                    if (m.find( )) {
                        table=m.group(2);
                        guid= m.group(3);
                        writer.write(table+"  "+guid+"\n");
                        writer.flush();
                    } else {
                        System.out.println("NO MATCH");
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
