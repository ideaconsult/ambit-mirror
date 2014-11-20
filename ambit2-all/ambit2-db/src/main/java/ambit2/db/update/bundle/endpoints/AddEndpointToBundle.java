package ambit2.db.update.bundle.endpoints;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceProperty;

public class AddEndpointToBundle extends AbstractUpdate<SubstanceEndpointsBundle,SubstanceProperty> {
	private static final String[] update_sql =  {"insert into bundle_endpoints values(?,?,?,?,now()) on duplicate key update created=now()"	};
	
	public AddEndpointToBundle(SubstanceEndpointsBundle bundle,SubstanceProperty property) {
		this(property);
		setGroup(bundle);
	}
	
	public AddEndpointToBundle(SubstanceProperty protocol) {
		super(protocol);
	}
	public AddEndpointToBundle() {
		this(null);
	}	
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		
		if (getGroup()==null || getGroup().getID()<=0) throw new AmbitException("Bundle not defined");
		if (getObject() == null || getObject().getTopcategory() == null || getObject().getEndpointcategory() == null)  throw new AmbitException("Endpoint not defined");
		
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		
		
		params.add(new QueryParam<String>(String.class, truncate(getObject().getTopcategory(),32)));
		params.add(new QueryParam<String>(String.class, truncate(getObject().getEndpointcategory(),45)));
		if (getObject().getIdentifier()==null)
			params.add(new QueryParam<String>(String.class, ""));
		else
			params.add(new QueryParam<String>(String.class, truncate(getObject().getIdentifier(),255)));
		return params;
		
	}
	@Override
	public String[] getSQL() throws AmbitException {
		
		return update_sql;
	}
	public void setID(int index, int id) {
		
	}
}