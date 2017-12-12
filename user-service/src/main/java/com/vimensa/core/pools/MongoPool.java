package com.vimensa.core.pools;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class MongoPool {
    private static Logger logger = LoggerFactory.getLogger(MongoPool.class.getName());

    public static void init() throws IOException {
        MongoConfig.init();
        newConnection();
    }

    private static MongoClient mongoClient;

    public static void newConnection() {
        MongoCredential credential = MongoCredential.createCredential(MongoConfig.MONGODB_USERNAME,
                MongoConfig.MONGODB_DATABASE, MongoConfig.MONGODB_PASSWORD.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(MongoConfig.MONGODB_HOST,
                MongoConfig.MONGODB_PORT), Arrays.asList(credential));
    }

    public static MongoDatabase getDB() {
        if (mongoClient == null) {
            MongoPool.newConnection();
        }
        return mongoClient.getDatabase(MongoConfig.MONGODB_DATABASE);
    }

    public static void log(String collectionName, Document doc) {
        MongoDatabase db = MongoPool.getDB();
        MongoCollection<Document> col = db.getCollection(collectionName);
        col.insertOne(doc);
        logger.debug("Log to {} : {}", collectionName, doc.toJson());
    }

    public static DB getDBJongo() {
        if (mongoClient == null) {
            MongoPool.newConnection();
        }
        return mongoClient.getDB(MongoConfig.MONGODB_DATABASE);
    }
}
