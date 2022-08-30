package com.jim.magicbox.utils;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoNamespace;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MongoDBUtil
 * @Description
 * @Author Jim
 * @Date 2022/3/29 20:21
 **/
@Slf4j
@Component
public class MongoDBUtil {

    @Autowired
    private MongoTemplate mongoTemplate;



    /**
     * @description 创建集合
     * zyt补充，添加处理事务相关的注解@Transactional，并抛出异常，让事务回滚
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/1 14:05
     **/
    @Transactional(propagation = Propagation.REQUIRED)//继承事务
    public void createCollection(String collName) {
        if (mongoTemplate.collectionExists(collName)){
            log.info("集合: " + collName + "   已经存在");
            return;
        }
        mongoTemplate.createCollection(collName);
    }

    /**
     * @description 重命名集合
     * @return
     * @exception
     * @author zyt
     * @date 2022/4/18 17:13
     **/
    public void renameCollection(String oldName,String newName) {
        if (mongoTemplate.collectionExists(newName)){
            log.info("集合: " + newName + "   已经存在，重命名错误");
            return;
        }
        mongoTemplate.getCollection(oldName).renameCollection(new MongoNamespace(mongoTemplate.getDb().getName(),
                newName));
    }


    /**
     * @description 创建单索引  （不会重复创建同一类型索引）
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/1 14:14
     **/
    private String createSingleIndex(String collName, String filedName, int sort, IndexOptions options) {
        //创建按filedName升序排的索引
        Bson bson = (sort == -1 ? Indexes.descending(filedName) : Indexes.ascending(filedName));
        if (options == null){
            options = new IndexOptions();
        }
        return mongoTemplate.getCollection(collName).createIndex(bson, options);
    }

    /**
     * @description 创建复合索引
     * @return 返回索引名称
     * @exception
     * @author Jim
     * @date 2022/4/1 14:14
     **/
    private String createCompositeIndex(String collName, Map<String, Integer> fields) {
        BasicDBObject compositeIndex = new BasicDBObject();
        for (String key : fields.keySet()){
            compositeIndex.put(key, fields.get(key));
        }
        // 创建索引
        return mongoTemplate.getCollection(collName).createIndex(compositeIndex);
    }

    /**
     * @description 创建哈希索引
     * @return 返回索引名称
     * @exception
     * @author Jim
     * @date 2022/4/1 14:14
     **/
    private String createHashIndex(String collName, String fieldName) {
        // 创建索引
        return mongoTemplate.getCollection(collName).createIndex(Indexes.hashed(fieldName));
    }

    /**
     * @description TODO 创建文本索引 (需要优化拓展更多功能)
     * @return 返回索引名称
     * @exception
     * @author Jim
     * @date 2022/4/1 14:14
     **/
    private String createTextIndex(String collName, String fieldName) {
        // 创建索引
        return mongoTemplate.getCollection(collName).createIndex(Indexes.text(fieldName));
    }

    /**
     * @description 根据索引名称删除索引
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 14:45
     **/
    public void removeIndex(String collectionName, String IndexName) {
        // 删除集合中某个索引
        mongoTemplate.getCollection(collectionName).dropIndex(IndexName);
    }

    /**
     * @description 删除集合内所有索引
     * 注意：集合内部默认存在_id的唯一索引 无法删除
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 14:45
     **/
    public void removeAllIndex(String collectionName) {
        // 删除集合中全部索引
        mongoTemplate.getCollection(collectionName).dropIndexes();
    }

    /**
     * @description  获取当前集合的所有索引
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/1 14:16
     **/
    public List<Document> getAllIndex(String collectionName) {
        // 获取集合中所有列表
        ListIndexesIterable<org.bson.Document> indexList = mongoTemplate.getCollection(collectionName).listIndexes();
        // 创建字符串集合
        List<Document> list = new ArrayList<>();
        // 获取集合中全部索引信息
        for (Document document : indexList) {
            log.info("索引列表：{}",document);
            list.add(document);
        }
        return list;
    }

    /**
     * @description 删除集合
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 15:47
     **/
    public boolean dropCollection(String collectionName) {
        mongoTemplate.dropCollection(collectionName);
        return true;
    }

    /**
     * @description 清空数据
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 16:03
     **/
    public void clearCollection(String collectionName) {
        Query query = new Query();
        mongoTemplate.remove(query, collectionName);
    }

