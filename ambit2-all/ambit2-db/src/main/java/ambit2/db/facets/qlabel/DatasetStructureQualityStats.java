package ambit2.db.facets.qlabel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.data.SourceDataset;
import ambit2.db.search.StringCondition;

public class DatasetStructureQualityStats extends AbstractFacetQuery<QLabel,SourceDataset,StringCondition,DatasetStructureQLabelFacet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5183010855788077541L;

	protected static final String sql = 
		"SELECT id_srcdataset,name,label,count(idstructure),quality_structure.user_name FROM quality_structure\n"+
		"join struc_dataset using(idstructure)\n"+
		"join src_dataset using(id_srcdataset)\n"+
		"%s\n"+
		"group by id_srcdataset,user_name,label with rollup\n";
	
	protected DatasetStructureQLabelFacet record;
	
	public DatasetStructureQualityStats(String url) {
		super(url);
		record = new DatasetStructureQLabelFacet(url);
		record.setProperty(getFieldname());
		record.setDataset(getValue());		
	}
	@Override
	public double calculateMetric(DatasetStructureQLabelFacet object) {
		return 0;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((getValue() !=null) && getValue().getId()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getId()));
		
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		if ((getValue() !=null) && getValue().getId()>0) return String.format(sql,"where id_srcdataset=?");
		else return String.format(sql,"");
	}

	@Override
	protected DatasetStructureQLabelFacet createFacet(String facetURL) {
		return new DatasetStructureQLabelFacet(null);
	}
	@Override
	public DatasetStructureQLabelFacet getObject(ResultSet rs) throws AmbitException {
		if (record == null) 
			record = createFacet(null);
		
		try {
			SourceDataset dataset = null;
			Object iddataset = rs.getObject("id_srcdataset");
			String dbname = rs.getString("name");
			if (iddataset!=null) {
				dataset = new SourceDataset(dbname);
				dataset.setId(rs.getInt("id_srcdataset"));
			}
			String uname = rs.getString("user_name");
			record.setDataset(dataset);
			String label = rs.getString("label");
			record.setValue(String.format("[%s] %s: %s",
					dataset==null?"Total":dataset.getName(),
							"comparison".equals(uname)?uname:uname==null?"":"Expert", 
							label==null?"All":label));
			
			record.setCount(rs.getInt(4));
			record.setProperty(label==null?null:new QLabel(QUALITY.valueOf(rs.getString("label"))));
			
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			return record;
		}
	}	
	
	@Override
	public String toString() {
		if ((getFieldname()!=null) && (getValue()!=null))
			return String.format("QLabel %s & Dataset %d",getFieldname(),getValue().getId());
		else return "Dataset structure quality labels";
	}	
}
