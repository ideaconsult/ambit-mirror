package ambit2.search.csls;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import ambit2.pubchem.NCISearchProcessor;

/**
 * http://cactus.nci.nih.gov/chemical/structure/c1ccccc1/image
 * @author nina
 *
 * @param <Result>
 */
public class CSLSImageRequest extends CSLSRequest<BufferedImage> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8697178762300415861L;
	protected int width = 400;
	protected int height = 200;
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public CSLSImageRequest() {
		super();
		representation = NCISearchProcessor.METHODS.image;
	}

	@Override
	protected String getOptions() {
		return String.format("?width=%d&height=%d", getWidth(),getHeight());
	}
	@Override
	protected BufferedImage read(InputStream in) throws Exception {
		return ImageIO.read(in);
	}
	
}
