package ambit2.rest.bundle;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.substance.ReadSubstancesByBundle;
import ambit2.rest.OpenTox;
import ambit2.rest.substance.SubstanceResource;

/**
 * Substances per /bundle/{id}
 * @author nina
 *
 * @param <Q>
 */
public class BundleSubstanceResource<Q extends IQueryRetrieval<SubstanceRecord>> extends SubstanceResource<Q,SubstanceRecord> {

	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
		if (idbundle != null)  try {
			Integer idnum = new Integer(Reference.decode(idbundle.toString()));
			ReadSubstancesByBundle q = new ReadSubstancesByBundle();
			SubstanceEndpointsBundle b = new SubstanceEndpointsBundle();
			b.setID(idnum);
			q.setFieldname(b);
			return (Q)q;
		} catch (NumberFormatException x) {
		}
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}	

	

}
