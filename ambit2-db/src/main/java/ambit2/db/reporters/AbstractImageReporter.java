package ambit2.db.reporters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.SessionID;
import ambit2.db.UpdateExecutor;
import ambit2.db.cache.RetrieveStructureImagePath;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.update.IQueryUpdate;
import ambit2.db.update.storedquery.CreateStoredQuery;
import ambit2.rendering.CompoundImageTools;

public abstract class AbstractImageReporter<Q extends IQueryRetrieval<IStructureRecord>,OUTPUT> extends QueryReporter<IStructureRecord, Q,OUTPUT > {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8546489672452997303L;
	protected CompoundImageTools depict = new CompoundImageTools();
	protected MoleculeReader reader = new MoleculeReader();
	protected CreateStoredQuery cache ;
	protected UpdateExecutor<IQueryUpdate> cacheUpdater = new UpdateExecutor<IQueryUpdate>();
	protected MyQuery strucQuery = new MyQuery();
	protected String mainType;
	protected String subType;
	protected Property img ;
	protected CachedImage<OUTPUT> imageWrapper;

	public AbstractImageReporter(String mainType,String subType) {
		this(mainType,subType,new Dimension(250,250));
	}
	public AbstractImageReporter(String mainType,String subType,Dimension dimension) {
		this.mainType = mainType;
		this.subType = subType;
		String mimeType = String.format("%s/%s",mainType,subType);
		img = Property.getInstance(mimeType,mimeType);
		strucQuery.setPageSize(1);
		strucQuery.setPage(0);
		
		strucQuery.setChemicalsOnly(false);
		depict.setImageSize(dimension);
		getProcessors().clear();
		RetrieveStructureImagePath q = new RetrieveStructureImagePath(mimeType);
		q.setQueryName(getQueryName());
		q.setPageSize(1);
		q.setPage(0);
		getProcessors().add(new ProcessorStructureRetrieval(q));
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
		setMaxRecords(1);
		
		cache = new CreateStoredQuery();
		SessionID queryCache = new SessionID();
		cache.setGroup(queryCache);		
		cache.setUser( new AmbitUser("admin"));		
		StoredQuery qs = new StoredQuery();
		qs.setQuery(strucQuery);
		cache.setObject(qs);		
	
		imageWrapper = createImageWrapper(null);
	}
	protected CachedImage<OUTPUT> createImageWrapper(BufferedImage image) {
		return new CachedImage<OUTPUT>(image, null);
	}
	protected String getQueryName() {
		 return String.format("w=%d&h=%d",depict.getImageSize().width,depict.getImageSize().height);
	}
	
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		cacheUpdater.setConnection(connection);
	}
	@Override
	public void close() throws SQLException {
		try {
		cacheUpdater.setConnection(null);
		} catch (Exception x) {};
		cacheUpdater.close();
		super.close();
	}	

	protected  synchronized void cache(IStructureRecord item, BufferedImage image) {
		if (image==null) return; //don't attempt to cache 
		try {
			String dimensions = getQueryName();
			String tmpDir = System.getProperty("java.io.tmpdir");
			
	        File file = new File(tmpDir,String.format("%s/%s/%d_%d.%s",
	        		getConnection().getCatalog(),
	        		dimensions,
	        		item.getIdchemical(),item.getIdstructure(),
	        		subType));
	        
        	//give up, perhaps another thread started writing it
        	if (file.exists()) return;

			File dir = file.getParentFile();
			//synchronized (this) {
				if ((dir!=null) && !dir.exists())	dir.mkdirs();		 
		        
				Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(subType);

				while (writers.hasNext()) {
					ImageWriter writer = (ImageWriter) writers.next();
					ImageOutputStream ios = ImageIO.createImageOutputStream(file);
					writer.setOutput(ios);
					writer.write(image);
					break;

				}
			//}
	        
	        strucQuery.setValue(item);
	        strucQuery.setText(file.getAbsolutePath());
	        strucQuery.setFieldname(dimensions);
	        String mimeType = String.format("%s/%s",mainType,subType);
	        strucQuery.setCategory(mimeType);

			cache.getGroup().setName(mimeType);
			cache.getObject().setName(dimensions);
			//
			cacheUpdater.process(cache);
			
	        
			if (depict.getImageMap()!=null) {
				//System.out.println(depict.getImageMap().toString());
				file = new File(tmpDir,String.format("%s/%s/%d_%d.map",
			        		getConnection().getCatalog(),
			        		dimensions,
			        		item.getIdchemical(),item.getIdstructure()
			        		));
				FileWriter writer = new FileWriter(file);
				try {
					writer.write(depict.getImageMap().toString());
				} finally {
					try {writer.close();} catch (Exception x) {}
				}
			}
			
		} catch (Throwable x) {
			x.printStackTrace();
		}

	}
	
	protected CachedImage<OUTPUT> createImage(IStructureRecord item) throws AmbitException  {
		imageWrapper.setImage(null);
		imageWrapper.setProperty(null);
		IAtomContainer ac = reader.process(item);
		try {
			switch (item.getType()) {
				case D2withH: {
					ac = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(ac);
					break;
				}
				case D3withH: {
					ac = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(ac);
					break;
				}
				case NA: { 
					return imageWrapper;
				}
			}
		} catch (Exception x) {}
		depict.setImageMap(new StringBuilder());
		imageWrapper.setImage(depict.getImage(ac));
		imageWrapper.setImageMap(depict.getImageMap().toString());
		return imageWrapper;
	}

	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			CachedImage<OUTPUT> result = getCached(item);
			if (result.getImage() == null) {
				result = createImage(item);
				if (result.getImage()!=null) {
					cache(item,result.getImage());
				}
			}
			if (result.getImage()==null) 
				result.setImage((BufferedImage) depict.getDefaultImage());
			setOutput(result.getProperty());
			return result;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	
	}
	protected abstract CachedImage<OUTPUT> getCached(IStructureRecord item);
}


class MyQuery extends AbstractStructureQuery<String,IStructureRecord,NumberCondition> { 
	/**
     * 
     */
    private static final long serialVersionUID = -2227075383236154179L;
    protected IStructureRecord maxValue = null;
	public static final String sql=
		"select idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,1 as metric,? text from sessions\n"+
		"join query using(idsessions)\n"+
		"join structure where title=? and name=? and idchemical=? and idstructure=?";
	
	protected long maxRecords = -1;
	protected String text = null;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	protected String category;

	public MyQuery() {
		setCondition(NumberCondition.getInstance("="));
	}
	
	public String getSQL() throws AmbitException {
		return sql;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getValue()==null) || (getCategory()==null)) throw new AmbitException("Undefined parameters");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getText()));
		params.add(new QueryParam<String>(String.class, getCategory()));
		params.add(new QueryParam<String>(String.class, getFieldname()));
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdchemical()));
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		return params;
	}

	@Override
	public String toString() {
		return getFieldname();
	}
	@Override
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

}

class CachedImage<MORE> {
	BufferedImage image;
	String imageMap = null;
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