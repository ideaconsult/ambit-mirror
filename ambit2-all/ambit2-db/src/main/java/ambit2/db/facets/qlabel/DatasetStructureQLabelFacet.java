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
		String label = getProperty()==null?null:URLEncoder.encode(getProperty().getLabel().toString());

		if (label==null)
			return getDataset()==null
				?String.format("%s/query/label_strucs",(params.length>0)?params[0]:"")
				:String.format("%s/dataset/%d/query/label_strucs",
				(params.length>0)?params[0]:"",
				getDataset().getId());
		else
			return getDataset()==null
				?String.format("%s/query/qlabel?%s=%s",
						(params.length>0)?params[0]:"",
						"search",
						label)
				:String.format("%s/dataset/%d/query/qlabel?%s=%s",
				(params.length>0)?params[0]:"",
				getDataset().getId(),		
				"search",
				label);
	
	}	
	@Override
	public String getSubcategoryTitle() {
		return getProperty()==null?"":getProperty().getLabel().toString();
	}
	@Override
	public String getSubCategoryURL(String... params) {
		return null;
		/*
		return dataset==null?null:
				String.format("%s/dataset/%d/metadata",
				(params.length>0)?params[0]:"",
				getDataset().getId()	
				);
		*/
	}
	
}
