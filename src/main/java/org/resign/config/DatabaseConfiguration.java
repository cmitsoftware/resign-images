package org.resign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;

@Configuration
@PropertySource(
		value = {"file:${spring.config.additional-location}/db.properties"},
		ignoreResourceNotFound = true)
public class DatabaseConfiguration extends AbstractMongoConfiguration {
	
	@Value("${db.host}")
    String host;
	
	@Value("${db.port}")
	Integer port;
	
	@Value("${db.name}")
    String name;
	
    @Override
    protected String getDatabaseName() {
        return name;
    }

	@Override
	public MongoClient mongoClient() {
		return new MongoClient(host, port);
	}
}