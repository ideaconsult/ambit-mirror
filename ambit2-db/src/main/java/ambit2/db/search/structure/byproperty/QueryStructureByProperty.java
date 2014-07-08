package ambit2.db.search.structure.byproperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.db.search.SetCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

public class QueryStructureByProperty<Q extends AbstractStructureQuery>  extends AbstractStructureQuery<Profile,Q, SetCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4056680895955934165L;
	protected static String sql_all =
		"select ? as idquery,s.idchemical,s.idstructure,1 as selected,preference as metric,type_structure as text from structure s\n"+
		"where s.idstructure %s\n"+
		"(select idstructure from property_values join structure using(idstructure)\n"+
		"where idproperty in (%s))\n"+
		"and type_structure != 'NA'\n";
	

	protected void addStructureParam(List<QueryParam> params)
			throws AmbitException {
	}

	protected String getSQLStructureQuery() throws AmbitException {
		return "";
	}

	protected String getSQLTemplate() throws AmbitException {
		return sql_all;
	}	
	public QueryStructureByProperty() {
		super();
		setCondition(new SetCondition(SetCondition.conditions.in));
	}
	public String getSQL() throws AmbitException {
		return String.format(getSQLTemplate(),getCondition().getSQL(),getSQLPropertyQuery(),getSQLStructureQuery());
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname() == null) throw new AmbitException("Properties not defined!");
		
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		addStructureParam(params);
		return params;
	}
	
	protected String getSQLPropertyQuery() throws AmbitException {
		StringBuilder b = new StringBuilder();
		Iterator<Property> i = getFieldname().getProperties(true);
		int c = 0;
		while (i.hasNext()) {
			Property p = i.next();
			b.append(String.format("%s%d",(c==0?"":","),p.getId()));
			c++;
		}
		if (c==0) throw new AmbitException("No properties!");
		return b.toString();
	}
	@Override
	public String toString() {
		return "Filtered dataset";
	}
	
}
