package io.transwarp.docutils;

import io.transwarp.config.ConfigReader;
import io.transwarp.elasticsearch.IndexDocument;
import io.transwarp.extractor.DocResolveException;
import io.transwarp.extractor.DocumentType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * extract text from **.pdf
 * Created by zxh on 2017/5/1.
 */
public class PDFExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PDFExtractor.class);

    /**
     * extract text from document and index the text in ES
     */
    public void extract(File file, String out, IndexDocument indexDocument){
        PDDocument document = null;
        PDFTextStripper stripper;
        BufferedWriter writer = null;
        StringBuilder builder = new StringBuilder();
        String textFile = out + "\\" + file.getName() + ".txt";

        try {
            //提取PDF中文本输出到TEXT文件。
            writer = new BufferedWriter(new FileWriter(new File(textFile)));
            document = PDDocument.load(file);
            stripper = new PDFTextStripper();
            builder.append(stripper.getText(document));
            //写text到文本文件
            writer.write(builder.toString());
            writer.flush();
            LOGGER.info("Extract text from {}, write text to {}", file.getName(), textFile);
            indexDocument.addData(indexDocument.getId(), builder, file.getName(), DocumentType.pdf.name());
            LOGGER.info("Extract text from {}, load to ES", file.getName());
        } catch (IOException e) {
            LOGGER.error("Error in extracting text from {}, error msg is:{}", file.getAbsolutePath(), e.getMessage());
        } finally {
            try {
                if(null != writer){
                    writer.close();
                }

                if(null != document){
                    document.close();
                }
                if(null != builder){
                    builder.delete(0, builder.length() - 1);
                }
            } catch (IOException e) {
                LOGGER.error("Error in closing writer or document, msg is {}", e.getMessage());
            }
        }
    }
}
