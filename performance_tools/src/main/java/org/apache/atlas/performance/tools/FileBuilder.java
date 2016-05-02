package org.apache.atlas.performance.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created by temp on 4/5/16.
 */
public class FileBuilder {

    public static void createFiles(Integer usersCnt) throws IOException, TransformerException, SAXException, ParserConfigurationException {
        for (int i = 1; i <= usersCnt; i++) {
            File f;
            Writer writer;
            f = new File("performance_tools/src/main/java/org.apache.atlas.performance.tools/Users/Atlas users 1-" + i + ".xml");
            f.createNewFile();
            writer = new FileWriter(f);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> \n <test/>");
            writer.flush();
        }

        populateFiles();

    }


    public static void populateFiles() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        File source = new File("/Users/temp/29-4/dataset50k/ResponseDataSet30_80_ctas.xml");
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document sourceDom = builder.parse(source);

        File target;
        Document targetDom;

        NodeList httpSample = sourceDom.getElementsByTagName("httpSample");
        for (int temp = 0; temp < httpSample.getLength(); temp++) {
            System.out.println("temp" + temp);
            Node sourceSection = builder.parse(new InputSource(new FileReader(source))).getElementsByTagName("httpSample").item(temp);
            Element element = (Element) sourceSection;
            String userName = element.getAttribute("tn");

            target = new File("performance_tools/src/main/java/org.apache.atlas.performance.tools/Users/" + userName + ".xml");
            targetDom = builder.parse(new InputSource(new FileReader(target)));
            Node targetSection = targetDom.getElementsByTagName("test").item(0);

            targetSection.appendChild(targetDom.adoptNode(sourceSection.cloneNode(true)));
            TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(targetDom),
                    new StreamResult(new FileWriter(target))
            );
        }

    }




}
