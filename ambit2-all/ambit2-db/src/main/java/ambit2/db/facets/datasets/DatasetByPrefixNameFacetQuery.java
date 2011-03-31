package ambit2.db.facets.datasets;

import ambit2.base.exceptions.AmbitException;

/**
 * getFieldname - prefix length
 * getValue - first letters of the prefix (could be null)
 * @author nina
 *
 */
public class DatasetByPrefixNameFacetQuery extends PrefixFacetQuery<DatasetPrefixFacet>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2156155776273258064L;
	protected static final String sql = 
		"select substring(name,1,?) as prefix, count(*) from src_dataset\n" +
		"%s\n"+
		"group by prefix order by prefix";

	public DatasetByPrefixNameFacetQuery(String url) {
		super(url);
	}	
	
	//protected PropertyDatasetFacet<Property,SourceDataset> record;
	@Override
	protected DatasetPrefixFacet createRecord(String url) {
		//record.setProperty(getFieldname());
		//record.setDataset(getValue());
		return new DatasetPrefixFacet(url);
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
		int minlength = 1;
		if (value!=null) minlength = getValue().length()+1;
		if ((getFieldname()==null) || (getFieldname()<minlength))
			setFieldname(minlength);
	}

	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql,(getValue()==null)?"":where);
	}		
	
}
