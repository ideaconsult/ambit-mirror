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
public class QueryMissingDescriptor extends AbstractQuery<LiteratureEntry,String,NumberCondition> {
    public static String MISSING_DESCRIPTOR = 
        "select ? as idquery,idchemical,idstructure,1 as selected,1 as metrics from structure where idstructure not in (select idstructure from property_values join properties using(idproperty) join catalog_references using(idreference) where name=? and title=?)";

	public String getSQL() throws AmbitException {
		return MISSING_DESCRIPTOR;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getValue()));		
		params.add(new QueryParam<String>(String.class, getFieldname().getTitle()));
		return params;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Reads structures with missing values for descriptor " + getValue() + " [" + getFieldname().getTitle() + "]";
	}
}
