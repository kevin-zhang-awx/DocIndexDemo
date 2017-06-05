package io.transwarp.extractor;

import io.transwarp.config.Constant;
import io.transwarp.elasticsearch.IndexDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zxh on 2017/5/16.
 */
public class TextExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextExtractor.class);
    private Properties config;
    private ExecutorService threadPool;
    private IndexDocument indexDocument;

    public TextExtractor(Properties config) {
        this.config = config;
        threadPool = Executors.newFixedThreadPool(
                Integer.parseInt(config.getProperty(Constant.THREAD_POOL_SIZE,"4")));
    }

    public void extractText()  {
        String doc_dir = config.getProperty(Constant.DOC_DIR);
        File file = new File(doc_dir);

        //创建ES index
        indexDocument = new IndexDocument();
        try {
            indexDocument.createIndex();
        } catch (IOException e) {
            LOGGER.info("Error in create index, msg:{}",e.getMessage());
        }

        File[] files = file.listFiles();
        for (File f : files) {
            String path = f.getAbsolutePath();
            String typeStr = path.substring(path.lastIndexOf(Constant.DOT)+1);
            DocumentType type;

            try {
                type = DocumentType.valueOf(typeStr);
            }catch (IllegalArgumentException e){
                LOGGER.warn("Type is not supported:{}",typeStr);
                continue;
            }

            threadPool.submit(new ExtractorWorker(f, type.ordinal(), config, indexDocument));
        }


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            indexDocument.searchDocs("content","西安交通大学");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //threadPool.shutdown();

        try {
            if(!threadPool.awaitTermination(Constant.THREADPOOL_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)){
                threadPool.shutdownNow();
                LOGGER.warn("shutdown thread pool timeout:{} s", Constant.THREADPOOL_SHUTDOWN_TIMEOUT);
            }
            LOGGER.info("thread pool shutdown!");
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            LOGGER.error("await thread pool shutdown is interrupted.");
        }

        indexDocument.close();

    }


}
