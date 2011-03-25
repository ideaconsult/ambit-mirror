package ambit2.rest.facet;

import java.net.URLEncoder;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.facet.IFacet;
import ambit2.db.facets.datasets.EndpointCompoundFacet;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.dataset.MetadatasetResource;

/**
 * Generates URI for {@link IFacet}
 * @author nina
 *
 * @param <Q>
 */
public class FacetURIReporter <Q extends IQueryRetrieval<IFacet>> extends QueryURIReporter<IFacet, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public FacetURIReporter(Request baseRef) {
		super(baseRef,null);
	}
	public FacetURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, IFacet item) {
		if (item instanceof EndpointCompoundFacet) {
			Reference root = getBaseReference();
			EndpointCompoundFacet q = (EndpointCompoundFacet) item;
			
			return String.format("%s/dataset?%s=%s",
							root,
							MetadatasetResource.search_features.feature_sameas,
							URLEncoder.encode(item.getValue().toString()));
		} else if (item instanceof PropertyDatasetFacet)  {
			Reference root = getBaseReference();
			PropertyDatasetFacet<Property,SourceDataset> q = (PropertyDatasetFacet<Property,SourceDataset>) item;
			return String.format("%s/dataset/%d?feature_uris[]=%s/dataset/%s/feature&feature_uris[]=%s/feature/%s&property=%s/feature/%s&search=%s",
							root,q.getDataset().getId(),
						    root,q.getDataset().getId(),
						    root,q.getProperty().getId(),
						    root,q.getProperty().getId(),
							item.getValue());

		} else 
			return item.getResultsURL();
	}

}
