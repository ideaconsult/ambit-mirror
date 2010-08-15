package ambit2.rest;

import javax.xml.stream.XMLStreamWriter;

import org.restlet.Request;

import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.rdf.OT;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class QueryStaXReporter<T,Q extends IQueryRetrieval<T>> extends QueryReporter<T, Q, XMLStreamWriter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected QueryURIReporter<T, IQueryRetrieval<T>> uriReporter;
	protected abstract QueryURIReporter<T, IQueryRetrieval<T>> createURIReporter(Request req);
	protected final static String ot = "ot";
	protected final static String rdf = "rdf";
	protected final static String dc = "dc";
	protected final static String owl = "owl";
	
	public QueryStaXReporter(Request request) {
		super();
		uriReporter = createURIReporter(request);
	}
	@Override
	public void footer(XMLStreamWriter writer, Q query) {
		try {
			writer.writeEndElement();
			writer.writeEndDocument();
		} catch (Exception x) {}
		
	}

	@Override
	public void header(XMLStreamWriter writer, Q query) {
		/**
		 * xmlns:ac="http://apps.ideaconsult.net:8080/ambit2/compound/"
    xmlns:ot="http://www.opentox.org/api/1.1#"
    xmlns:bx="http://purl.org/net/nknouf/ns/bibtex#"
    xmlns:otee="http://www.opentox.org/echaEndpoints.owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:ar="http://apps.ideaconsult.net:8080/ambit2/reference/"
    xmlns="http://apps.ideaconsult.net:8080/ambit2/"
    xmlns:am="http://apps.ideaconsult.net:8080/ambit2/model/"
    xmlns:af="http://apps.ideaconsult.net:8080/ambit2/feature/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:ad="http://apps.ideaconsult.net:8080/ambit2/dataset/"
    xmlns:ag="http://apps.ideaconsult.net:8080/ambit2/algorithm/"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:ota="http://www.opentox.org/algorithmTypes.owl#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
  xml:base="http://apps.ideaconsult.net:8080/ambit2/">
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Dataset"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Compound"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Feature"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#FeatureValue"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#NumericFeature">
    <rdfs:subClassOf rdf:resource="http://www.opentox.org/api/1.1#Feature"/>
  </owl:Class>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#DataEntry"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#hasSource"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#dataEntry"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#values"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#compound"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#feature"/>
  <owl:DatatypeProperty rdf:about="http://www.opentox.org/api/1.1#units"/>
  <owl:DatatypeProperty rdf:about="http://www.opentox.org/api/1.1#value"/>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/title"/>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/description"/>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/type"/>
		 */
		try {
			writer.writeStartDocument();
			writer.setPrefix(ot, OT.NS);
			writer.setPrefix(rdf, RDF.getURI());
			writer.setPrefix(rdf, OWL.getURI());
			writer.setPrefix(dc, DC.getURI());
			
			writer.writeStartElement(rdf,"RDF");
		    writer.writeNamespace(ot, OT.NS);
			writer.writeNamespace(rdf, RDF.getURI());
			writer.writeNamespace(rdf, OWL.getURI());
			writer.writeNamespace(dc, DC.getURI());			    
		    writer.setDefaultNamespace(ot);
		} catch (Exception x) {}
		
	}


}
