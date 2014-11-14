package ambit2.db.reporters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.json.JSONUtils;
import ambit2.db.cache.RetrieveStructureImagePath;
import ambit2.rendering.CachedImage;

public class ImageAreaReporter<Q extends IQueryRetrieval<IStructureRecord>> extends AbstractImageReporter<Q,StringWriter> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6323248045172976921L;
	String jsonpcallback = null;
	
	public ImageAreaReporter(String mainType,String subType,String jsonpcallback) {
		this(mainType,subType,new Dimension(250,250),jsonpcallback);
	}
	public ImageAreaReporter(String mainType,String subType,Dimension dimension,String jsonpcallback) {
		super(mainType,subType,dimension);
		this.jsonpcallback = JSONUtils.jsonSanitizeCallback(jsonpcallback);
	}
	@Override
	protected RetrieveStructureImagePath initQuery(String mainType,String subType) {
		String mimeType = "image/png";
		img = Property.getInstance(mimeType,mimeType);
		RetrieveStructureImagePath q = new RetrieveStructureImagePath(mimeType);
		q.setQueryName(getQueryName());
		q.setPageSize(1);
		q.setPage(0);
		return q;
	}
	
	@Override
	protected CachedImage<StringWriter> createImageWrapper(BufferedImage image) {
		return new TheImageMap(image, "");
	}
	protected String readImageMapFile(File imgPath) {
		StringBuilder b = new StringBuilder();
	     BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(imgPath));
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
			String dimensions = getQueryName();
			String tmpDir = System.getProperty("java.io.tmpdir");
			File file = getFilePath(tmpDir, getConnection().getCatalog(), dimensions, item, "json");
			String json = ((file!=null) && file.exists())?readImageMapFile(file):"{\"a\": []}";// item.getProperty(img);
			getOutput().write(json);
			return null;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	
	}	
	@Override
	protected CachedImage<StringWriter> getCached(IStructureRecord item) {
		return null;
	}
	
	@Override
	public void open() throws DbAmbitException {
	}

	@Override
	public void footer(StringWriter output, Q query) {
		if (jsonpcallback!=null) try {
			getOutput().write(");");
		} catch (Exception x) {}
	};
	@Override
	public void header(StringWriter output, Q query) {
		if (jsonpcallback!=null) try {
			getOutput().write(jsonpcallback);
			getOutput().write("(");
			getOutput().flush();
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	};
	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	
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