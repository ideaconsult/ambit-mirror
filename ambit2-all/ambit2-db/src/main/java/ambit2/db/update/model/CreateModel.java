package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;

public class CreateModel extends AbstractObjectUpdate<ModelQueryResults>{
	public static final String[] create_sql = {
		"INSERT INTO models (idmodel,name,idquery,predictors,dependent,content) SELECT null,?,?,t1.idtemplate,t2.idtemplate,? from template t1 join template t2 where t1.name=? and t2.name =?"
	};

	public CreateModel(ModelQueryResults ref) {
		super(ref);
	}
	public CreateModel() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<String>(String.class, getObject().getName()));
		if (getObject().getTrainingInstances()==null)
			params1.add(new QueryParam<Integer>(Integer.class, null));
		else
			params1.add(new QueryParam<Integer>(Integer.class, getObject().getTrainingInstances().getFieldname().getId()));
		params1.add(new QueryParam<String>(String.class, getObject().getContent()));
		params1.add(new QueryParam<String>(String.class, getObject().getPredictors().getName()));
		params1.add(new QueryParam<String>(String.class, getObject().getDependent().getName()));
		return params1;
		
	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	public void setID(int index, int id) {
		getObject().setId(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
