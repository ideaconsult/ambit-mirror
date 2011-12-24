package ambit2.db.facets.qlabel;

import java.net.URLEncoder;

import ambit2.base.data.ConsensusLabel.CONSENSUS_LABELS;
import ambit2.base.data.ConsensusLabel;
import ambit2.base.data.SourceDataset;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;

/**
 * 
'CHRIP-www.safe.nite.go.jp', 46, 'Consensus', '2'
'CHRIP-www.safe.nite.go.jp', 799, 'Consensus', '3'
'CHRIP-www.safe.nite.go.jp', 1, 'Majority', '1:1:2'
'CHRIP-www.safe.nite.go.jp', 69, 'Majority', '1:2'
'CHRIP-www.safe.nite.go.jp', 12, 'Unconfirmed', '1'
'CHRIP-www.safe.nite.go.jp', 21, 'Ambiguous', '1:1'
'CHRIP-www.safe.nite.go.jp', 7, 'Ambiguous', '1:1:1'

 * @author nina
 *
 */
public class DatasetConsensusLabelFacet  extends PropertyDatasetFacet<ConsensusLabel, SourceDataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4011962497826164064L;

	public DatasetConsensusLabelFacet(String url) {
		super(url);
	}

	@Override
	public String getResultsURL(String... params) {
		return String.format("%s/dataset/%d/query/consensuslabel?%s=%s&%s=%s",
				(params.length>0)?params[0]:"",
				getDataset().getId(),		
				"search",
				URLEncoder.encode(getProperty().getLabel().toString()),
				"text",
				getProperty().getText()==null?"":URLEncoder.encode(getProperty().getText().toString())
				);
	}	
}
