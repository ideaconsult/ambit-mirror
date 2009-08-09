package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;

public class DeleteModel extends AbstractObjectUpdate<ModelQueryResults> {

	public static final String[] delete_sql = {"delete from models where idmodel=?"};

	public DeleteModel(ModelQueryResults ref) {
		super(ref);
	}
	public DeleteModel() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		return delete_sql;
	}
	public void setID(int index, int id) {
			
	}
}