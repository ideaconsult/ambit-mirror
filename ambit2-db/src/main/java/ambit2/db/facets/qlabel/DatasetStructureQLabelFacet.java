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
		return getDataset()==null
				?String.format("%s/query/qlabel?%s=%s",
						(params.length>0)?params[0]:"",
						"search",
						URLEncoder.encode(getProperty().getLabel().toString()))
				:String.format("%s/dataset/%d/query/qlabel?%s=%s",
				(params.length>0)?params[0]:"",
				getDataset().getId(),		
				"search",
				URLEncoder.encode(getProperty().getLabel().toString()));
	
	}	
	@Override
	public String getSubcategoryTitle() {
		return getDataset()==null?null:getDataset().getName();
	}
	@Override
	public String getSubCategoryURL(String... params) {
		return dataset==null?null:
				String.format("%s/dataset/%d",
				(params.length>0)?params[0]:"",
				getDataset().getId()	
				);
		
	}
	
}
