package org.apache.atlas.performance.tools.response.data.parser;

import org.apache.atlas.performance.tools.PropertiesFileReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ResultWriter {
    static File file;
    FileWriter writer;

    ResultWriter(String filename) throws IOException {
        String outputDir = PropertiesFileReader.getOutputDir();
        this.file = new File(String.format("%s/%s",outputDir,filename));
        System.out.println(file.getAbsoluteFile());
        this.file.createNewFile();
        writer = new FileWriter(file);
    }

    void writeToFile(String content) throws IOException {
        writer.write(content+"\n");
        writer.flush();
    }


}
