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
import org.apache.commons.configuration.ConfigurationException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;

public class JMeterResponseCollector {

    public static void main(String args[]) throws IOException, TransformerException, SAXException, ParserConfigurationException, ParseException, ConfigurationException {
        System.setProperty("atlas.perf.dir",args[0]);
        PropertiesFileReader.readPropertiesFile();
        FileBuilder.createFiles(PropertiesFileReader.getNumUsers());
        User.loopCount=PropertiesFileReader.getNumLoops();
        QuerySet.setNumQueriesPerSet(PropertiesFileReader.getNumQueriesPerSet());
        ResultCollector rs=new ResultCollector(PropertiesFileReader.getNumUsers(),
                                                PropertiesFileReader.getNumLoops(),
                                                PropertiesFileReader.getSmallTablesLast(),
                                                PropertiesFileReader.getMediumTablesLast(),
                                                PropertiesFileReader.getLargeTablesLast());
        rs.getResults();













    }
}
