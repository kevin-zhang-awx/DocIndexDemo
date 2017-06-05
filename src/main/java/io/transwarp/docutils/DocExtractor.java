package io.transwarp.docutils;

import io.transwarp.elasticsearch.IndexDocument;
import io.transwarp.extractor.DocumentType;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * extract text from **.doc file (word 97)
 * Created by zxh on 2017/5/16.
 */
public class DocExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocExtractor.class);

    private DocExtractor(){
        //不可实例化
    }
    /**
     * extract  text from document
     */
    public static void extract(File file, String out, IndexDocument indexDocument) {
        String textFile = out + "\\" + file.getName() + ".txt";
        HWPFDocument document;
        BufferedWriter writer = null;

        StringBuilder builder = new StringBuilder();

        try {
            writer = new BufferedWriter(new FileWriter(textFile));
            document = new HWPFDocument(new FileInputStream(file));
            Range range = document.getRange();
            builder.append(range.text());
            writer.write(builder.toString());
            writer.flush();
            LOGGER.info("Extract text from {}, write text to {}", file.getName(), textFile);
            indexDocument.addData(indexDocument.getId(),builder, file.getName(), DocumentType.doc.name());
            LOGGER.info("Extract text from {}, load to ES", file.getName());
        } catch (IOException e) {
            LOGGER.error("Error in extracting text from {}, error msg is:{}", file.getAbsolutePath(), e.getMessage());
        }finally {
            try {
                if(null != writer){
                    writer.close();
                }
                if(null != builder){
                    builder.delete(0, builder.length() - 1);
                }
            } catch (IOException e) {
                LOGGER.error("Error in closing writer, msg is {}", e.getMessage());
            }
        }
    }
}
