package ambit2.rest.algorithm;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.GraphHandler;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.xml.RdfXmlReader;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.AmbitResource;

public class RDFGraphResource extends ServerResource {
	protected static Graph graph;
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML))
			return graph.getRdfXmlRepresentation();
		else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)) 
			return graph.getRdfTurtleRepresentation();
		else if (variant.getMediaType().equals(MediaType.TEXT_RDF_N3)) 
			return graph.getRdfN3Representation();
		else if (variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)) 
			return graph.getRdfNTriplesRepresentation();
		else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) try {
			return getHTML(); } catch (Exception x) { throw new ResourceException(x);}
		else return graph.getRdfN3Representation();
	}
	protected Representation getHTML() throws Exception {
		StringWriter w = new StringWriter();
		AmbitResource.writeHTMLHeader(w, "rdf test", getRequest());

		Iterator<Link> links = graph.iterator();
		
		w.write("<table>");
		while (links.hasNext()) {
			Link link = links.next();
			//
			w.write(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>",
					link.getSource(),
					link.getTypeRef(),
					link.getTargetAsGraph()));
		}
		w.write("</table>");
		AmbitResource.writeHTMLFooter(w, "rdf test", getRequest());
		w.flush();
		return new StringRepresentation(w.toString());
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			initGraph();
		} catch (Exception x) {
			throw new ResourceException(x);
		}
		MediaType[] mimeTypes = new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.APPLICATION_RDF_XML,	
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
		};
        List<Variant> variants = new ArrayList<Variant>();
        for (MediaType mileType:mimeTypes) variants.add(new Variant(mileType));
        getVariants().put(Method.GET, variants);
       // getVariants().put(Method.POST, variants);
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
}
