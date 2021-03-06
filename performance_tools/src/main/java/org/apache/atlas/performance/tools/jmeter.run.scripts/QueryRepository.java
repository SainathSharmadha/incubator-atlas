/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.atlas.performance.tools.jmeter.run.scripts;

import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jorphan.collections.ListedHashTree;

import java.io.IOException;


class HTTPRequest {
    String name;
    String query;

    HTTPRequest(String name, String query) {
        this.name = name;
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public HTTPSampler getHTTPSampler() {
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setDomain(PropertiesFileReader.getDomain());
        httpSampler.setPort(21000);
        httpSampler.setPath(getQuery());
        httpSampler.setName(getName());
        httpSampler.setMethod("GET");
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSampler.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        return httpSampler;


    }
}

class POSTRequest extends HTTPRequest {
    String body;

    public POSTRequest(String name, String query, String body) {
        super(name, query);
        this.body = body;
        this.name = name;
        this.query = query;

    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HTTPSampler getHTTPSampler() {
        HTTPSampler httpSampler = super.getHTTPSampler();
        httpSampler.setMethod("POST");
        httpSampler.setPostBodyRaw(true);
        httpSampler.addNonEncodedArgument("args", body, "");
        return httpSampler;
    }
}

public class QueryRepository {


    ListedHashTree testPlanTree;

    QueryRepository(ListedHashTree testPlanTree) {
        this.testPlanTree = testPlanTree;
    }

    final String PORT = "21000";
    final String getTableGivenName = "/api/atlas/discovery/search/dsl/?query=hive_table+where+qualifiedName%3D%27${fqn}%27";
    final String getDetailsOfTable = "/api/atlas/entities/${guid}";
    final String getSchemaOfTable = "/api/atlas/lineage/${guid}/schema";
    //  final String iLineage = "/api/atlas/lineage/hive/table/${fqn}/inputs/graph";
    // final String oLineage = "/api/atlas/lineage/hive/table/${fqn}/outputs/graph";
    final String iLineage = "/api/atlas/lineage/${guid}/inputs/graph";
    final String oLineage = "/api/atlas/lineage/${guid}/outputs/graph";
    final String createTags = "/api/atlas/types";
    final String associateTags = "/api/atlas/entities/${guid}/traits";
    final String getAssociatedEntity = "/api/atlas/discovery/search/dsl?query=hive_table+where+hive_table+isa+${tag}";

    private CSVDataSet getCSVDataSetConfig(String filename, String variables) {
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());
        csvDataSet.setName("CSV dataset config");
        csvDataSet.setProperty(new StringProperty("filename", filename));
        csvDataSet.setProperty(new StringProperty("variableNames", variables));
        csvDataSet.setProperty(new StringProperty("delimiter", ","));
        csvDataSet.setProperty(new StringProperty("shareMode", "shareMode.all"));
        csvDataSet.setProperty("quoted", false);
        csvDataSet.setProperty("recycle", true);
        csvDataSet.setProperty("stopThread", false);
        return csvDataSet;
    }

    private HeaderManager getHeaderManager() {
        HeaderManager headerManager = new HeaderManager();
        headerManager.setName("Header Manager");
        Header header = new Header();
        header.setName("Content-Type");
        header.setValue("application/json");
        headerManager.add(header);
        headerManager.setProperty(HeaderManager.GUI_CLASS, HeaderPanel.class.getName());
        headerManager.setProperty(HeaderManager.TEST_CLASS, HeaderManager.class.getName());
        return headerManager;
    }

    ListedHashTree getPostTags() throws IOException {

        HTTPSampler[] queries = new HTTPSampler[1];
        String body = "{\"enumTypes\":[],\"traitTypes\":[{\"attributeDefinitions\":[{\"dataTypeName\":\"string\",\"multiplicity\":\"optional\",\"isComposite\":false,\"isUnique\":false,\"isIndexable\":true,\"reverseAttributeName\":null,\"name\":\"${attribute}\"}],\"typeName\":\"${tag}\",\"typeDescription\":null,\"superTypes\":null,\"hierarchicalMetaTypeName\":\"org.apache.atlas.typesystem.types.TraitType\"}],\"structTypes\":[],\"classTypes\":[]}";
        POSTRequest postTags = new POSTRequest("Create tags", createTags, body);
        queries[0] = postTags.getHTTPSampler();
        testPlanTree.add(queries);
        CSVDataSet csvDataSet = getCSVDataSetConfig(PropertiesFileReader.getOutputDir() + "/tags-attributes.txt", "tag,attribute");
        HeaderManager headerManager = getHeaderManager();
        testPlanTree.add(headerManager);
        testPlanTree.add(csvDataSet);
        return testPlanTree;

    }

    ListedHashTree getAssociateTagsQueries() {
        String body = "{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"${tag}\",\"values\":{\"${attribute}\":\"${value}\"}}\n";
        POSTRequest postTags = new POSTRequest("Associate Tags", associateTags, body);
        CSVDataSet csvDataSet = getCSVDataSetConfig(PropertiesFileReader.getOutputDir() + "/tags-tables.txt", "tableno,fqn,guid,tag,attribute,val");
        HeaderManager headerManager = getHeaderManager();
        testPlanTree.add(postTags.getHTTPSampler());
        testPlanTree.add(headerManager);
        testPlanTree.add(csvDataSet);
        return testPlanTree;
    }


    ListedHashTree testUserQueries() {
        HTTPRequest getTable = new HTTPRequest("Get table given name", getTableGivenName);
        HTTPRequest getDetailsofTable = new HTTPRequest("Get details of table", getDetailsOfTable);
        HTTPRequest getSchema = new HTTPRequest("Get schema of a table", getSchemaOfTable);
        HTTPRequest getInputLineage = new HTTPRequest("Get Input Lineage graph", iLineage);
        HTTPRequest getOutputLineage = new HTTPRequest("Get Output Lineage graph", oLineage);
        HTTPSampler[] queries = {getTable.getHTTPSampler(),
                getDetailsofTable.getHTTPSampler(),
                getSchema.getHTTPSampler(),
                getInputLineage.getHTTPSampler(),
                getOutputLineage.getHTTPSampler()
        };
        testPlanTree.add(queries);
        CSVDataSet csvDataSet = getCSVDataSetConfig(PropertiesFileReader.getOutputDir() + "/testplantables.txt", "tableno,fqn,guid,st,et");
        testPlanTree.add(csvDataSet);
        return testPlanTree;
    }

    ListedHashTree getEntityWithTag() {
        HTTPRequest getEntityAssociatedToTag = new HTTPRequest("Get Entity Associated to a Tag", getAssociatedEntity);
        testPlanTree.add(getEntityAssociatedToTag.getHTTPSampler());
        CSVDataSet csvDataSet = getCSVDataSetConfig(PropertiesFileReader.getOutputDir() + "/tags-attributes.txt", "tag,attribute");
        testPlanTree.add(csvDataSet);
        return testPlanTree;
    }


}
