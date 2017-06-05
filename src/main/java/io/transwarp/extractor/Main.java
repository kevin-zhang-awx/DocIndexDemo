package io.transwarp.extractor;

import io.transwarp.config.ConfigReader;
import io.transwarp.elasticsearch.ESconnector;
import io.transwarp.elasticsearch.IndexDocument;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.Config;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by zxh on 2017/5/1.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private Properties config;


    public void start(){
        try {
            config = ConfigReader.getConfig();
        } catch (DocResolveException e) {
            LOGGER.error("Error in reading config, error msg is {}", e.getMessage());
        }

        TextExtractor extractor = new TextExtractor(config);
        extractor.extractText();
    }

    public void search(){
        try {
            config = ConfigReader.getConfig();
        } catch (DocResolveException e) {
            e.printStackTrace();
        }

        IndexDocument indexDocument = new IndexDocument();
        try {
            indexDocument.searchDocs("content", "test");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //indexDocument.getDoc("documents","office", "0");
    }

    public static void main(String[] args) throws DocResolveException, IOException {
        new Main().search();
    }
}
