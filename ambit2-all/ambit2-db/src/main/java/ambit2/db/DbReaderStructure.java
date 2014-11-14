package ambit2.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.AmbitUser;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.cache.QueryCachedResultsBoolean;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.update.storedquery.CreateStoredQuery;

/**
 * Reads structures given a query.
 * @author nina
 *
 */
public class DbReaderStructure extends DbReader<IStructureRecord> {
	protected CreateStoredQuery cache ;
	protected UpdateExecutor<IQueryUpdate> cacheUpdater = new UpdateExecutor<IQueryUpdate>();
	protected MyQuery strucQuery = new MyQuery();
	protected QueryCachedResultsBoolean hits = new QueryCachedResultsBoolean();
	protected QueryExecutor<IQueryObject> lookup = new QueryExecutor<IQueryObject>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 7593295052807885237L;
	protected ProcessorStructureRetrieval retriever;
	
	public DbReaderStructure() {
		this(false);
	}
	public DbReaderStructure(boolean preffered) {
		super();
		RetrieveStructure q = new RetrieveStructure(preffered);
		q.setPageSize(1);
		q.setPage(0);
		retriever = new ProcessorStructureRetrieval(q);
		

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
		retriever.setConnection(connection);
		cacheUpdater.setConnection(connection);
		lookup.setConnection(connection);
		lookup.setCache(true);
	}
	@Override
	public void close() throws Exception {
		retriever.close();
		cacheUpdater.close();
		lookup.close();
		super.close();
	}
	@Override
	protected boolean prescreen(IQueryRetrieval<IStructureRecord> query,
			IStructureRecord object) throws AmbitException {
		if ((object.getIdstructure()<=0) && (retriever.getQuery() instanceof RetrieveStructure)) {
			((RetrieveStructure) retriever.getQuery()).setPreferedStructure(true);
			retriever.getQuery().setPage(0);
			retriever.getQuery().setPageSize(1);
		}
		//looks like the metric is lost after retrieval!
		Property metric = null;
		Object metricValue = null;
		for (Property property : object.getProperties())
			if ("metric".equals(property.getName())) {
				metric = property; 
				metricValue = object.getProperty(metric);
				break;
			}

		IStructureRecord record = retriever.process(object);
		object.setIdchemical(record.getIdchemical());
		object.setIdstructure(record.getIdstructure());
		object.setContent(record.getContent());
		object.setFormat(record.getFormat());
		if ((metric !=null) && (metricValue!=null)) object.setProperty(metric,metricValue);
		return super.prescreen(query, record);
	}
	@Override
	protected ambit2.db.DbReader.cached_results getCached(String category,
			String key, IStructureRecord object) {
		ResultSet rs = null;
		try {
			if ((key==null) || (key.length()>255)) return cached_results.NOTCACHED;
			hits.setCategory(category);
			hits.setValue(object);
			hits.setFieldname(key);
			rs = lookup.process(hits);
			while (rs.next()) {
				return hits.getObject(rs)?cached_results.TRUE:cached_results.FALSE;
			}
			return cached_results.NOTCACHED;
		} catch (Exception x) {
			return cached_results.NOTCACHED;
		} finally {
			try { lookup.closeResults(rs); } catch (Exception x) {}
		}
	}
	@Override
	protected void cache(String category, String key,
			IStructureRecord object, boolean ok) {
		try {
			if ((category!=null) && (key!=null) && (key.length()<=255)) {
				strucQuery.setMetric(ok?1:0);
				strucQuery.setValue(object);
				strucQuery.setFieldname(key);
				strucQuery.setCategory(category);
				cache.getGroup().setName(category);
				cache.getObject().setName(key);
				cacheUpdater.process(cache);
			}
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		super.cache(category,key, object, ok);
	}
}

class MyQuery extends AbstractStructureQuery<String,IStructureRecord,NumberCondition> { 
	/**
     * 
     */
    private static final long serialVersionUID = -2227075383236154179L;
    protected IStructureRecord maxValue = null;
	public static final String sql=
		"select idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,? as metric,null as text from sessions\n"+
		"join query using(idsessions)\n"+
		"join structure where title=? and name=? and idchemical=?";
	
	protected long maxRecords = -1;
	protected int metric = 0;
	protected String category;
	public int getMetric() {
		return metric;
	}
	public void setMetric(int metric) {
		this.metric = metric;
	}
	public MyQuery() {
		setCondition(NumberCondition.getInstance("="));
	}


	
	public String getSQL() throws AmbitException {
		
		return sql;
		
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getValue()==null) || (getCategory()==null)) throw new AmbitException("Undefined parameters");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getMetric()));
		params.add(new QueryParam<String>(String.class, getCategory()));
		params.add(new QueryParam<String>(String.class, getFieldname()));
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdchemical()));
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
