package ambit2.rest.queryresults;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.rest.StatusException;
import ambit2.rest.query.StructureQueryResource;

/**
 * Makes available previousy stored search results as Datasets http://opentox.org/development/wiki/dataset
 * Resource wrapper for {@link QueryStoredResults}
 * @author nina
 *
 */
public class QueryResultsResource extends StructureQueryResource<QueryStoredResults> {
	public static String resource = "/query/results";
	public static String resourceKey = "id";
	public static String resourceID = String.format("%s/{%s}",resource,resourceKey);	
	public QueryResultsResource(Context context, Request request,
			Response response) {
		super(context, request, response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));			
	}

	@Override
	protected QueryStoredResults createQuery(Context context, Request request,
			Response response) throws StatusException {
		Object key = request.getAttributes().get(resourceKey);
		if (key !=null) try {
			QueryStoredResults q = new QueryStoredResults() {
				@Override
				public String toString() {
					return getFieldname()==null?"Previous search result":String.format("Previous search result #%d", getFieldname().getId());
				}
			};
			q.setChemicalsOnly(true);
			q.setFieldname(new StoredQuery(Integer.parseInt(key.toString())));
			q.setValue(null);
			return q;
		} catch (Exception x) {
			throw new StatusException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		throw new StatusException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	@Override
	public boolean allowPost() {
		return true;
	}
	@Override
	public void acceptRepresentation(Representation entity)
			throws ResourceException {
		throw new ResourceException(new Status(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Not implemented yet!"));
	}
}
