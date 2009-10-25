package ambit2.rest.queryresults;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

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
	public static String resource = "/results";
	public static String resourceKey = "id";
	public static String resourceID = String.format("/{%s}",resourceKey);	

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
	protected Representation post(Representation entity)
			throws ResourceException {
		Form requestHeaders = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
		Form form = new Form(entity);
		form.getFirstValue("compound[]");
		throw new ResourceException(new Status(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Not implemented yet!"));
	}
}
