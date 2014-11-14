package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.search.StringCondition;

/**
 * ModelQueryResults
 * @author nina
 *
 */
public class ReadModel  extends AbstractModelQuery<String, Integer>  implements IQueryRetrieval<ModelQueryResults> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8595708937489941431L;

	public ReadModel(Integer id) {
		super();
		setValue(id);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
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
			b.append(String.format(_models_criteria.name.getSQL(),getCondition()));
			where = " or ";
		}
		if (getValue()!= null) {
			b.append(where);
			b.append(_models_criteria.id.getSQL());
			where = " or ";
		}
		return String.format(sql,b.toString());
	}

	
	@Override
	public String toString() {
		return getValue()==null?"All models":String.format("Model id=%d",getValue());
	}
}
