package ambit2.rest.query;

import java.io.Serializable;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;

/**
 * Parent class for resources generating async tasks on POST
 * @author nina
 *
 * @param <Q>
 * @param <T>
 */
public abstract class ProcessingResource<Q extends IQueryRetrieval<T>,T extends Serializable> extends QueryResource<Q, T> {

	protected String getObjectURI(Form queryForm) throws ResourceException {
		Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(queryForm);
		if (datasetURI==null) throw new ResourceException(
				Status.CLIENT_ERROR_BAD_REQUEST,String.format("Empty %s [%s]", OpenTox.params.dataset_uri.toString(), OpenTox.params.dataset_uri.getDescription()));
		return Reference.decode(datasetURI.toString());
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			return process(entity, variant,true);
		}
	}

}
