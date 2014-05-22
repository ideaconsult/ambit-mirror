package ambit2.rest.substance.owner;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.db.substance.ReadSubstanceByOwner;
import ambit2.rest.substance.SubstanceResource;

/**
 * All substance by owner
 * @author nina
 *
 */
public class SubstanceByOwnerResource extends SubstanceResource<ReadSubstanceByOwner> {
	
	@Override
	protected ReadSubstanceByOwner createQuery(Context context, Request request, Response response) throws ResourceException {
		Object owneruuid = request.getAttributes().get(OwnerSubstanceFacetResource.idowner);
		return new ReadSubstanceByOwner(ReadSubstanceByOwner._ownersearchmode.owner_uuid,owneruuid.toString());

	}
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation put(Representation representation)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation put(Representation representation, Variant variant)
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
