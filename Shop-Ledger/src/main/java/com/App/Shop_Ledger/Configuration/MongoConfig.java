package com.App.Shop_Ledger.Configuration;
import com.App.Shop_Ledger.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Component;
@Component
public class MongoConfig {

    private MongoTemplate mongoTemplate;
     public MongoConfig(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
    @EventListener(ApplicationReadyEvent.class)
    public void initIndexes() {
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("name")
                .onField("category")
                .build();
        mongoTemplate.indexOps(Products.class).ensureIndex(textIndex);
        System.out.println("✅ MongoDB Text Index Created!");
    }
}
