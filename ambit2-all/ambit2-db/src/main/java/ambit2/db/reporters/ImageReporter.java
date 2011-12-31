package ambit2.db.reporters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
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

/**
SELECT idchemical,idstructure,text,title FROM structure
left join query_results using(idchemical,idstructure)
left join `query` using(idquery)
left join sessions using(idsessions)
where
sessions.title="SMARTS" and
idchemical=460 and idstructure=100664
union
SELECT idchemical,idstructure,null,"SMARTS" FROM structure
where
idchemical=460 and idstructure=100664
 * @author nina
 *
 * @param <Q>
 */
public class ImageReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q,BufferedImage > {
	protected CompoundImageTools depict = new CompoundImageTools();
	protected MoleculeReader reader = new MoleculeReader();
	protected CreateStoredQuery cache ;
	protected UpdateExecutor<IQueryUpdate> cacheUpdater = new UpdateExecutor<IQueryUpdate>();
	protected MyQuery strucQuery = new MyQuery();
	protected Property img ;
	protected String mainType;
	protected String subType;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	
	public ImageReporter(String mainType,String subType) {
		this(mainType,subType,new Dimension(250,250));
	}
	public ImageReporter(String mainType,String subType,Dimension dimension) {
		super();
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
			
		} catch (Throwable x) {
			x.printStackTrace();
		}

	}
	protected String getQueryName() {
		 return String.format("w=%d&h=%d",depict.getImageSize().width,depict.getImageSize().height);
	}
	protected BufferedImage getCached(IStructureRecord item)  {
		try {
			Object path = item.getProperty(img);
			if (path != null) {
				File file = new File(path.toString());
				if (file.exists()) {
					return ImageIO.read(file);
				}
			} 
			return null;
		} catch (Exception x) {
			logger.warn(x);
			return null;
		}
	}
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			BufferedImage image = getCached(item);
			if (image == null) {
				image = createImage(item);
				cache(item,image);
			}
			if (image==null) 
				image = (BufferedImage) depict.getDefaultImage();
			setOutput(image);
			return image;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	
	}
	protected BufferedImage createImage(IStructureRecord item) throws AmbitException  {
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
					return null;
				}
			}
		} catch (Exception x) {}
		
		return depict.getImage(ac);
	}
	
	public void open() throws DbAmbitException {
	}
	@Override
	public void footer(BufferedImage output, Q query) {
		
	}
	@Override
	public void header(BufferedImage output, Q query) {
	}

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