package ambit2.workflow;

import ambit2.base.data.SourceDataset;


public class DatasetInteraction extends UserInteraction<SourceDataset> {
	public DatasetInteraction(SourceDataset dataset) {
		super(dataset,DBWorkflowContext.DATASET);
	}
}
