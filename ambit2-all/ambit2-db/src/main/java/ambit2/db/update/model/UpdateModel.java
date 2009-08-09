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

	public static final String[] update_sql = {"update models set content=? where idmodel=?"};

	public UpdateModel(ModelQueryResults ref) {
		super(ref);
	}
	public UpdateModel() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getObject().getContent()));
		params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		return update_sql;
	}
	public void setID(int index, int id) {
			
	}
}