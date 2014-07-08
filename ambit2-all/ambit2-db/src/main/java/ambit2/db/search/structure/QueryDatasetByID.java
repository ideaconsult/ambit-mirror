package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;
import ambit2.db.search.NumberCondition;

public class QueryDatasetByID extends AbstractStructureQuery<String,Integer,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sql = 
		"select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,1 as metric,null as text from structure join struc_dataset using(idstructure) where id_srcdataset %s %s";
	public QueryDatasetByID(SourceDataset dataset) {
		this(dataset==null?null:dataset.getId());
	}
	public QueryDatasetByID() {
		this((SourceDataset)null);
	}
	public QueryDatasetByID(Integer id) {
		super();
		setValue(id);
		setCondition(NumberCondition.getInstance("="));
	}	
	public String getSQL() throws AmbitException {
		return 	String.format(sql,getCondition().getSQL(),getValue()==null?"":"?");
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class,getValue()));
		return params;
	}
	@Override
	public String toString() {
		if (getValue()==null) return "Datasets";
		return String.format("Dataset %s %s",getCondition().toString(),getValue()==null?"":getValue());
	}

}