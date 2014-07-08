package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ILiteratureEntry;
import ambit2.db.search.BooleanCondition;

/**
 * reads structures, for each there are / or there are not (depending on getCondition) descriptors with given idname and reference title.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryMissingDescriptor extends AbstractStructureQuery<ILiteratureEntry,String,BooleanCondition> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8610455060315105098L;
	public static String MISSING_DESCRIPTOR = 
        "select ? as idquery,idchemical,idstructure,1 as selected,1 as metric,null as text from structure where (type_structure != 'NA') and idstructure %s in " +
        "(select idstructure from property_values join properties using(idproperty) join catalog_references using(idreference) where %s %s %s)";
	public static String ALL = 
        "select ? as idquery,idchemical,idstructure,1 as selected,1 as metric,null as text from structure where (type_structure != 'NA')";
	public static String WHERE_NAME = "name=?";
	public static String WHERE_TITLE = "title=?";

	public QueryMissingDescriptor() {
		setCondition(BooleanCondition.getInstance(BooleanCondition.BOOLEAN_CONDITION.B_NO.toString()));
	}
	public String getSQL() throws AmbitException {

		if ((getValue()!=null) || (getFieldname()!=null))  {
			String wname = (getValue()!=null)?WHERE_NAME:"";
			String wtitle = (getFieldname()!=null)?WHERE_TITLE:"";
			String wand = ((getValue()!=null)&&(getFieldname()!=null))?"and":"";			
			return String.format(MISSING_DESCRIPTOR,
					getCondition().equals(BooleanCondition.BOOLEAN_CONDITION.B_YES)?"":"not",
					wname,wand,wtitle);
		} else
			return ALL;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if (getValue()!=null) {
			params.add(new QueryParam<String>(String.class, getValue()));		
		}
		if (getFieldname()!=null) {
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
	@Override
	public long getPageSize() {
		return 0;
	}
	@Override
	public void setPageSize(long records) {
	
	}
}
