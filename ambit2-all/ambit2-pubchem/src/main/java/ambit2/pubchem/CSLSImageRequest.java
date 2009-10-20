package ambit2.pubchem;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;

/**
 * http://cactus.nci.nih.gov/chemical/structure/c1ccccc1/image
 * @author nina
 *
 * @param <Result>
 */
public class CSLSImageRequest extends DefaultAmbitProcessor<String, BufferedImage> {
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
	
	public BufferedImage process(String target) throws AmbitException {
		try {
			return retrieveImage(new URL(
				String.format("http://cactus.nci.nih.gov/chemical/structure/%s/image?width=%d&height=%d", target,getWidth(),getHeight())));
		} catch (MalformedURLException x) {
			throw new AmbitException(x);
		}
	}
	public BufferedImage retrieveImage(URL url) throws AmbitException {

		InputStream in = null;
		HttpURLConnection uc = null;
		try {
			uc =(HttpURLConnection) url.openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("GET");
			in= uc.getInputStream();
			return ImageIO.read(uc.getInputStream());
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try {
				if (in != null) in.close();
			} catch (Exception x) { 
			}
		}
	}
	
}
