package ambit2.db.facets.qlabel;

import java.net.URLEncoder;

import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;

public class DatasetStrucTypeFacet  extends PropertyDatasetFacet<IStructureRecord.STRUC_TYPE, SourceDataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4011962497826164064L;

	public DatasetStrucTypeFacet(String url) {
		super(url);
	}

	@Override
	public String getResultsURL(String... params) {
		String label = getProperty()==null?null:URLEncoder.encode(getProperty().toString());

		if (label==null)
			return getDataset()==null
				?String.format("%s/query/structype",(params.length>0)?params[0]:"")
				:String.format("%s/dataset/%d/query/structype",
				(params.length>0)?params[0]:"",
				getDataset().getId());
		else
			return getDataset()==null
				?String.format("%s/query/structype?%s=%s",
						(params.length>0)?params[0]:"",
						"search",
						label==null?"":label)
				:String.format("%s/dataset/%d/query/structype?%s=%s",
				(params.length>0)?params[0]:"",
				getDataset().getId(),		
				"search",
				label==null?"":label);
	
	}	
	@Override
	public String getSubcategoryTitle() {
		return getProperty()==null?"":getProperty().toString();
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
