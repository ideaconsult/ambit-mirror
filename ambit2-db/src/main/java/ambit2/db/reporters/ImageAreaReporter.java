package ambit2.db.reporters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;

public class ImageAreaReporter<Q extends IQueryRetrieval<IStructureRecord>> extends AbstractImageReporter<Q,StringWriter> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6323248045172976921L;

	public ImageAreaReporter(String mainType,String subType) {
		this(mainType,subType,new Dimension(250,250));
	}
	public ImageAreaReporter(String mainType,String subType,Dimension dimension) {
		super(mainType,subType,dimension);

	}
	@Override
	protected CachedImage<StringWriter> createImageWrapper(BufferedImage image) {
		return new TheImageMap(image, "");
	}
	protected String readImageMapFile(String imgPath) {
		StringBuilder b = new StringBuilder();
	     BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(new File(imgPath)));
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                b.append(line).append(System.getProperty("line.separator"));
	            }
	        } catch (IOException e) {

	        } finally {
	            try { if (reader != null)  reader.close(); } catch (IOException e) {}
	        }
	   return b.toString();     
	}
	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			CachedImage result = getCached(item);
			if (result.getImageMap() == null) {
				result = createImage(item);
				if (result.getImage()!=null) {
					cache(item,result.getImage());
				}
			}
			StringWriter w = new StringWriter();
			w.write(result.getImageMap()==null?"":result.getImageMap());
			setOutput(w);
			return result;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	
	}	
	@Override
	protected CachedImage<StringWriter> getCached(IStructureRecord item) {
		try {
			imageWrapper.setImage(null);
			imageWrapper.setProperty(null);
			Object path = item.getProperty(img);
			if (path != null) {
				File file = new File(path.toString());
				if (file.exists()) {
					imageWrapper.setImage(null);
					imageWrapper.setImageMap(readImageMapFile(path.toString()));
					return imageWrapper;
				}
			} 
			return imageWrapper;
		} catch (Exception x) {
			logger.warn(x);
			return null;
		}
	}
	
	@Override
	public void open() throws DbAmbitException {
	}

	public void footer(StringWriter output, Q query) {};
	public void header(StringWriter output, Q query) {};
}

class TheImageMap extends CachedImage<StringWriter> {
	public TheImageMap(BufferedImage image, String path) {
		super(image,path);
	}
	@Override
	public StringWriter getProperty() {
		StringWriter w = new StringWriter();
		w.write(imageMap==null?"":imageMap);
		return w;
	}
	
}