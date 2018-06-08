package org.resign.assembler;

import org.resign.controller.ImagesController;
import org.resign.controller.ResourceImagesController;
import org.resign.repo.Image;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ImageResourceAssembler extends ResourceAssemblerSupport<Image, ImageResource> {

	public ImageResourceAssembler() {
		super(ResourceImagesController.class, ImageResource.class);
	}

	@Override
	public ImageResource toResource(Image image) {

		ImageResource resource = createResource(image);
		
		Link viewLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ImagesController.class)
			      .view(image.getId())).withRel("view");
		resource.add(viewLink);
		
		return resource;
	}
	
	private ImageResource createResource(Image image) {
		
		ImageResource imageResource = new ImageResource();
		imageResource.setImageId(image.getId());
		imageResource.setDesc(image.getDesc());
		imageResource.setMimeType(image.getMimeType());
		imageResource.setName(image.getName());
		imageResource.setPath(image.getPath());
		imageResource.setType(image.getType());
		return imageResource;
	}
}
