package org.resign.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.resign.assembler.ImageResourceAssembler;
import org.resign.config.ImageStorageConfiguration;
import org.resign.repo.Image;
import org.resign.repo.ImageRepository;
import org.resign.repo.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ImagesController {

	private static final Logger log = LoggerFactory.getLogger(ImagesController.class);
	
	@Autowired 
	ImageStorageConfiguration imageStorageConfiguration;
	
	@Autowired
	ImageRepository imageRepository;
	
	@Autowired
	ResourceRepository resourceRepository;
	
	@Autowired
	ImageResourceAssembler imageResourceAssembler;
	
	@RequestMapping(value = "/view/{imageId}", method = RequestMethod.GET)
	public ResponseEntity<?> view(@PathVariable String imageId) {
		
		Optional<Image> optionalImage = imageRepository.findById(imageId);
		
		if(!optionalImage.isPresent()) {
			log.warn("Image {} not found", imageId);
			return new ResponseEntity<String>("Image not found", HttpStatus.NOT_FOUND);
		}
		
		try {
				
			Image image = optionalImage.get();
			
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(image.getMimeType()));
	
			if(image.getType() == Image.FILE_SYSTEM) {
				
				Path output = Paths.get(imageStorageConfiguration.getStorageLocationPath()).resolve(image.getPath());
				log.debug("Found image {}. Path: {}", imageId, output.toString());
				return new ResponseEntity<byte[]>(
						IOUtils.toByteArray(new FileInputStream(new File(output.toString()))), 
						headers, 
						HttpStatus.OK);
			} else {
				log.warn("Image {} not found on file system", imageId);
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			}
		
		} catch (Exception e) {
			log.error(ExceptionUtils.getFullStackTrace(e));
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
