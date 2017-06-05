package io.transwarp.elasticsearch;

import io.transwarp.config.ConfigReader;
import io.transwarp.extractor.DocResolveException;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by zxh on 2017/5/27.
 */
public class IndexDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexDocument.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Client client;
    private Properties config;
    private AtomicInteger id;

    public IndexDocument() {
        try {
            config = ConfigReader.getConfig();
        } catch (DocResolveException e) {
            LOGGER.info("Error in reading config, error msg is {}",e.getMessage());
        }
        client = ESconnector.getESClient(config);
    }

    public void createIndex() throws IOException {
        //创建索引
        client.admin().indices().prepareCreate("documents").execute().actionGet();
        //创建索引结构
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("office")
                        .startObject("properties")
                            .startObject("fileName")
                                .field("type","string")
                                .field("store","false")
                                .field("analyzer","ik")
                                .field("index","analyzed")
                            .endObject()
                            .startObject("content")
                                .field("type","string")
                                .field("store","true")
                                .field("analyzer","ik")
                                .field("index","analyzed")
                            .endObject()
                            .startObject("docType")
                                .field("type","string")
                                .field("store","false")
                                .field("index","not_analyzed")
                            .endObject()
                            .startObject("date")
                                .field("type","string")
                                .field("store","false")
                                .field("index","not_analyzed")
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest("documents").type("office").source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
    }



    public void addData(int id, StringBuilder textBuilder, String fileName, String docType){
        if(client == null){
            LOGGER.error("Not connect ES");
            return;
        }


        IndexRequestBuilder builder = client.prepareIndex("documents","office", String.valueOf(id));

        try {
            builder.setSource(jsonBuilder().startObject()
                    .field("fileName", fileName)
                    .field("docType", docType)
                    .field("content",textBuilder.toString())
                    .field("date", DATE_FORMAT.format(new Date()))
                    .endObject()
            ).execute().actionGet();
        } catch (IOException e) {
            LOGGER.error("Error in index document,{}",e.getMessage());
        }
    }

    public int getId(){
        if(id == null){
            id = new AtomicInteger(0);
        }
        return id.getAndIncrement();

    }

    public void close(){
        if(null != client){
            client.close();
        }
    }


    public void searchDocs(String field, String keyWord) throws ExecutionException, InterruptedException {
        QueryBuilder queryBuilder = QueryBuilders.termQuery(field, keyWord);

       /* SearchResponse response = client.prepareSearch("documents")
                .setTypes("office")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .setExplain(true).setFrom(0).setSize(1000)
                .execute()
                .get();*/


        GetResponse getResponse = client.prepareGet("documents","office","0").get();
        System.out.println("SIGN1");
        SearchResponse response1 = client.prepareSearch("documents").get();
        System.out.println("SIGN2");






       /* SearchHits hits = response.getHits();

        for(SearchHit hit:hits){
            Map<String, SearchHitField> result = hit.getFields();
            for(Map.Entry<String, SearchHitField> entry: result.entrySet()){
                if(entry.getKey().compareToIgnoreCase("content") == 0)
                    continue;
                LOGGER.info("{}:{}:{}",entry.getKey(), entry.getValue().getName(), entry.getValue().getValue());
            }
        }*/
    }

    public void getDoc(String index, String type, String id){
        System.out.println();
        GetResponse response = client.prepareGet(index, type, id).get();

        System.out.println(response.getIndex());
        System.out.println(response.getSourceAsMap().size());
        System.out.println(response.getSourceAsString());
        //Map<String, GetField> map = response.getFields();

        Map<String, Object> map = response.getSourceAsMap();

        System.out.println(map.keySet());
    }

}
