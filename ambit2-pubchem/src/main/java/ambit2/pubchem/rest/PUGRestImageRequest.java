package ambit2.pubchem.rest;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class PUGRestImageRequest extends PUGRestRequest<BufferedImage> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8697178762300415861L;
	protected IMAGE_SIZE imageSize = IMAGE_SIZE.small;
	public IMAGE_SIZE getImageSize() {
		return imageSize;
	}
	public void setImageSize(IMAGE_SIZE imageSize) {
		this.imageSize = imageSize;
	}
	public RECORD_TYPE getRecordType() {
		return recordType;
	}
	public void setRecordType(RECORD_TYPE recordType) {
		this.recordType = recordType;
	}
	protected RECORD_TYPE recordType = RECORD_TYPE.D2;
	
	
	public enum RECORD_TYPE {
		D2 {
		@Override
		public String toString() {
			return "2d";
		}
		},
		D3 {
			@Override
			public String toString() {
				return "3d";
			}			
		};
	}	
	public enum IMAGE_SIZE {
		small,
		large,
		dimension;
	}
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
	public PUGRestImageRequest() {
		super();
		setOutput(COMPOUND_DOMAIN_OUTPUT.PNG);
	}

	@Override
	protected String getOptions() {
		switch (recordType) {
		case D3: {
			//Cannot (yet) generate arbitrarily-sized 3D images
			return String.format("?record_type=%s&image_size=%s", recordType.toString(),
					IMAGE_SIZE.dimension.equals(imageSize)?IMAGE_SIZE.large:imageSize.toString());
		}
		default: {
			return String.format("?record_type=%s&image_size=%s", recordType.toString(),
					IMAGE_SIZE.dimension.equals(imageSize)?String.format("%dx%d",getWidth(),getHeight()):imageSize.toString());	
		}
		}
		
	}
	@Override
	protected BufferedImage read(InputStream in) throws Exception {
		return ImageIO.read(in);
	}
	
}