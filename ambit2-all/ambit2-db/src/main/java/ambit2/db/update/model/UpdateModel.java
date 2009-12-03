package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;

/**
 * Updates content of the model
 * @author nina
 *
 */
public class UpdateModel extends AbstractObjectUpdate<ModelQueryResults>{

	public static final String update_sql = "update models set content=? where %s";

	public UpdateModel(ModelQueryResults ref) {
		super(ref);
	}
	public UpdateModel() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getObject().getContent()));
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
			return new String[] {String.format(update_sql,ReadModel.whereID)};
		} else	if (getObject().getName()!= null) 
			return new String[] {String.format(update_sql,ReadModel.whereName)};
		throw new AmbitException("no model specified");
	}
	public void setID(int index, int id) {
			
	}
}