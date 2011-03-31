package ambit2.db.facets.datasets;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.AbstractFacet;
import ambit2.base.facet.IFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.facets.AbstractFacetQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public abstract  class PrefixFacetQuery<PARAM,FACET extends AbstractFacet<String>>  extends AbstractFacetQuery<PARAM,String,StringCondition,IFacet<String>>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -360487682677697165L;
	protected static final String where_name = " name regexp ?"; 
	protected FACET record; 
	
	public PrefixFacetQuery(String url) {
		super(url);
		setValue(null);
		record = createRecord(url);
	}
	@Override
	public double calculateMetric(IFacet<String> object) {
		return 0;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		int num = 1;
		if (getValue()==null)
			params.add(new QueryParam<Integer>(Integer.class,1));
		else {
		    num = getValue().length()+1;
		    params.add(new QueryParam<Integer>(Integer.class,num));
			params.add(new QueryParam<String>(String.class,String.format("^%s",getValue())));
		}	
		return params;
	}
	
	@Override
	public String toString() {
		return String.format("Name starting with %s %s %s",getValue()==null?"":getValue(),
				getFieldname()!=null?"Compound ":"",
				getFieldname()!=null?getFieldname():"");
	}

	protected abstract FACET createRecord(String url);
	
	@Override
	public IFacet<String> getObject(ResultSet rs) throws AmbitException {
		if (record == null) {
			record = createRecord(null);
		}
		try {
			record.setValue(rs.getString(1));
			record.setCount(rs.getInt(2));
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			return record;
		}
	}		
	
}
