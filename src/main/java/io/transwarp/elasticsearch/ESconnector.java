package io.transwarp.elasticsearch;

import io.transwarp.config.Constant;
import org.apache.xmlbeans.impl.jam.internal.elements.ConstructorImpl;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by zxh on 2017/5/17.
 */
public class ESconnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESconnector.class);
    private static Client client;

    //不可实例化类
    private ESconnector() {
        //nothing
    }

    /**
     * 单例模式获取TransportClient
     * @param config
     * @return
     */
    public static Client getESClient(Properties config) {
        if (null == client) {
            Settings settings = Settings.settingsBuilder()
                                            .put("cluster.name", config.getProperty(Constant.CONF_KEY_ES_CLUSTER_NAME))
                                            .put("client.transport.sniff",true)
                                        .build();
            String nodes = config.getProperty(Constant.CONF_KEY_ES_CLUSTER_IP);
            int port = Integer.parseInt(config.getProperty(Constant.CONF_KEY_ES_CLUSTER_PORT));
            String[] IPs = nodes.split(",");

            try {
                client = TransportClient.builder().settings(settings).build()
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(IPs[0]),port))
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(IPs[1]),port))
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(IPs[2]),port));

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return client;
    }
}
