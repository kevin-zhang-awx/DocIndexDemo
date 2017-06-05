package io.transwarp.docutils;

import io.transwarp.config.Constant;
import io.transwarp.elasticsearch.IndexDocument;
import io.transwarp.extractor.DocumentType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * extract text from **.docx( word 2007)
 * Created by zxh on 2017/5/1.
 */
public class DocxExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocxExtractor.class);

    private DocxExtractor() {
    }

    public static void extract(File file, String outDir, IndexDocument indexDocument) {
        String textFile = outDir + "\\" + file.getName() + ".txt";
        XWPFDocument document = null;
        XWPFWordExtractor extractor = null;
        BufferedWriter writer = null;
        InputStream is = null;
        StringBuilder builder = new StringBuilder();

        try {
            writer = new BufferedWriter(new FileWriter(textFile));
            is = new FileInputStream(file);

            document = new XWPFDocument(is);

            if (null == document) {
                LOGGER.info("document is null");
            }

            extractor = new XWPFWordExtractor(document);
            builder.append(extractor.getText());
            writer.write(builder.toString());
            writer.flush();

            indexDocument.addData(indexDocument.getId(),builder,file.getName(), DocumentType.docx.name());
            LOGGER.info("Extract text from {}, write text to {}", file.getName(), textFile);
            LOGGER.info("Extract text from {}, load to ES", file.getName());
        } catch (IOException e) {
            LOGGER.error("Error in extracting text from {}, error msg is:{}", file.getAbsolutePath(), e.getMessage());
        } finally {
            try {
                if(null != is){
                    LOGGER.info("closing inputstream");
                    is.close();
                }
                if (null != document) {
                    LOGGER.info("closing document");
                    document.close();
                }

                if (null != writer) {
                    LOGGER.info("closing ");
                    writer.close();
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
