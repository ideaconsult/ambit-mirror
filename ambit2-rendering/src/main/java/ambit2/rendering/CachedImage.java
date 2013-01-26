package ambit2.rendering;

import java.awt.image.BufferedImage;

public class CachedImage<MORE> {
	protected BufferedImage image;
	protected String imageMap = null;
	public String getImageMap() {
		return imageMap;
	}
	public void setImageMap(String imageMap) {
		this.imageMap = imageMap;
	}
	MORE property;
	public CachedImage(BufferedImage image, String path) {
		this.image = image;
	}
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public MORE getProperty() {
		return property;
	}
	public void setProperty(MORE property) {
		this.property = property;
	}
	
}