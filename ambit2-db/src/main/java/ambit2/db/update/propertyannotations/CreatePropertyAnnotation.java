package ambit2.db.update.propertyannotations;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;

public class CreatePropertyAnnotation extends AbstractUpdate<Property,PropertyAnnotation> {
	protected final String[] sql = {
			"INSERT ignore into property_annotation (idproperty,rdf_type,predicate,object) values (?,?,?,?)"
	};
	protected final String[] sql_bypropertyname = {
			"INSERT ignore into property_annotation (idproperty,rdf_type,predicate,object) " +
			"SELECT idproperty,?,?,? FROM properties join catalog_references using(idreference) WHERE name=? and title=?"
	};	
	
	public CreatePropertyAnnotation() {
		super();
	}
	@Override
	public String[] getSQL() throws AmbitException {
		if (getObject().getIdproperty()>0)
			return sql;
		else if ((getGroup()!=null) && (getGroup().getId()>0))
			return sql;
		else { //by name
			if (getGroup()!=null)
				return sql_bypropertyname;
			else 
				 throw new AmbitException("No property defined!");
		}		
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		boolean byname = false;
		List<QueryParam> param = new ArrayList<QueryParam>();
		if (getObject().getIdproperty()>0)
			param.add(new QueryParam<Integer>(Integer.class,getObject().getIdproperty()));
		else if ((getGroup()!=null) && (getGroup().getId()>0))
			param.add(new QueryParam<Integer>(Integer.class,getGroup().getId()));
		else { //by name
			if (getGroup()==null) throw new AmbitException("No property defined!");
			else byname = true;
		}
		
		param.add(new QueryParam<String>(String.class,getObject().getType()));
		param.add(new QueryParam<String>(String.class,getObject().getPredicate()));
		if (getObject().getObject() instanceof String)
			param.add(new QueryParam<String>(String.class,getObject().getObject().toString()));
		else throw new AmbitException(String.format("Found object of class %s instead of String. [%s]", 
						getObject().getObject().getClass(),getObject().getObject()));
		
		if (byname) {
			param.add(new QueryParam<String>(String.class,getGroup().getName()));
			param.add(new QueryParam<String>(String.class,getGroup().getTitle()));
		}
		
		return param;
	}

	@Override
	public void setID(int index, int id) {
		
	}

}
