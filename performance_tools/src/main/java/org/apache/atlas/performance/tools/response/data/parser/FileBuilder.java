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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;


public class FileBuilder {
    static String outputDir=PropertiesFileReader.getOutputDir();

    public static void createFiles(Integer usersCnt) throws IOException, TransformerException, SAXException, ParserConfigurationException {
       File f;
        for (int i = 1; i <= usersCnt; i++) {

            new File(outputDir+"/Users").mkdir();
            f = new File(outputDir+"/Users/Atlas users 1-" + i + ".xml");
            f.createNewFile();
            createEmptyXMLFile(f);

        }
        f=new File(PropertiesFileReader.getOutputDir()+"/EndSamplers.xml");
        f.createNewFile();
        createEmptyXMLFile(f);

        populateFiles();

    }

    static void createEmptyXMLFile(File f) throws IOException {
        Writer writer = new FileWriter(f);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> \n <test/>");
        writer.flush();
        writer.close();

    }


    public static void populateFiles() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        File source = new File(PropertiesFileReader.getJmeterResponseFile());
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document sourceDom = builder.parse(source);

        File target;
        Document targetDom;

        NodeList httpSample = sourceDom.getElementsByTagName("httpSample");
        System.out.println(httpSample.getLength());
        for (int temp = 0; temp < httpSample.getLength(); temp++) {
            System.out.println("temp" + temp);

            Node sourceSection = builder.parse(new InputSource(new FileReader(source))).getElementsByTagName("httpSample").item(temp);
            Element element = (Element) sourceSection;
            String userName = element.getAttribute("tn");

            target = new File(outputDir+"/Users/" + userName + ".xml");

           writeToTargetFile(target,builder,sourceSection);
            if((temp==0)||(temp==httpSample.getLength()-1)){
                target=new File(outputDir+"/EndSamplers.xml");
                writeToTargetFile(target,builder,sourceSection);
            }

        }

    }

    static void writeToTargetFile(File targetFile,DocumentBuilder builder,Node sourceSection) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        Document targetDom = builder.parse(new InputSource(new FileReader(targetFile)));
        Node targetSection = targetDom.getElementsByTagName("test").item(0);

        targetSection.appendChild(targetDom.adoptNode(sourceSection.cloneNode(true)));
        TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(targetDom),
                new StreamResult(new FileWriter(targetFile))
        );
    }




}
