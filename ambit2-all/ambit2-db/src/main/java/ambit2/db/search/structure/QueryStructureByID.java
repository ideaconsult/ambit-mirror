package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.NumberCondition;

/**
 * Selects a structure by idstructure
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStructureByID extends AbstractStructureQuery<ISourceDataset,IStructureRecord,NumberCondition> { 
	/**
     * 
     */
    private static final long serialVersionUID = -2227075383236154179L;
    protected IStructureRecord maxValue = null;
	public static final String sqlField="select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,? as metric,? as text from structure where %s %s %s order by idchemical,preference,idstructure";
	public static final String sqlDataset=
		"select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,? as metric,? as text from structure\n" +
		"join struc_dataset using(idstructure) where %s %s %s and id_srcdataset=? order by idchemical,preference,idstructure";
	public static final String sqlQuery=
		"select ? as idquery,structure.idchemical,idstructure,if(type_structure='NA',0,1) as selected,? as metric,? as text from structure\n" +
		"join query_results q using(idstructure) where %s %s %s and q.idquery=? order by structure.idchemical,preference,idstructure";
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
		super();
		setFieldname(null);
		setCondition(NumberCondition.getInstance("="));
		setPageSize(1);
		setPage(0);
	}
	@Override
	public long getPageSize() {
		return maxRecords;
	}
	@Override
	public void setPageSize(long records) {
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
		String sql = sqlField;
		if ((getFieldname()!=null) && (getFieldname().getID()>0)) {
			if (getFieldname() instanceof SourceDataset) sql = sqlDataset;
			else sql = sqlQuery;
		}
		return String.format(sql, 
					isChemicalsOnly()?"structure.idchemical":"idstructure",
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
		
		if ((getFieldname()!=null) && (getFieldname().getID()>0))
			params.add(new QueryParam<Integer>(Integer.class,getFieldname().getID()));
		return params;
	}

	@Override
	public String toString() {
		return isChemicalsOnly()?String.format("idcompound=%d",getValue().getIdchemical()):String.format("idconformer=%d",getValue().getIdstructure());
	}

	@Override
	protected boolean isPreferredStructure() {
		return isChemicalsOnly();
	}
}
