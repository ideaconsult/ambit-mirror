package ambit2.db.update.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.jaxen.function.StringFunction;

import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryStoredResults;

/**
 * ModelQueryResults
 * @author nina
 *
 */
public class ReadModel  extends AbstractQuery<String, Integer, StringCondition, ModelQueryResults>  implements IQueryRetrieval<ModelQueryResults> {
	protected static String sql = 
		"select idmodel,m.name,idquery,t1.idtemplate,t1.name,t2.idtemplate,t2.name,content\n"+
		"from models m join template t2 on t2.idtemplate=m.dependent left join template t1 on t1.idtemplate = m.predictors %s";
	protected static String whereID = " idmodel = ? ";
	protected static String whereName = " m.name %s substr(?,1,45)";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8595708937489941431L;

	public ReadModel(Integer id) {
		super();
		setValue(id);
		setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
	}
	public ReadModel() {
		this(null);
	}
		
	public double calculateMetric(ModelQueryResults object) {

		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname() != null) 
			params.add(new QueryParam<String>(String.class, getFieldname()));
		
		if (getValue()!=null) 
			params.add(new QueryParam<Integer>(Integer.class, getValue()));

		return params.size()==0?null:params;
	}

	public String getSQL() throws AmbitException {
		String where = "where";
		StringBuilder b = new StringBuilder();
		
		if (getFieldname()!= null) {
			b.append(where);
			b.append(String.format(whereName,getCondition()));
			where = " or ";
		}
		if (getValue()!= null) {
			b.append(where);
			b.append(whereID);
			where = " or ";
		}
		return String.format(sql,b.toString());
	}

	public ModelQueryResults getObject(ResultSet rs) throws AmbitException {
		try {
			ModelQueryResults q = new ModelQueryResults();
			q.setId(rs.getInt(1));
			q.setName(rs.getString(2));
			if (rs.getObject(3)!=null)
				q.setTrainingInstances(new QueryStoredResults(new StoredQuery(rs.getInt(3))));
			else q.setTrainingInstances(null);
			Object idtemplate = rs.getObject(4);
			if (idtemplate==null) q.setPredictors(null);
			else {
				Template t = new Template(rs.getString(5));
				t.setId(rs.getInt(4));
				q.setPredictors(t);
			}
			idtemplate = rs.getObject(6);
			if (idtemplate==null) q.setDependent(null);
			else {
				Template t = new Template(rs.getString(7));
				t.setId(rs.getInt(6));
				q.setDependent(t);
			}		
			q.setContent(rs.getString(8));
			return q;
		} catch (Exception x) {
			return null;
		}
	}
	@Override
	public String toString() {
		return getValue()==null?"All models":String.format("Model id=%d",getValue());
	}
}
