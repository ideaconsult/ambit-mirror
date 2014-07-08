package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.EQCondition;

public class MissingInChIsQuery extends AbstractStructureQuery<String, String, EQCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7898392932554642629L;
	public final static String sql =

		"select ? as idquery, chemicals.idchemical,-1,1 as selected,1 as metric,null as text\n"+
		"from chemicals where label = ?\n";
	
	/**
	 * TODO identify potential errors
update chemicals,(SELECT inchi,count(*) c  FROM chemicals where inchi is not null group by inchi ) a set label='ERROR' where chemicals.inchi=a.inchi 	and a.c>1;
update chemicals set label="ERROR" where inchi is null;
	 */
	
	public MissingInChIsQuery(String label) {
		super();
		setCondition(EQCondition.getInstance());
		setFieldname(label);
	}	
	public MissingInChIsQuery() {
		this("ERROR");
	}
	public String getSQL() throws AmbitException {
		return sql;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getFieldname()));
		return params;
	}
	@Override
	public String toString() {
		return "Compounds with missing InChI string";
	}

}
