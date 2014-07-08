package ambit2.db.facets.datasets;

import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;

/**
 * getFieldname - prefix length
 * getValue - first letters of the prefix (could be null)
 * @author nina
 *
 */
public class DatasetByPrefixNameFacetQuery extends PrefixFacetQuery<IStructureRecord,DatasetPrefixFacet>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2156155776273258064L;
	protected static final String sql = 
		"select substring(name,1,?) as prefix, count(*) from src_dataset\n" +
		"%s\n"+
		"%s\n"+
		"group by prefix order by prefix";

	protected static final String sql_structure = 
		"select substring(name,1,?) as prefix, count(*) from src_dataset\n" +
		"join struc_dataset using(id_srcdataset)\n" +
		"where \n" +
		"%s\n"+
		"%s\n"+
		"idstructure=?\n"+
		"group by prefix order by prefix";
	
	protected static final String sql_compound = 
		"select substring(name,1,?) as prefix, count(*) from src_dataset\n" +
		"join struc_dataset using(id_srcdataset)\n" +
		"join structure using(idstructure)\n" +
		"where \n" +
		"%s\n"+
		"%s\n"+
		"idchemical=?\n"+
		"group by prefix order by prefix";
	
	public DatasetByPrefixNameFacetQuery(String url) {
		super(url);
	}	
	
	//protected PropertyDatasetFacet<Property,SourceDataset> record;
	@Override
	protected DatasetPrefixFacet createRecord(String url) {
		//record.setProperty(getFieldname());
		//record.setDataset(getValue());
		return createFacet(url);
	}
	
	@Override
	protected DatasetPrefixFacet createFacet(String facetURL) {
		DatasetPrefixFacet record = new DatasetPrefixFacet(facetURL);
		record.setStructure(getFieldname());
		return record;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> param = super.getParameters();
		if (noStructure()) return param;
		if (getFieldname().getIdstructure()!=-1) {
			param.add(new QueryParam<Integer>(Integer.class,getFieldname().getIdstructure()));
		} else if (getFieldname().getIdchemical()!=-1) {
			param.add(new QueryParam<Integer>(Integer.class,getFieldname().getIdchemical()));

		}   
		return param;		
			
	}
	protected boolean noStructure() {
		return getFieldname()==null || ((getFieldname().getIdchemical()==-1)  && (getFieldname().getIdstructure()==-1));
	}
	@Override
	public String getSQL() throws AmbitException {
		if (!noStructure()) {
			if (getFieldname().getIdstructure()!=-1) 
				return String.format(sql_structure,getValue()==null?"":where_name,getValue()==null?"":"and");
			else if (getFieldname().getIdchemical()!=-1) 
				return String.format(sql_compound,getValue()==null?"":where_name,getValue()==null?"":"and");
		}
		return String.format(sql,getValue()==null?"":"where",getValue()==null?"":where_name);
	}		
	@Override
	public void setFieldname(IStructureRecord fieldname) {
		super.setFieldname(fieldname);
		try {
		if (record != null)
			record.setStructure(getFieldname());
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	}
}
