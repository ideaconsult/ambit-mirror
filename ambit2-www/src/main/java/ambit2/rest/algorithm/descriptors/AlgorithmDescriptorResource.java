package ambit2.rest.algorithm.descriptors;

import java.util.Iterator;

import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import ambit2.rest.StatusException;
import ambit2.rest.algorithm.AlgorithmCatalogResource;

/**
 * Descriptor calculation 
 * Under development
 * @author nina
 *
 */
public class AlgorithmDescriptorResource extends AlgorithmDescriptorTypesResource {

	protected IMolecularDescriptor descriptor = null;

	public AlgorithmDescriptorResource(Context context, Request request,
			Response response) {
		super(context, request, response);


	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws StatusException {
		setCategory(AlgorithmCatalogResource.algorithmtypes.descriptorcalculation.toString());
		/*
		ArrayList<String> q = new ArrayList<String>();
		for (descriptors d : descriptors.values())
			q.add(String.format("%s/%s",request.getOriginalRef(),d.toString()));
			*/
		return null;

	}
	@Override
	public Representation getRepresentation(Variant variant) {
		try {
			Object descriptorID = Reference.decode(getRequest().getAttributes().get(iddescriptor).toString());
			return super.getRepresentation(variant);
		} catch (Exception x) {
			return super.getRepresentation(variant);
		}
	}	
}
