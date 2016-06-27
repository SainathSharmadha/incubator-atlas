package org.apache.atlas.performance.tools.tag.creator;

import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.atlas.performance.tools.PropertiesFileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TagCreator {

    public static void createTags() throws IOException {
        Integer numTablesToTag= PropertiesFileUtils.getNumTablesToTag();
        Integer numTablePerTag=numTablesToTag/PropertiesFileReader.getNumTags();
        Integer numTags=PropertiesFileReader.getNumTags();

        System.out.println(numTags+" "+numTablePerTag+"\t"+numTablesToTag);
        File tagsAttributesFile=new File(String.format("%s/tags-attributes.txt",PropertiesFileReader.getOutputDir()));
        tagsAttributesFile.createNewFile();
        FileWriter tagAttributesFileWriter=new FileWriter(tagsAttributesFile);
        File tagsTablesFile=new File(String.format("%s/tags-tables-temp.txt",PropertiesFileReader.getOutputDir()));
        FileWriter tagTablesFileWriter=new FileWriter(tagsTablesFile);

        Integer tagStart=1,tagEnd=numTags,tableStart=0,tableEnd=0,val=0;
        String towrite;
        for(int i=tagStart;i<=tagEnd;i++){
            tableStart=tableEnd+1;
            tableEnd=tableEnd+numTablePerTag;
            towrite=String.format("tagg_%d,tagg_%d_attribute\n",i,i);
            tagAttributesFileWriter.write(towrite);
            for(int j=tableStart;j<=tableEnd;j++){
                    val++;
                    towrite=String.format("tagg_%d,tagg_%d_attribute,val_%d",i,i,val);
                    tagTablesFileWriter.write(towrite+"\n");
            }
        }
        tagAttributesFileWriter.flush();
        tagTablesFileWriter.flush();



    }

}