    /**
     * @description 插入数据
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 15:56
     **/
    public <T> void insert(T info, String collectionName) {
        mongoTemplate.insert(info, collectionName);
    }

    /**
     * @description 插入多条数据
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 15:59
     **/
    public <T> void insertMulti(List<T> infos, String collectionName) {
        mongoTemplate.insert(infos, collectionName);
    }

    /**
     * @description  修改数据 id为mongo默认的主键_id
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 16:01
     **/
    public <T> void updateById(String id, String collectionName, T info) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        String str = JSONObject.toJSONString(info);
        JSONObject jQuery = JSONObject.parseObject(str);
        jQuery.forEach((key, value) -> {
            // 按照mongo自带的主键_id  不需要自定义ID
            if (!key.equals("id")) {
                update.set(key, value);
            }
        });
        mongoTemplate.updateMulti(query, update, info.getClass(), collectionName);
    }

    /**
     * @description 删除数据 id为mongo默认的主键_id
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 16:02
     **/
    public void deleteById(String id, String collectionName) {
        // 设置查询条件，当id=#{id}
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, collectionName);
    }


    /**
     * @description TODO 关联查询
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 20:03
     **/
    public void multiQuery(){
        // 筛选主集合数据
        //Criteria criteria = new Criteria().and("price").lt(30);

        // 多表关联查询设置
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("merchant")  // 同一个数据库下等待被Join的集合。
                .localField("_id")	// 源集合中的match值，如果输入的集合中，某文档没有 localField 这个Key（Field），在处理的过程中，会默认为此文档含 有 localField：null的键值对。
                .foreignField("materialCode")	// 被关联字段
                .as("merchant_as");	// 为输出文档的新增值命名。如果输入的集合中已存在该值，则会覆盖掉

        // 输出设置: 指定输出文档里的字段
        ProjectionOperation project = Aggregation.project("_id","name","price")
                .and("merchant_as").as("merchants");

        SortOperation sortOperation = Aggregation.sort(Sort.Direction.ASC, "merchants._id");

        //Criteria criteria2 = new Criteria().and("merchantName").is("张三建材");

        // 多个Aggregation 是按先后顺序执行的，即后面一个在前面一个的基础上作筛选等操作，所以顺序不一致可能会报错
        Aggregation aggregation = Aggregation.newAggregation(lookupOperation,project, sortOperation);

        // 查询哪个集合，查询条件：多个Aggregation
        List<Map> result =  mongoTemplate.aggregate(aggregation,"material",Map.class).getMappedResults();
        System.out.println(result);
    }

    /**
     * @description TODO 执行原生mongodb语句
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/5 20:26
     **/
    public void executeMql(String mql){
        String jsonCommand = "db.material.aggregate([\n" +
                "    {\n" +
                "        $match:{\"price\" : { \"$lt\" : 30 } }\n" +
                "    },\n" +
                "    {\n" +
                "        $lookup:{\n" +
                "            from: \"merchant\",\n" +
                "            localField: \"_id\",\n" +
                "            foreignField: \"materialCode\",\n" +
                "            as: \"merchant_as\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        $project:{\n" +
                "            \"_id\":1,\n" +
                "            \"name\":1,\n" +
                "            \"price\":1,\n" +
                "            \"merchantName\":\"$merchant_as.name\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        $unwind:\"$merchantName\"\n" +
                "    },\n" +
                "    {\n" +
                "        $match : { \"merchantName\" : \"张三建材\" }\n" +
                "    },\n" +
                "])";
        mongoTemplate.getDb().runCommand(Document.parse(jsonCommand), Document.class);
        System.out.println("document");
    }

    public  List<String> query(){
        Query query = new Query();
        query.addCriteria(Criteria.where("title").is("PHP 教程"));


        List<String> list = mongoTemplate.find(query, String.class,"test1");
        return list;
    }

    /**
     * 多个查询条件同时满足
     * @return
     */
    public List<Map> andQuery(String collName) {
        //Query query = new Query(Criteria.where("user").is("用户名blog").and("age").is(18));
        Query query = new Query();
        List<Map> list = mongoTemplate.find(query, Map.class, collName);
        //System.out.println("query: " + query + " | andQuery: " + result);
        return list;
    }
}

