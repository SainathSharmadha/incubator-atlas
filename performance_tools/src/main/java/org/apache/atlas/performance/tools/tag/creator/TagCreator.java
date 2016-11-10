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
package org.apache.atlas.performance.tools.tag.creator;

import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.atlas.performance.tools.PropertiesFileUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TagCreator {

    public static void createTags() throws IOException, ConfigurationException {
        Integer numTablesToTag = PropertiesFileUtils.getNumTablesToTag();
        Integer numTablePerTag = numTablesToTag / PropertiesFileReader.getNumTags();
        Integer numTags = PropertiesFileReader.getNumTags();
        File tagsAttributesFile = new File(String.format("%s/tags-attributes.txt", PropertiesFileReader.getOutputDir()));
        tagsAttributesFile.createNewFile();
        FileWriter tagAttributesFileWriter = new FileWriter(tagsAttributesFile);
        File tagsTablesFile = new File(String.format("%s/tags-tables-temp.txt", PropertiesFileReader.getOutputDir()));
        FileWriter tagTablesFileWriter = new FileWriter(tagsTablesFile);
        if (numTablePerTag > 0)
            tagMultipleTablesToATag(numTags, numTablePerTag, tagAttributesFileWriter, tagTablesFileWriter);
        else
            tagMultipleTagsToATable(numTags, numTablePerTag, tagAttributesFileWriter, tagTablesFileWriter);
        tagAttributesFileWriter.flush();
        tagTablesFileWriter.flush();
    }


    public static void tagMultipleTablesToATag(Integer numTags, Integer numTablePerTag, FileWriter tagAttributesFileWriter, FileWriter tagTablesFileWriter) throws IOException, ConfigurationException {
        Integer tagStart = 1, tagEnd = numTags, tableStart = 0, tableEnd = 0, val = 0;
        String towrite;
        System.out.println("tagMultipleTablesToATag ...");
        for (int i = tagStart; i <= tagEnd; i++) {
            tableStart = tableEnd + 1;
            tableEnd = tableEnd + numTablePerTag;
            towrite = String.format("tagg_%d,tagg_%d_attribute\n", i, i);
            tagAttributesFileWriter.write(towrite);
            for (int j = tableStart; j <= tableEnd; j++) {
                val++;
                towrite = String.format("tagg_%d,tagg_%d_attribute,val_%d", i, i, val);
                tagTablesFileWriter.write(towrite + "\n");
            }
        }
        PropertiesFileUtils.changeNumTaggedTables(tableEnd);
    }

    public static void tagMultipleTagsToATable(Integer numTags, Integer numTablePerTag, FileWriter tagAttributesFileWriter, FileWriter tagTablesFileWriter) throws IOException, ConfigurationException {

        Integer tagStart = 1, tagEnd = numTags, val = 0;
        String towrite;
        for (int i = tagStart; i <= tagEnd; i++) {
            val++;
            towrite = String.format("tagg_%d,tagg_%d_attribute,val_%d", i, i, val);
            tagAttributesFileWriter.write(String.format("tagg_%d,tagg_%d_attribute \n", i, i));
            tagTablesFileWriter.write(towrite + "\n");
        }
        PropertiesFileUtils.changeNumTaggedTables(tagEnd);
    }

}
