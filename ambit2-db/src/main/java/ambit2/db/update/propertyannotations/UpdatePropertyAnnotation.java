package ambit2.db.update.propertyannotations;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;

public class UpdatePropertyAnnotation extends AbstractUpdate<Property,PropertyAnnotation> {
	protected final String[] sql = {
			"UPDATE property_annotation set rdf_type=substring(?,45),predicate=?,object=? where idproperty=?"
	};
	@Override
	public String[] getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		param.add(new QueryParam<String>(String.class,getObject().getType()));
		param.add(new QueryParam<String>(String.class,getObject().getPredicate()));
		if (getObject().getObject() instanceof String)
			param.add(new QueryParam<String>(String.class,getObject().getObject().toString()));
		else throw new AmbitException(String.format("Found object of class %s instead of String. [%s]", 
						getObject().getObject().getClass(),getObject().getObject()));
		
		int idproperty =  getGroup()==null?getObject().getIdproperty():getGroup().getId();
		
		param.add(new QueryParam<Integer>(Integer.class,idproperty));		
		return param;
	}

	@Override
	public void setID(int index, int id) {
		
	}

}
