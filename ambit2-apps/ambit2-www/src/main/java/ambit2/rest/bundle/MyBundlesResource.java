package ambit2.rest.bundle;

import java.util.Set;
import java.util.TreeSet;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.UpdateBundle;
import ambit2.db.update.bundle.UpdateBundle._published_status;

public class MyBundlesResource extends BundleMetadataResource {

	@Override
	protected IQueryRetrieval<SubstanceEndpointsBundle> createQuery(
			Context context, Request request, Response response)
			throws ResourceException {
		String username = getUserName();
		if (username == null)
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		return super.createQuery(context, request, response);
	}

	@Override
	protected String getUserName() {
		if (getClientInfo() == null || getClientInfo().getUser() == null)
			return null;
		else
			return getClientInfo().getUser().getIdentifier();
	}

	protected Set<UpdateBundle._published_status> getPublishedStatus() {
		Set<_published_status> status = new TreeSet<_published_status>();
		status.add(_published_status.published);
		status.add(_published_status.draft);

		return status;
	}
}
