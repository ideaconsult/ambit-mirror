package ambit2.db.update.property;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;
import ambit2.db.update.reference.CreateReference;

public class CreatePropertyReferenceID  extends AbstractObjectUpdate<Property> {
	public static final String create_sql = 
		"INSERT IGNORE INTO properties (idproperty,idreference,name,units,comments,islocal) SELECT null,idreference,?,?,?,? from catalog_references where idreference=?"
	;

	public CreatePropertyReferenceID(Property property) {
		super(property);
	}
	public CreatePropertyReferenceID() {
		this(null);
	}		
	@Override
	public boolean returnKeys(int index) {
		return true;
	}

	public List<QueryParam> getParameters(int index) throws AmbitException {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class, getObject().getName()));
			params1.add(new QueryParam<String>(String.class, getObject().getUnits()));
			params1.add(new QueryParam<String>(String.class, getObject().getLabel()));
			params1.add(new QueryParam<Boolean>(Boolean.class, false));
			params1.add(new QueryParam<Integer>(Integer.class, getObject().getReference().getId()));
			return params1;
	}

	public String[] getSQL() throws AmbitException {
		return new String[] {create_sql};
	}
	public void setID(int index, int id) {
		try {
			getObject().setId(id);
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	}

}