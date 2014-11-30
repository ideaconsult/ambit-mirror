package ambit2.rest.facet;

import java.net.URLEncoder;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.study.Protocol._categories;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.facets.bookmarks.BookmarksByTopicFacet;
import ambit2.db.facets.datasets.EndpointCompoundFacet;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;
import ambit2.db.facets.qlabel.DatasetConsensusLabelFacet;
import ambit2.db.facets.qlabel.DatasetStrucTypeFacet;
import ambit2.db.facets.qlabel.DatasetStructureQLabelFacet;
import ambit2.db.substance.study.facet.OwnerFacet;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;
import ambit2.rest.OpenTox;
import ambit2.rest.bookmark.BookmarkResource;
import ambit2.rest.structure.ConformerURIReporter;
import ambit2.rest.substance.SubstanceResource;
import ambit2.rest.substance.composition.SubstanceStructuresResource;
import ambit2.rest.substance.owner.OwnerSubstanceFacetResource;

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
	protected ConformerURIReporter<IQueryRetrieval<IStructureRecord>> cmpReporter;
	public ConformerURIReporter<IQueryRetrieval<IStructureRecord>> getCmpReporter() {
		return cmpReporter;
	}
	public void setCmpReporter(
			ConformerURIReporter<IQueryRetrieval<IStructureRecord>> cmpReporter) {
		this.cmpReporter = cmpReporter;
	}
	public FacetURIReporter(Request baseRef) {
		super(baseRef,null);
		cmpReporter = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(baseRef,null);
	}
	public FacetURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, IFacet item) {
		Reference root = getBaseReference();

		if (item instanceof EndpointCompoundFacet) {
			
			EndpointCompoundFacet q = (EndpointCompoundFacet) item;
			
			String cmpURI = "";
			if ((q.getDataset()!=null) && (q.getDataset().getIdchemical()>0 || q.getDataset().getIdstructure()>0)) 
				cmpURI = String.format("&%s=%s",OpenTox.params.compound_uri,URLEncoder.encode(cmpReporter.getURI(q.getDataset())));

			return q.getResultsURL(root.toString(),cmpURI);
		} else if (item instanceof DatasetStructureQLabelFacet) {
			DatasetStructureQLabelFacet q = (DatasetStructureQLabelFacet) item;
			return q.getResultsURL(root.toString());		
		} else if (item instanceof DatasetConsensusLabelFacet) {
			DatasetConsensusLabelFacet q = (DatasetConsensusLabelFacet) item;
			return q.getResultsURL(root.toString());			
		} else if (item instanceof DatasetStrucTypeFacet) {
			DatasetStrucTypeFacet q = (DatasetStrucTypeFacet) item;
			return q.getResultsURL(root.toString());			
				
		} else if (item instanceof PropertyDatasetFacet)  {
			PropertyDatasetFacet<Property,SourceDataset> q = (PropertyDatasetFacet<Property,SourceDataset>) item;
			return String.format("%s/dataset/%d?feature_uris[]=%s/dataset/%s/feature&feature_uris[]=%s/feature/%s&property=%s/feature/%s&search=%s",
							root,q.getDataset().getId(),
						    root,q.getDataset().getId(),
						    root,q.getProperty().getId(),
						    root,q.getProperty().getId(),
							item.getValue());
		} else if (item instanceof BookmarksByTopicFacet)  {
			BookmarksByTopicFacet q = (BookmarksByTopicFacet) item;
			return String.format("%s%s/%s?hasTopic=%s",
							root,
						    BookmarkResource.resource,
						    q.getCreator(),
						    Reference.encode(item.getValue().toString()));
		} else if (item instanceof SubstanceByCategoryFacet)  {
			if (item!=null && item.getValue()!=null) {
				_categories endpoint = ((SubstanceByCategoryFacet)item).getEndpoint();
				return String.format("%s%s?type=endpointcategory&search=%s",
							root,
							SubstanceResource.substance,
							endpoint==null?"":endpoint.name()
						    );			
			} else return "";
		
		} else if (item instanceof OwnerFacet)  {
			String uri = String.format("%s%s/%s%s",
							root,
							OwnerSubstanceFacetResource.owner,
							item==null?"ALL":item.getValue()==null?"ALL":item.getValue().toString(),
							SubstanceStructuresResource.structure
						    );			
			return String.format("%s/ui/_dataset?dataset_uri=%s", root,Reference.encode(uri));
		} else 
			return item.getResultsURL(root.toString());
	}

}
