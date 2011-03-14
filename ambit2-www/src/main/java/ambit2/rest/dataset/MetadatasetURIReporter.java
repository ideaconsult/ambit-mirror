package ambit2.rest.dataset;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ResourceDoc;

public class MetadatasetURIReporter<Q extends IQueryRetrieval<ISourceDataset>> extends DatasetURIReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6706277580631778672L;

	public MetadatasetURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public MetadatasetURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}	
	public MetadatasetURIReporter() {
		super();
	}	
	@Override
	public String getURI(String ref, ISourceDataset dataset) {
		return String.format("%s/metadata",super.getURI(ref, dataset));
	}
}
