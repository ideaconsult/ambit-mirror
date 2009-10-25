package ambit2.rest.test.algorithm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.junit.Test;
import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.Entry;
import org.restlet.data.MediaType;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.GraphHandler;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.Literal;
import org.restlet.ext.rdf.internal.xml.RdfXmlReader;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

import ambit2.rest.algorithm.descriptors.AlgorithmDescriptorTypesResource;
import ambit2.rest.test.ResourceTest;

public class AlgorithmDescriptorsResource extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/algorithm/descriptorcalculation", port);
	}
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count == 8;
	}	
	@Test
	public void testDictionary() throws Exception {
		//InputStream in = getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/dict/data/descriptor-algorithms.owl");
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/descriptors/descriptor-algorithms_old.owl");
		Dictionary dict = AlgorithmDescriptorTypesResource.readDictionary(in,"owl");

		System.out.println(dict.getEntry("atomhybridizationvsepr"));
		System.out.println(dict.getNS());
		for (Entry entry : dict.getEntries()) {
			System.out.println(entry);
			if (entry.getClassName().equals("Descriptor")) {
				
			}
			/*
			System.out.println(entry.getDefinition());
			System.out.println(entry.getDescription());
			System.out.println("id " + entry.getID());
			System.out.println("label "+entry.getLabel());
			System.out.println(entry.getRawContent());
			System.out.println("metadata " +entry.getDescriptorMetadata());
			System.out.println("-------");
			*/			
		}
	}	
	
	@Test
	public void testRDFReader() throws Exception {
		//InputStream in = getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/dict/data/descriptor-algorithms.owl");
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/descriptors/descriptor-algorithms.owl");
		Graph g = new Graph();
		GraphHandler gh = new GraphBuilder(g);
		Representation r = new InputRepresentation(in);
		RdfXmlReader reader = new RdfXmlReader(r,gh);
		reader.parse();
		Iterator<Link> links = g.iterator();
		while (links.hasNext()) {
			Link link = links.next();
			System.out.println(String.format("%s\t%s\t%s",link.getSource(),link.getTypeRef(),
					(link.getTarget()==null)?"":
						(link.getTarget() instanceof Literal)?link.getTargetAsLiteral().getValue():link.getTarget()));
		}
		in.close();
		
	}		
}
