package ambit2.rest.algorithm.descriptors;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.OWLFile;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.rest.StatusException;
import ambit2.rest.algorithm.AlgorithmResource;

/**
 * Descriptor calculation resources
 * @author nina
 *
 */
public class AlgorithmDescriptorTypesResource extends AlgorithmResource {
	public static final String iddescriptor = "iddescriptor";
	protected IMolecularDescriptor descriptor = null;
	public enum descriptortypes  {
		constitutionalDescriptor,geometricalDescriptor,topologicalDescriptor,quantumchemical,physicochemical,patternmining,pharmacophore,simdist
	};
	public AlgorithmDescriptorTypesResource(Context context, Request request,
			Response response) {
		super(context, request, response);


	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws StatusException {
		setCategory(AlgorithmResource.algorithmtypes.descriptorcalculation.toString());

		ArrayList<String> q = new ArrayList<String>();
		for (descriptortypes d : descriptortypes.values())
			q.add(String.format("algorithm/%s/%s",getCategory(),d.toString()));
		return q.iterator();
	}
	/**
	 *http://www.w3.org/TR/owl-ref/#MIMEType
	 *application/rdf+xml
	 * @param stream
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static Dictionary readDictionary(InputStream stream,String type) throws Exception {
		Dictionary dictionary = null;
            InputStreamReader reader = new InputStreamReader(stream);
            if (type.equals("owl")) {
                dictionary = OWLFile.unmarshal(reader);
            //} else if (type.equals("owl_React")) {
              //  dictionary = OWLReact.unmarshal(reader);
            } else { // assume XML using Castor
                dictionary = Dictionary.unmarshal(reader);
            }

        return dictionary;
				
	}
}
