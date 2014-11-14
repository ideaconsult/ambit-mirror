package ambit2.rest.dataset;

import java.util.concurrent.Callable;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.i.task.ITaskResult;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStoredResultsByName;
import ambit2.rest.OpenTox;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.task.CallableQueryResultsCreator;
import ambit2.rest.task.PolicyProtectedTask;
import ambit2.rest.task.TaskResult;

public class CollectionStructureResource<Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<Q> {
	public final static String collection = "/collection";
	public final static String folderKey = "folderid";
	public final static String datasetKey = OpenTox.URI.dataset.getKey();

	@Override
	protected Q createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			setGroupProperties(context, request, response);
			setTemplate(createTemplate(context, request, response));
			
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}

			Object datasetid = request.getAttributes().get(datasetKey);
			Object folder = request.getAttributes().get(folderKey);
			if (datasetid != null)  try {
				QueryStoredResultsByName q = new QueryStoredResultsByName();
				q.setChemicalsOnly(false);
				IStoredQuery sq = new StoredQuery();
				sq.setName(datasetid.toString());
				q.setFieldname(sq);
				q.setValue(folder==null?"temp":folder.toString());
				return (Q)q;

			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			}
			else throw new InvalidResourceIDException("");
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		}		
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		/*
		if ((entity == null) || !entity.isAvailable()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");
		
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			return copyDatasetToQueryResultsTable(new Form(entity),true);
		} 
		*/
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	/**
	 * Creates new entry in query table and adds structures into query_results
	 */
	protected Representation copyDatasetToQueryResultsTable(Form form, boolean clearPreviousContent)
			throws ResourceException {
		String token = getToken();
		Callable<ITaskResult> callable = null;
		
		Object datasetid = getRequest().getAttributes().get(datasetKey);
		Object folder = getRequest().getAttributes().get(folderKey);
		if (folder!=null) { form.removeAll("folder"); form.add("folder", folder.toString());}
		if (datasetid!=null) { form.removeAll("title"); form.add("title", datasetid.toString()); }

		IStoredQuery sq = new StoredQuery();
		sq.setName(datasetid.toString());
		callable = new CallableQueryResultsCreator(
					form,
					getRequest().getRootRef(),
					getContext(),
					sq,
					token);
			((CallableQueryResultsCreator)callable).setClearPreviousContent(clearPreviousContent);
		
		try {
			getResponse().setLocationRef(callable.call().getReference());
			if (token != null) {
				PolicyProtectedTask task = new PolicyProtectedTask(token.toString(),true);
				task.setUri(new TaskResult(getResponse().getLocationRef().toString(),true));
				task.setPolicy();
			}
			getResponse().setStatus(Status.SUCCESS_OK);
			return new StringRepresentation(getResponse().getLocationRef().toString(),MediaType.TEXT_URI_LIST);			
		} catch (ResourceException x) {
			throw x;
		} catch  (Exception x) {
			throw new ResourceException(x);
		}

	}		
}