package ambit2.db.facets.datasets;

import java.net.URLEncoder;

import ambit2.base.facet.AbstractFacet;

public class DatasetPrefixFacet extends AbstractFacet<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3704763536776924159L;
	public DatasetPrefixFacet(String url) {
		this.url = url;
	}
	@Override
	public String getResultsURL(String... params) {
		return String.format("%s/dataset?search=^%s",params.length>0?params[0]:"",getValue());
	}
	
	@Override
	public String getSubCategoryURL(String... params) {
		if (getURL()==null) return null;
		int pos = getURL().indexOf("?");
		return String.format("%s?search=%s",pos>0?getURL().substring(0,pos):getURL(),URLEncoder.encode(getValue())); 
		
	}
}
