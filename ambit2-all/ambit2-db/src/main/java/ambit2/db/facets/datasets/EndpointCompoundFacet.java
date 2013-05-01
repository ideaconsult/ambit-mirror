package ambit2.db.facets.datasets;

import java.net.URLEncoder;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;

public class EndpointCompoundFacet extends PropertyDatasetFacet<Property,IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3196764532235094256L;
	
	public EndpointCompoundFacet(String url) {
		super(url);
	}
	private static String endpoint = "http://www.opentox.org/echaEndpoints.owl#";
	/**
	 * params[0] root URL 
	 * params[1] full compound URL
	 */
	@Override
	public String getResultsURL(String... params) {
		return String.format("%s/dataset?%s=%s%s",
				(params.length>0)?params[0]:"",
				"feature_sameas",
				URLEncoder.encode(getValue().toString().replace(endpoint,"")),
				(params.length>1)?params[1]:"");
	}

}
