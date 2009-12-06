package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;

/**
 * Selects a structure by idstructure
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStructureByID extends AbstractStructureQuery<String,IStructureRecord,NumberCondition> { 
	/**
     * 
     */
    private static final long serialVersionUID = -2227075383236154179L;
    protected IStructureRecord maxValue = null;
	public static final String sqlField="select ? as idquery,idchemical,idstructure,1 as selected,? as metric,? as text from structure where %s %s %s order by type_structure desc";
	protected long maxRecords = -1;
	protected int metric = 1;
	protected String text = null;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getMetric() {
		return metric;
	}
	public void setMetric(int metric) {
		this.metric = metric;
	}
	
	public QueryStructureByID() {
		setCondition(NumberCondition.getInstance("="));
	}
	@Override
	public long getMaxRecords() {
		return maxRecords;
	}
	@Override
	public void setMaxRecords(long records) {
		this.maxRecords = records;
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
		
		return String.format(sqlField, 
					isChemicalsOnly()?"idchemical":"idstructure",
					getCondition(),
					NumberCondition.between.equals(getCondition().getSQL())?" ? and ?":"?"
					  );
		
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<Integer>(Integer.class, getMetric()));
		params.add(new QueryParam<String>(String.class, getText()));

		params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getValue().getIdchemical():getValue().getIdstructure()));
		if (NumberCondition.between.equals(getCondition().getSQL())) 
			params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getMaxValue().getIdchemical():getMaxValue().getIdstructure()));
		return params;
	}

	@Override
	public String toString() {
		return isChemicalsOnly()?String.format("idcompound=%d",getValue().getIdchemical()):String.format("idconformer=%d",getValue().getIdstructure());
	}

}
