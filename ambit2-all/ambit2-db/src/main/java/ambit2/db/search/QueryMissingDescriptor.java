package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.LiteratureEntry;
import ambit2.core.exceptions.AmbitException;

/**
 * reads structures, for each there are no descriptors with given name and reference title.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryMissingDescriptor extends AbstractStructureQuery<LiteratureEntry,String,NumberCondition> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8610455060315105098L;
	public static String MISSING_DESCRIPTOR = 
        "select ? as idquery,idchemical,idstructure,1 as selected,1 as metrics from structure where idstructure not in (select idstructure from property_values join properties using(idproperty) join catalog_references using(idreference) where name=? and title=?)";
	public static String ALL = 
        "select ? as idquery,idchemical,idstructure,1 as selected,1 as metrics from structure";

	public String getSQL() throws AmbitException {
		if ((getValue()!=null) && (getFieldname()!=null)) 
			return MISSING_DESCRIPTOR;
		else
			return ALL;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if ((getValue()!=null) && (getFieldname()!=null)) {
			params.add(new QueryParam<String>(String.class, getValue()));		
			params.add(new QueryParam<String>(String.class, getFieldname().getTitle()));
		}
		return params;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Reads structures with missing values for descriptor ");
		if (getValue()!=null) b.append(getValue().toString());
		if (getFieldname()!=null) {
			b.append(" [");
			b.append(getFieldname().getTitle());
			b.append("]");
		}
		return b.toString();
	}
}
