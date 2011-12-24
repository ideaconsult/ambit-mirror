package ambit2.db.facets.qlabel;

import java.net.URLEncoder;

import ambit2.base.data.QLabel;
import ambit2.base.data.SourceDataset;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;

public class DatasetStructureQLabelFacet extends PropertyDatasetFacet<QLabel, SourceDataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4011962497826164064L;

	public DatasetStructureQLabelFacet(String url) {
		super(url);
	}

	@Override
	public String getResultsURL(String... params) {
		return String.format("%s/dataset/%d/query/qlabel?%s=%s",
				(params.length>0)?params[0]:"",
				getDataset().getId(),		
				"search",
				URLEncoder.encode(getProperty().getLabel().toString()));
	}	
}
