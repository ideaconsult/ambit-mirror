package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.core.exceptions.AmbitException;

/**
 * Selects a structure by idstructure
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStructureByID extends AbstractQuery<String,IStructureRecord,NumberCondition,IStructureRecord> {
	/**
     * 
     */
    private static final long serialVersionUID = -2227075383236154179L;
    protected IStructureRecord maxValue = null;
	public static final String sqlField="select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure ";
	public QueryStructureByID() {
		setCondition(NumberCondition.getInstance("="));
	}
	public IStructureRecord getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(IStructureRecord maxValue) {
		this.maxValue = maxValue;
	}
	public QueryStructureByID(IStructureRecord record) {
		this();
		setValue(record);
	}
	public QueryStructureByID(int idstructure) {
		this();
		setValue(new StructureRecord(-1,idstructure,null,null));
	}	
	public QueryStructureByID(int idstructure1,int idstructure2) {
		this();
		setValue(new StructureRecord(-1,idstructure1,null,null));
		setMaxValue(new StructureRecord(-1,idstructure2,null,null));
		setCondition(NumberCondition.getInstance(NumberCondition.between));
	}		
	public String getSQL() throws AmbitException {
		if (NumberCondition.between.equals(getCondition().getSQL())) {
			return sqlField + getCondition() + " ? and ?";
		}
		else return sqlField + getCondition() + " ?";
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));

		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		if (NumberCondition.between.equals(getCondition().getSQL())) 
			params.add(new QueryParam<Integer>(Integer.class, getMaxValue().getIdstructure()));
		return params;
	}

}
