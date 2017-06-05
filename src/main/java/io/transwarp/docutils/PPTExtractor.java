package io.transwarp.docutils;

import io.transwarp.elasticsearch.IndexDocument;
import io.transwarp.extractor.DocumentType;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * extract text from **.ppt (powerpoint 97)
 * Created by zxh on 2017/5/4.
 */
public class PPTExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PPTExtractor.class);
    private PPTExtractor() {
        //nothing
    }

    public static void extract(File file, String outDir, IndexDocument indexDocument) {
        String textFile = outDir + "\\" + file.getName() + ".txt";
        PowerPointExtractor extractor = null;
        BufferedWriter writer = null;
        StringBuilder builder = new StringBuilder();

        try {
            writer = new BufferedWriter(new FileWriter(file));
            extractor = new PowerPointExtractor(new FileInputStream(file));

            builder.append(extractor.getText(true,true));
            writer.write(builder.toString());
            writer.flush();
            LOGGER.info("Extract text from {}, write text to {}", file.getName(), textFile);

            indexDocument.addData(indexDocument.getId(), builder, file.getName(), DocumentType.ppt.name());
            LOGGER.info("Extract text from {}, load to ES", file.getName());
        } catch (IOException e) {
            LOGGER.error("Error in extracting text from {}, error msg is:{}", file.getAbsolutePath(), e.getMessage());
        } finally {
            try {
                if (null != writer) {
                    writer.close();
                }
                if (null != extractor) {
                    extractor.close();
                }

                if(null != builder){
                    builder.delete(0, builder.length() - 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
