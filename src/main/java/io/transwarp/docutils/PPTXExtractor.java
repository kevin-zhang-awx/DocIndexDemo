package io.transwarp.docutils;

import io.transwarp.elasticsearch.IndexDocument;
import io.transwarp.extractor.DocumentType;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * extract text from **.pptx (powerpoint 2007)
 * Created by zxh on 2017/5/17.
 */
public class PPTXExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PPTXExtractor.class);

    private PPTXExtractor() {
        //nothing
    }

    public static void extract(File file, String outDir, IndexDocument indexDocument) {
        String textFile = outDir + "\\" + file.getName() + ".txt";
        XSLFSlideShow ppt = null;
        XSLFPowerPointExtractor extractor = null;
        StringBuilder builder = new StringBuilder();
        BufferedWriter writer = null;


        try {
            writer = new BufferedWriter(new FileWriter(textFile));

            ppt = new XSLFSlideShow(file.getAbsolutePath());
            extractor = new XSLFPowerPointExtractor(ppt);
            builder.append(extractor.getText(true, true));
            writer.write(builder.toString());
            writer.flush();
            LOGGER.info("Extract text from {}, write text to {}", file.getName(), textFile);
            indexDocument.addData(indexDocument.getId(), builder, file.getName(), DocumentType.pptx.name());
            LOGGER.info("Extract text from {}, load to ES", file.getName());
        } catch (IOException e) {
            LOGGER.error("Error in extracting text from {}, error msg is:{}", file.getAbsolutePath(), e.getMessage());
        } catch (OpenXML4JException e) {
            LOGGER.error("Error in extracting text from {}, error msg is:{}", file.getAbsolutePath(), e.getMessage());
        } catch (XmlException e) {
            LOGGER.error("Error in extracting text from {}, error msg is:{}", file.getAbsolutePath(), e.getMessage());
        } finally {
            try {
                if (null != writer) {
                    writer.close();
                }

                if (null != ppt) {
                    ppt.close();
                }

                if (null != extractor) {
                    extractor.close();
                }
                if (null != builder) {
                    builder.delete(0, builder.length() - 1);
                }
            } catch (IOException e) {
                LOGGER.error("Error in closing object, msg is {}", e.getMessage());
            }
        }
    }
}
