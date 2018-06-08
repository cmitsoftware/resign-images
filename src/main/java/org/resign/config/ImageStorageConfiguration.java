package org.resign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
		value = {"file:${spring.config.additional-location}/storage.properties"},
		ignoreResourceNotFound = true)
public class ImageStorageConfiguration {

	@Value("${storage.location}")
    private String storageLocationPath;

	public String getStorageLocationPath() {
		return storageLocationPath;
	}

	public void setStorageLocationPath(String storageLocationPath) {
		this.storageLocationPath = storageLocationPath;
	}
	
	
}
