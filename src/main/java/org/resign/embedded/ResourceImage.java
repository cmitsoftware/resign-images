package org.resign.embedded;

/**
 * Utility class to store images info on resources as embedded items
 *  
 * @author cerri
 */
public class ResourceImage {

	public ResourceImage() {}
	
	String imageId;
	private String name;
    private String desc;
    private String mimeType;
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
}
