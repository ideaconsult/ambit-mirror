package ambit2.db.facets.qlabel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

public class DatasetStrucTypeStats  extends AbstractFacetQuery<IStructureRecord.STRUC_TYPE,SourceDataset,StringCondition,DatasetStrucTypeFacet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5183010855788077541L;

	protected static final String sql = 
		"SELECT id_srcdataset,name,type_structure,count(idstructure),src_dataset.user_name FROM structure\n"+
		"join struc_dataset using(idstructure)\n"+
		"join src_dataset using(id_srcdataset)\n"+
		"%s\n"+
		"group by id_srcdataset,user_name,type_structure with rollup\n";
	
	protected DatasetStrucTypeFacet record;
	
	public DatasetStrucTypeStats(String url) {
		super(url);
		record = new DatasetStrucTypeFacet(url);
		record.setProperty(getFieldname());
		record.setDataset(getValue());		
	}
	@Override
	public double calculateMetric(DatasetStrucTypeFacet object) {
		return 1;
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
	protected DatasetStrucTypeFacet createFacet(String facetURL) {
		return new DatasetStrucTypeFacet(null);
	}
	@Override
	public DatasetStrucTypeFacet getObject(ResultSet rs) throws AmbitException {
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
			String label = rs.getString("type_structure");
			record.setValue(String.format("[%s] %s",
					dataset==null?"Total":dataset.getName(),label==null?"All":label));
			
			record.setCount(rs.getInt(4));
			String ts = rs.getString("type_structure");
			record.setProperty(null);
			for (IStructureRecord.STRUC_TYPE t : IStructureRecord.STRUC_TYPE.values())
				if (t.toString().equals(ts)) {
					record.setProperty(label==null?null:t);
					break;
				}
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
			return String.format("Struc type %s & Dataset %d",getFieldname(),getValue().getId());
		else return "Dataset structure types";
	}	
}