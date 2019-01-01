package ambit2.rest.bundle;

import java.util.Set;
import java.util.TreeSet;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.db.update.bundle.UpdateBundle;
import ambit2.db.update.bundle.UpdateBundle._published_status;

public class BundleDraftMetadataResource extends BundleMetadataResource {

	protected Set<UpdateBundle._published_status> getPublishedStatus() {
		Set<_published_status> status = new TreeSet<_published_status>();
		status.add(_published_status.draft);
		return status;
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation put(Representation representation)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation delete() throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
}
