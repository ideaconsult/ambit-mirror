package ambit2.db.update.propertyannotations;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class CreatePropertyAnnotations  extends AbstractUpdate<Property,PropertyAnnotations> {
	protected String sql_values = "(?,?,?,?)";
	protected final String sql = 
			"INSERT ignore into property_annotation (idproperty,rdf_type,predicate,object) values %s";

	
	public CreatePropertyAnnotations() {
		super();
	}
	@Override
	public String[] getSQL() throws AmbitException {
		if ((getObject()==null) && (getObject().size()==0)) throw new AmbitException("No annotations!");
		StringBuilder b = new StringBuilder();
		String delimiter = "";
		for (PropertyAnnotation pa : getObject()) {
			b.append(delimiter);
			b.append(sql_values);
			delimiter = ",";
		}
		return new String[] {String.format(sql,b.toString())};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getObject()==null) && (getObject().size()==0)) throw new AmbitException("No annotations!");
		
		List<QueryParam> param = new ArrayList<QueryParam>();

		for (PropertyAnnotation pa : getObject()) {
			if (pa.getIdproperty()>0)
				param.add(new QueryParam<Integer>(Integer.class,pa.getIdproperty()));
			else if ((getGroup()!=null) && (getGroup().getId()>0))
				param.add(new QueryParam<Integer>(Integer.class,getGroup().getId()));
			else throw new AmbitException("No property assigned!");
			param.add(new QueryParam<String>(String.class,pa.getType()));
			param.add(new QueryParam<String>(String.class,pa.getPredicate()));
			param.add(new QueryParam<String>(String.class,pa.getObject()));
		}

		return param;
	}

	@Override
	public void setID(int index, int id) {
		
	}

}
