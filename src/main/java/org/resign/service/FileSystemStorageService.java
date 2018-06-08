package org.resign.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.resign.config.ImageStorageConfiguration;
import org.resign.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileSystemStorageService {

	@Autowired 
	ImageStorageConfiguration imageStorageConfiguration;
	
    public Path store(InputStream inputStream, String fileName) throws StorageException {
        try {
        	
            Path output = Paths.get(imageStorageConfiguration.getStorageLocationPath()).resolve(fileName);
            Files.copy(inputStream, output);
            
            return output;
            
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + fileName, e);
        }
    }
}
