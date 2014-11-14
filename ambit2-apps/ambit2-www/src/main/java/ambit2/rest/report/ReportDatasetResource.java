package ambit2.rest.report;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.OpenTox;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.task.CallableQueryProcessor;

public class ReportDatasetResource<Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<Q> {
	protected String dataset_uri;
	protected String[] feature_uris;
	public static final String resource = "/report";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		Form form  = getResourceRef(getRequest()).getQueryAsForm();
		//dataset_uri = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		//feature_uris = form.getValuesArray(OpenTox.params.feature_uris.toString());
	}
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		if (dataset_uri!=null) try {
			Object q = CallableQueryProcessor.getQueryObject(new Reference(dataset_uri), 
						getRequest().getRootRef(),getApplication().getContext());
			if ((q!=null) && (q instanceof AbstractStructureQuery)) {
				setTemplate(createTemplate(getContext(),getRequest(),getResponse(),feature_uris));
				return (Q)q;
			} else 
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Processing foreign datasets not implemented!");
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		return null;
	}

	@Override
	protected Template createTemplate(Context context, Request request,
			Response response) throws ResourceException {
		return createTemplate(context, request, response,feature_uris);
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM)) {
			Form form = new Form(entity);
			dataset_uri = form.getFirstValue(OpenTox.params.dataset_uri.toString());
			feature_uris = form.getValuesArray(OpenTox.params.feature_uris.toString());		
			queryObject = createQuery(getContext(),getRequest(),getResponse());
			if ((feature_uris!=null) && (feature_uris.length>0))
			setTemplate(createTemplate(getContext(),getRequest(),getResponse(),feature_uris));
			return super.get(variant);
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,MediaType.APPLICATION_WWW_FORM + " required");
	}
}
