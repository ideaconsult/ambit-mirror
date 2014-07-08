package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.update.AbstractObjectUpdate;

public class DeleteModel extends AbstractObjectUpdate<ModelQueryResults> {

	public static final String delete_sql = "delete from models where %s and user_name=(SUBSTRING_INDEX(user(),'@',1))";

	public DeleteModel(ModelQueryResults ref) {
		super(ref);
	}
	public DeleteModel() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getObject().getId() != null) 
			params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		else
		if (getObject().getName()!=null) 
			params.add(new QueryParam<String>(String.class, getObject().getName()));
		else
			throw new AmbitException("no model specified");
		return params.size()==0?null:params;		
		
	}

	public String[] getSQL() throws AmbitException {
		if (getObject().getId()!= null) {
			return new String[] {String.format(delete_sql,AbstractModelQuery._models_criteria.id.getSQL())};
		} else	if (getObject().getName()!= null) 
			return new String[] {String.format(delete_sql,AbstractModelQuery._models_criteria.name.getSQL())};
		throw new AmbitException("no model specified");
	}
	public void setID(int index, int id) {
			
	}
}