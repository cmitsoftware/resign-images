package org.resign.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.resign.config.ImageStorageConfiguration;
import org.resign.controller.ImagesController;
import org.resign.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileSystemStorageService {

	private static final Logger log = LoggerFactory.getLogger(FileSystemStorageService.class);
	
	@Autowired 
	ImageStorageConfiguration imageStorageConfiguration;
	
    public Path store(InputStream inputStream, String fileName) throws StorageException {
    	
    	log.info("Saving image {}", fileName);
        
    	try {
        
    		Path output = Paths.get(imageStorageConfiguration.getStorageLocationPath()).resolve(fileName);
            Files.copy(inputStream, output);
            
            return output;
            
        } catch (IOException e) {
        	log.error(ExceptionUtils.getFullStackTrace(e));
            throw new StorageException("Failed to store file " + fileName, e);
        }
    }
    
    public void delete(String fileName) {
    	
    	log.info("Deleting image {}", fileName);
        
    	try {

        	Path deleteMe = Paths.get(imageStorageConfiguration.getStorageLocationPath()).resolve(fileName);
            Files.delete(deleteMe);
            
        } catch (IOException e) {
        	log.error(ExceptionUtils.getFullStackTrace(e));
            throw new StorageException("Failed to delete file " + fileName, e);
        }
    }
}
