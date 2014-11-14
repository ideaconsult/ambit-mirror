package ambit2.db.update.storedquery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.StringCondition;

public class ReadStoredQuery extends  AbstractQuery<String, IStoredQuery, StringCondition, ISourceDataset> 
									implements IQueryRetrieval<ISourceDataset>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1485698692883271843L;
	public final static String sqlField = "select idquery,name,title,content from query join sessions using(idsessions)\n where %s order by name";
	
	public enum _query {
		idquery {
			@Override
			public String getSQL(ReadStoredQuery query) {
				return " idquery = ?";
			}
			@Override
			public boolean isEnabled(ReadStoredQuery q) {
				return (q.getValue()!=null) && (q.getValue().getId() != null) && (q.getValue().getId() >0 );
			}
			@Override
			public QueryParam getParam(ReadStoredQuery q) {
				if (isEnabled(q)) 
					return new QueryParam<Integer>(Integer.class, q.getValue().getId());
				else return null;
			}	
			@Override
			public void update(StoredQuery q,ResultSet rs) throws SQLException {
				q.setId(rs.getInt(ordinal()+1));
			}
		},
		name {
			@Override
			public String getSQL(ReadStoredQuery q) {
				return String.format(" name %s ?",q.getCondition());
			}
			@Override
			public boolean isEnabled(ReadStoredQuery q) {
				return (q.getValue()!=null) && (q.getValue().getName() != null);
			}
			@Override
			public QueryParam getParam(ReadStoredQuery q) {
				return new QueryParam<String>(String.class, q.getValue().getName());
			}	
			@Override
			public void update(StoredQuery q,ResultSet rs) throws SQLException {
				q.setName(rs.getString(ordinal()+1));
			}
		},
		title {
			@Override
			public String getSQL(ReadStoredQuery q) {
				return String.format(" title %s ?",q.getCondition());
			}
			@Override
			public boolean isEnabled(ReadStoredQuery q) {
				return true;
			}
			@Override
			public QueryParam getParam(ReadStoredQuery q) {
				String v = q.getFieldname()==null?"temp":q.getFieldname();
				return new QueryParam<String>(String.class, v);
			}
			@Override
			public void update(StoredQuery q,ResultSet rs) throws SQLException {
				
			}
		},
		content {
			@Override
			public QueryParam getParam(ReadStoredQuery arg0) {
				return null;
			}
			@Override
			public String getSQL(ReadStoredQuery arg0) {
				return null;
			}
			@Override
			public boolean isEnabled(ReadStoredQuery arg0) {
				return false;
			}
			@Override
			public void update(StoredQuery q,ResultSet rs) throws SQLException {
				q.setContent(rs.getString(ordinal()+1));
			}
		};
		public abstract String getSQL(ReadStoredQuery query);
		public abstract boolean isEnabled(ReadStoredQuery query);
		public abstract QueryParam getParam(ReadStoredQuery query);
		public abstract void update(StoredQuery query,ResultSet rs) throws SQLException;

	}
	public ReadStoredQuery() {
		super();
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
	public ReadStoredQuery(int idquery) {
		this();

		setFieldname("temp");
		StoredQuery q = new StoredQuery(); q.setName(null);
		q.setID(idquery);
		setValue(q);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (_query q: _query.values())  
			if (q.isEnabled(this)) {
				QueryParam p = q.getParam(this);
				if (p!= null) params.add(p);
			}
		if (params.size()==0) throw new AmbitException("No query or name defined!");
		else return params;
	}

	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String d = "";
		for (_query q: _query.values())  
			if (q.isEnabled(this)) {
				b.append(d);
				b.append(q.getSQL(this));
				d = " and ";
			}

		return String.format(sqlField,b);
	}

	public IStoredQuery getObject(ResultSet rs) throws AmbitException {
		try {
			StoredQuery query = new StoredQuery();
			
			for (_query q: _query.values())  
				q.update(query, rs);

			return query;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	public double calculateMetric(ISourceDataset object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}

}
