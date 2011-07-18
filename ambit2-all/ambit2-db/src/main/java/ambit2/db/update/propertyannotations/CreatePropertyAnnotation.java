package ambit2.db.update.propertyannotations;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class CreatePropertyAnnotation extends AbstractUpdate<Property,PropertyAnnotation> {
	protected final String[] sql = {
			"INSERT into property_annotation (idproperty,rdf_type,predicate,object) values (?,?,?,?)"
	};
	
	public CreatePropertyAnnotation() {
		super();
	}
	@Override
	public String[] getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		if (getObject().getIdproperty()>0)
			param.add(new QueryParam<Integer>(Integer.class,getObject().getIdproperty()));
		else if ((getGroup()!=null) && (getGroup().getId()>0))
			param.add(new QueryParam<Integer>(Integer.class,getGroup().getId()));
		else throw new AmbitException("No property assigned!");
		
		param.add(new QueryParam<String>(String.class,getObject().getType()));
		param.add(new QueryParam<String>(String.class,getObject().getPredicate()));
		if (getObject().getObject() instanceof String)
			param.add(new QueryParam<String>(String.class,getObject().getObject().toString()));
		else throw new AmbitException(String.format("Found object of class %s instead of String. [%s]", 
						getObject().getObject().getClass(),getObject().getObject()));
		return param;
	}

	@Override
	public void setID(int index, int id) {
		
	}

}
