package ambit2.rest.bundle;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.db.DBConnection;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.ReadBundle;
import ambit2.db.update.bundle.ReadBundleVersion;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetURIReporter;

public class BundleVersionsResource extends BundleMetadataResource {
    @Override
    protected Representation post(Representation entity) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected IQueryRetrieval<SubstanceEndpointsBundle> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	ReadBundle query = null;
	Object id = request.getAttributes().get(OpenTox.URI.bundle.getKey());
	if (id != null)
	    try {
		Integer idnum = new Integer(Reference.decode(id.toString()));
		if (idnum.intValue() <= 0)
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		dataset = new SubstanceEndpointsBundle();
		dataset.setID(idnum);
		query = new ReadBundleVersion();
		query.setValue(dataset);
		return query;
	    } catch (NumberFormatException x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }
	else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
    }
    
    @Override
    protected IQueryRetrieval<SubstanceEndpointsBundle> createUpdateQuery(Method method, Context context,
	    Request request, Response response) throws ResourceException {
	Object id = request.getAttributes().get(OpenTox.URI.bundle.getKey());
	if (Method.POST.equals(method)) {
	    if (id != null) {
		try {
		    Integer idnum = new Integer(Reference.decode(id.toString()));
		    dataset = new SubstanceEndpointsBundle();
		    dataset.setID(idnum);
		    ReadBundle query = new ReadBundle();
		    query.setValue(dataset);
		    return query;
		} catch (NumberFormatException x) {
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	    }
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    
    @Override
    protected CallableProtectedTask<String> createCallable(Method method, Form form, SubstanceEndpointsBundle item)
	    throws ResourceException {
	SubstanceEndpointsBundle bundle = null;
	Object id = getRequest().getAttributes().get(OpenTox.URI.bundle.getKey());
	if ((id != null))
	    try {
		Integer i = new Integer(Reference.decode(id.toString()));
		if (i > 0)
		    bundle = new SubstanceEndpointsBundle(i);
	    } catch (Exception x) {
	    }

	Connection conn = null;
	try {
	    DatasetURIReporter r = new DatasetURIReporter(getRequest());
	    DBConnection dbc = new DBConnection(getApplication().getContext(), getConfigFile());
	    conn = dbc.getConnection();
	    return new CallableBundleVersionCreator(bundle, r, method, form, conn, getToken());
	} catch (Exception x) {
	    x.printStackTrace();
	    try {
		conn.close();
	    } catch (Exception xx) {
	    }
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
	}
    }

    @Override
    protected TaskCreator getTaskCreator(Representation entity, Variant variant, Method method, boolean async)
            throws Exception {
	return getTaskCreator(null, method, async, null);
    }    
}
