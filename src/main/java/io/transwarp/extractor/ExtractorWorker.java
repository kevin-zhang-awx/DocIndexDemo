package io.transwarp.extractor;

import com.sun.xml.internal.txw2.output.IndentingXMLFilter;
import io.transwarp.config.Constant;
import io.transwarp.docutils.*;
import io.transwarp.elasticsearch.IndexDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by zxh on 2017/5/16.
 */
public class ExtractorWorker implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractorWorker.class);
    private File file;
    private int docType;
    private String textDir;
    private IndexDocument indexDocument;

    public ExtractorWorker(File file, int docType, Properties config, IndexDocument index) {
        this.file = file;
        this.docType = docType;
        this.textDir = config.getProperty(Constant.TEXT_OUT_DIR);
        this.indexDocument = index;
    }

    public void run() {
        LOGGER.info("{} start extracting doc:{}",Thread.currentThread().getName(), file.getAbsolutePath());
        switch (docType) {
            case 0:
                new PDFExtractor().extract(file, textDir, indexDocument);
                break;
            case 1:
                DocExtractor.extract(file, textDir, indexDocument);
                break;
            case 2:
                DocxExtractor.extract(file, textDir, indexDocument);
                break;
            case 3:
                PPTExtractor.extract(file, textDir, indexDocument);
                break;
            case 4:
                PPTXExtractor.extract(file, textDir, indexDocument);
                break;
            default:
                System.out.println("DO NOTHING");
        }
        LOGGER.info("{} finish extracting doc:{}",Thread.currentThread().getName(), file.getAbsolutePath());
    }
}
