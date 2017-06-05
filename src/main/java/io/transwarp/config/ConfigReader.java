package io.transwarp.config;

import io.transwarp.extractor.DocResolveException;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by zxh on 2017/5/16.
 */
public class ConfigReader {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ConfigReader.class);
    private static Properties config;

    //不可实例化类
    private ConfigReader(){
        throw new RuntimeException("ConfigReader not instantiable");
    }

    public static Properties getConfig() throws DocResolveException {
        if(config == null){
            config = new Properties();
            try {
                config.load(new FileInputStream(new File(ConfigReader.class.getClassLoader().getResource("config.properties").toURI())));
            } catch (IOException e) {
                LOGGER.info("Error in reading config file");
                throw new DocResolveException("Error in reading config file");
            } catch (URISyntaxException e) {
                LOGGER.info("Error in reading config file");
                throw new DocResolveException("Error in reading config file");
            }
        }
        return config;
    }

}
