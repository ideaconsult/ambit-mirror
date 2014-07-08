package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.update.AbstractObjectUpdate;

/**
 * Updates content of the model
 * @author nina
 *
 */
public class UpdateModel extends AbstractObjectUpdate<ModelQueryResults>{

	public static final String update_sql = "update models set content=?,mediatype=? where %s";

	public UpdateModel(ModelQueryResults ref) {
		super(ref);
	}
	public UpdateModel() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getObject().getContent()));
		params.add(new QueryParam<String>(String.class, getObject().getContentMediaType()));
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
			return new String[] {String.format(update_sql,AbstractModelQuery._models_criteria.id.getSQL())};
		} else	if (getObject().getName()!= null) 
			return new String[] {String.format(update_sql,AbstractModelQuery._models_criteria.name.getSQL())};
		throw new AmbitException("no model specified");
	}
	public void setID(int index, int id) {
			
	}
}