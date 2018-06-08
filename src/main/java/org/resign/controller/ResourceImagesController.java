package org.resign.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.tika.Tika;
import org.resign.assembler.ImageResource;
import org.resign.assembler.ImageResourceAssembler;
import org.resign.embedded.ResourceImage;
import org.resign.exception.StorageException;
import org.resign.repo.Image;
import org.resign.repo.ImageRepository;
import org.resign.repo.Resource;
import org.resign.repo.ResourceRepository;
import org.resign.service.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ResourceImagesController {

	@Autowired
	private FileSystemStorageService fileSystemStorageService;
	
	@Autowired
	ImageRepository imageRepository;
	
	@Autowired
	ResourceRepository resourceRepository;
	
	@Autowired
	ImageResourceAssembler imageResourceAssembler;
	
	
	@RequestMapping(value="/resources/{resourceId}/update", method = {RequestMethod.PUT})
	public ResponseEntity<?> update(
			@PathVariable String resourceId, 
			@RequestParam MultipartFile image, 
			@RequestParam String imageId,
			@RequestParam String name, 
			@RequestParam String desc) {

		try {
			
			/*
			 * Check if resource exists
			 */
			Optional<Resource> optionalResource = resourceRepository.findById(resourceId);
			if(!optionalResource.isPresent()) {
				return new ResponseEntity<String>("Resource not found", HttpStatus.NOT_FOUND);
			}
			Resource resource = optionalResource.get();

			Tika tika = new Tika();
			String uuid = UUID.randomUUID().toString();
			
			/*
			 * Save the image on file system
			 */
			Path output = fileSystemStorageService.store(image.getInputStream(), uuid);
			String mimeType = tika.detect(output);
			
			/*
			 * Check if image exists
			 */
			Image img = new Image();
			Optional<Image> optionalImage = imageRepository.findById(imageId);
			if(optionalImage.isPresent()) {
				img = optionalImage.get();
				String oldPath = img.getPath();
				
				/*
				 * Save the image on db
				 */
				img.setPath(uuid);
				img.setMimeType(mimeType);
				img.setDesc(desc);
				img.setName(name);
				img.setType(Image.FILE_SYSTEM);
				img = imageRepository.save(img);
				
				/*
				 * Update the image on the resource
				 */
				
				if(resource.getImages() != null) {
					for(ResourceImage ri: resource.getImages()) {
						if(ri.getImageId().equals(img.getId())) {
							ri.setImageId(img.getId());
							ri.setName(img.getName());
							ri.setDesc(img.getDesc());
							ri.setMimeType(img.getMimeType());
						}
					}
				} else {
					ResourceImage resourceImage = new ResourceImage();
					resourceImage.setImageId(img.getId());
					resourceImage.setName(img.getName());
					resourceImage.setDesc(img.getDesc());
					resourceImage.setMimeType(img.getMimeType());
					resource.setImages(new ArrayList<ResourceImage>());
					resource.getImages().add(resourceImage);
				}
				resourceRepository.save(resource);

				/*
				 * Delete the old image on the file system
				 */
				try {
					/*
					 * TODO
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return new ResponseEntity<ImageResource>(imageResourceAssembler.toResource(img), HttpStatus.OK);
			}
			
			return new ResponseEntity<String>("Image not found", HttpStatus.NOT_FOUND);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@RequestMapping(value="/resources/{resourceId}/add", method = {RequestMethod.POST})
	public ResponseEntity<?> add(
			@PathVariable String resourceId, 
			@RequestParam MultipartFile image, 
			@RequestParam String name, 
			@RequestParam String desc) {

		try {
			
			/*
			 * Check if resource exists
			 */
			Optional<Resource> optionalResource = resourceRepository.findById(resourceId);
			if(!optionalResource.isPresent()) {
				return new ResponseEntity<String>("Resource not found", HttpStatus.NOT_FOUND);
			}
			Resource resource = optionalResource.get();

			Tika tika = new Tika();
			String uuid = UUID.randomUUID().toString();
			
			/*
			 * Save the image on file system
			 */
			Path output = fileSystemStorageService.store(image.getInputStream(), uuid);
			String mimeType = tika.detect(output);
			
			/*
			 * Save the image on db
			 */
			Image img = new Image();
			img.setPath(uuid);
			img.setMimeType(mimeType);
			img.setDesc(desc);
			img.setName(name);
			img.setType(Image.FILE_SYSTEM);
			img = imageRepository.save(img);
			
			/*
			 * Add the image to the resource
			 */
			ResourceImage resourceImage = new ResourceImage();
			resourceImage.setImageId(img.getId());
			resourceImage.setName(img.getName());
			resourceImage.setDesc(img.getDesc());
			resourceImage.setMimeType(img.getMimeType());
			if(resource.getImages() == null) {
				resource.setImages(new ArrayList<ResourceImage>());
			}
			resource.getImages().add(resourceImage);
			resourceRepository.save(resource);
			
			return new ResponseEntity<ImageResource>(imageResourceAssembler.toResource(img), HttpStatus.OK); 
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value="/resources/{resourceId}/delete", method = {RequestMethod.DELETE})
	public ResponseEntity<?> delete(
			@PathVariable String resourceId, 
			@RequestParam String imageId) {

		try {
			
			/*
			 * Check if resource exists
			 */
			Optional<Resource> optionalResource = resourceRepository.findById(resourceId);
			if(!optionalResource.isPresent()) {
				return new ResponseEntity<String>("Resource not found", HttpStatus.NOT_FOUND);
			}
			Resource resource = optionalResource.get();

			/*
			 * Check if image exists
			 */
			Optional<Image> optionalImage = imageRepository.findById(imageId);
			if(optionalImage.isPresent()) {
				Image img = optionalImage.get();
				String oldPath = img.getPath();
				
				/*
				 * Delete the image on the resource
				 */
				List<ResourceImage> updatedList = new ArrayList<ResourceImage>();
				if(resource.getImages() != null) {
					for(ResourceImage ri: resource.getImages()) {
						if(!ri.getImageId().equals(img.getId())) {
							updatedList.add(ri);						}
					}
					resource.setImages(updatedList);
				}
				resourceRepository.save(resource);

				/*
				 * Delete the image
				 */
				imageRepository.delete(img);
				
				/*
				 * Delete the old image on the file system
				 */
				try {
					/*
					 * TODO
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return new ResponseEntity<String>(imageId, HttpStatus.OK);
			}
			
			return new ResponseEntity<String>("Image not found", HttpStatus.NOT_FOUND);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
