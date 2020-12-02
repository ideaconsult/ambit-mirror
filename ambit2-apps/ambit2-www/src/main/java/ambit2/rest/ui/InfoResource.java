package ambit2.rest.ui;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class InfoResource extends ServerResource {

  @Override
  protected Representation get() throws ResourceException {
    return new StringRepresentation("{\"indigo_xversion\": \"v1.r2\",\n \"imago_versions\": \"v3.r4\" }", MediaType.APPLICATION_JSON);
  }

  @Override
  protected Representation post(Representation entity) throws ResourceException {
    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
  }

  @Override
  protected Representation put(Representation representation) throws ResourceException {
    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
  }

  @Override
  protected Representation delete() throws ResourceException {
    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
  }

}
