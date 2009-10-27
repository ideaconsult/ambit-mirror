package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.descriptors.FunctionalGroup;

/**
 * SMARTS search in database
 * @author nina
 *
 */
public class SmartsQueryResource  extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public final static String smartsKey =  "smarts";
	public final static String resourceID =  String.format("/{%s}",smartsKey);
	public final static String resource =  String.format("/%s",smartsKey);
	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			Form form = request.getResourceRef().getQueryAsForm();
			Object key = form.getFirstValue("search");
			if (key ==null) {
				key = request.getAttributes().get(smartsKey);
				if (key==null) throw new AmbitException("Empty smarts");
			}
			String smarts = Reference.decode(key.toString());
			QuerySMARTS query = new QuerySMARTS();
			query.setChemicalsOnly(true);
			query.setValue(new FunctionalGroup(smarts,smarts,smarts));
			
			Object id = request.getAttributes().get("dataset_id");
			if (id != null) try {
				QueryDatasetByID scope = new QueryDatasetByID();
				scope.setValue(new Integer(Reference.decode(id.toString())));
				
				QueryCombinedStructure combined = new QueryCombinedStructure();
				combined.add(query);
				combined.setScope(scope);
				return combined;
			} catch (NumberFormatException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,String.format("Invalid resource id %d",id),x
						);				
			} catch (Exception x) {
				throw new ResourceException(
						Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
						);
			}
			return query;

		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		}
	}		

/*
	@Override
	public void acceptRepresentation(Representation entity)
			throws ResourceException {
		 try {
		  if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM,true)) {
		   Form form = new Form(entity);
		  // User u = new User();
		   //u.setName(form.getFirstValue("user[name]"));
		   u.setName(form.getFirstValue("search"));
		   // :TODO {save the new user to the database}
		   getResponse().setStatus(Status.SUCCESS_OK);
		   // We are setting the representation in the example always to
		   // JSON.
		   // You could support multiple representation by using a
		   // parameter
		   // in the request like "?response_format=xml"
		  // Representation rep = new JsonRepresentation(u.toJSON());
		  // getResponse().setEntity(rep);
		  } else {
		   getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		  }
		 } catch (Exception e) {
		  getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		 }
	}
*/
}
