package ambit2.db.facets.qlabel;

import java.net.URLEncoder;

import ambit2.base.data.SourceDataset;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;
import ambit2.db.search.structure.QueryStructureByQualityPairLabel.CONSENSUS_LABELS;

public class DatasetConsensusLabelFacet  extends PropertyDatasetFacet<CONSENSUS_LABELS, SourceDataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4011962497826164064L;

	public DatasetConsensusLabelFacet(String url) {
		super(url);
	}

	@Override
	public String getResultsURL(String... params) {
		return String.format("%s/dataset/%d/query/consensuslabel?%s=%s",
				(params.length>0)?params[0]:"",
				getDataset().getId(),		
				"search",
				URLEncoder.encode(getProperty().toString()));
	}	
}
