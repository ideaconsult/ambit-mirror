package ambit2.rest.bundle;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.CreateBundleVersion;
import ambit2.rest.dataset.DatasetURIReporter;

public class CallableBundleVersionCreator extends CallableDBUpdateTask<SubstanceEndpointsBundle, Form, String> {
    protected DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle> reporter;
    protected SubstanceEndpointsBundle item;

    public CallableBundleVersionCreator(SubstanceEndpointsBundle item,
	    DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle> reporter,
	    Method method, Form input, Connection connection, String token) {
	super(method, input, connection, token);
	this.item = item;
	this.reporter = reporter;
    }

    @Override
    protected SubstanceEndpointsBundle getTarget(Form input) throws Exception {
	if (Method.POST.equals(method)) {
	    if (item.getID() <= 0)
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    return item;
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected IQueryUpdate<? extends Object, SubstanceEndpointsBundle> createUpdate(SubstanceEndpointsBundle target)
	    throws Exception {
	if (Method.POST.equals(method))
	    return item != null ? new CreateBundleVersion(item) : null;
	else
	    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected String getURI(SubstanceEndpointsBundle item) throws Exception {
	if (item.getID() > 0)
	    return reporter.getURI(item);
	else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    @Override
    public String toString() {
	if (Method.POST.equals(method)) 
	    return String.format("Create version of %s", item == null ? "" : item.toString());
	return item == null ? "Read" : item.toString();
    }
}
