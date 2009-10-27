package ambit2.rest.algorithm.descriptors;

import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.restlet.data.Reference;
import org.restlet.ext.rdf.Link;
import org.restlet.resource.ResourceException;

/**
 * Descriptor calculation 
 * Under development
 * @author nina
 *
 */
public class AlgorithmDescriptorResource extends AlgorithmDescriptorTypesResource {

	protected IMolecularDescriptor descriptor = null;
	public AlgorithmDescriptorResource() {
		//setCategory("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#definition");
		super();
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
	}
	@Override
	protected String add(Link link) {
		Object type = Reference.decode(getRequest().getAttributes().get("category").toString());
		if(type.equals(link.getTargetAsReference().toString()))
			return link.getSourceAsReference().toString();
		else return null;
	}
	/*
	@Override
	public Representation get(Variant variant) {
		try {
			Object descriptorID = Reference.decode(getRequest().getAttributes().get(iddescriptor).toString());
			return super.get(variant);
		} catch (Exception x) {
			return super.get(variant);
		}
	}	
	*/
}
