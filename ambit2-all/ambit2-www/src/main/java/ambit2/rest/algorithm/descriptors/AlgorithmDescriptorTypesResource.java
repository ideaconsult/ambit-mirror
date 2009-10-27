package ambit2.rest.algorithm.descriptors;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.OWLFile;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.GraphHandler;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.xml.RdfXmlReader;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.algorithm.AlgorithmCatalogResource;

/**
 * Descriptor calculation resources
 * @author nina
 *
 */
public class AlgorithmDescriptorTypesResource extends AlgorithmCatalogResource<String> {
	public static final String iddescriptor = "iddescriptor";
	public static Graph graph = null;
	protected IMolecularDescriptor descriptor = null;
	/*
	public enum descriptortypes  {
		constitutionalDescriptor,geometricalDescriptor,topologicalDescriptor,quantumchemical,physicochemical,patternmining,pharmacophore,simdist
	};
	*/
	public AlgorithmDescriptorTypesResource() {
		super();
		setCategory("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#isClassifiedAs");
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

	}
	
	protected void initGraph() throws Exception {
		InputStream in = null;
		if (graph == null) 
		try {
			graph = new Graph();
			in = DescriptorsFactory.class.getClassLoader().getResourceAsStream("ambit2/descriptors/descriptor-algorithms.owl");
			GraphHandler gh = new GraphBuilder(graph);
			Representation r = new InputRepresentation(in,MediaType.TEXT_XML);
			RdfXmlReader reader = new RdfXmlReader(r,gh);
			reader.parse();
		} catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try { in.close();} catch (Exception x) {}
		}		
	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			initGraph();
			ArrayList<String> q = new ArrayList<String>();
			Iterator<Link> links = graph.iterator();
			
			//crude way to search rdf ...
			while (links.hasNext()) {
				Link link = links.next();
				if (link.getTypeRef().toString().equals(
						getCategory()
						)) {
					String x = add(link);
					if ((x!=null) && (q.indexOf(x)<0))
						q.add(add(link));
				}	
			}
	
			return q.iterator();
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		
	}
	protected String add(Link link) {
		return String.format("algorithm/%s/%s",algorithmtypes.descriptorcalculation, Reference.encode(link.getTargetAsReference().toString()));
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
