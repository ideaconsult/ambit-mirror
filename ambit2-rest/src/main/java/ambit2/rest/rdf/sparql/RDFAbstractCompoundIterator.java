package ambit2.rest.rdf.sparql;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Hashtable;

import net.idea.restnet.rdf.ns.OT;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.rest.OpenTox;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public abstract class RDFAbstractCompoundIterator<COMPOUND,PROPERTY> extends RDFSparqlIterator<COMPOUND> {
	protected Template compoundTemplate;
	protected Template conformerTemplate;
	protected Hashtable<RDFNode, PROPERTY> lookup;
	
	protected final static String sparql = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX dc:<http://purl.org/dc/elements/1.1/#>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"select ?dataset ?dataEntry ?compound ?feature ?value\n"+
		"where {\n"+
		"     ?dataset rdf:type ot:Dataset.\n"+
		"     ?dataset ot:dataEntry ?dataEntry.\n"+
		"     ?dataEntry ot:compound ?compound.\n"+
		"     OPTIONAL {\n"+
		"         ?dataEntry ot:values ?values.\n"+
		"         ?values rdf:type ot:FeatureValue.\n"+ 
		"         ?values ot:feature ?feature.\n"+
		"         ?values ot:value ?value.\n"+
		"         ?feature rdf:type ot:Feature.\n"+
		"     }.\n"+
		"}\n"+
		"ORDER by ?dataset ?dataEntry ?compound ?feature ?value\n"
		;
	public RDFAbstractCompoundIterator(Representation representation, MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,representation,mediaType),sparql);
	}
	
	public RDFAbstractCompoundIterator(Reference reference) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,reference, MediaType.APPLICATION_RDF_XML),sparql);
	}
	
	public RDFAbstractCompoundIterator(Reference reference,MediaType mediaType) throws ResourceException ,MalformedURLException,IOException{
		this(OT.createModel(null,reference, mediaType),sparql);
	}
	
	public RDFAbstractCompoundIterator(InputStream in,MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,in, mediaType),sparql);
	}	
	public RDFAbstractCompoundIterator(Model model, String sparql) throws ResourceException {
		super(model,sparql);
		recordIDVar = "compound";
		conformerTemplate = OpenTox.URI.conformer.getTemplate(baseReference);
		compoundTemplate = OpenTox.URI.compound.getTemplate(baseReference);
		
		lookup = new Hashtable<RDFNode, PROPERTY>();

	}
	@Override
	public void setBaseReference(Reference baseReference) {
		super.setBaseReference(baseReference);
		conformerTemplate = OpenTox.URI.conformer.getTemplate(baseReference);
		compoundTemplate = OpenTox.URI.compound.getTemplate(baseReference);
		
		RDFAbstractFeatureIterator<PROPERTY> i = null;
		try {
			i =  getPropertyIterator(jenaModel);
			i.setBaseReference(baseReference);
			while (i.hasNext()) {
				PROPERTY p = i.next();
				lookup.put(i.recordNode,p);
			}
		} finally {
			try {i.close(); } catch (Exception x) {};
		}		
	}

	protected abstract  RDFAbstractFeatureIterator<PROPERTY> getPropertyIterator(Model jenaModel);
	

	protected abstract COMPOUND newCompound(RDFNode node);
	@Override
	protected COMPOUND parse(QuerySolution querySolution, COMPOUND record) {
		//?dataset ?dataEntry ?compound ?feature ?value\n"+
		
		RDFNode node = getRecordURI(querySolution);
		if (record ==null) {
			record = newCompound(node);
		}
		RDFNode feature = querySolution.getResource("feature");
		Literal  value = querySolution.getLiteral("value");
		
		if (feature!=null) {
			PROPERTY key = getProperty(feature);
			
			if (value == null) 
				setValue(record, key, "NOT AVAILABLE"); //do smth more reasonable here
			else {
				RDFDatatype datatype = value.getDatatype();
				if (XSDDatatype.XSDdouble.equals(datatype)) 
					setValue(record,key, ((Literal)value).getDouble());
				else if (XSDDatatype.XSDfloat.equals(datatype)) 
					setValue(record,key, ((Literal)value).getFloat());
				else if (XSDDatatype.XSDinteger.equals(datatype)) 
					setValue(record,key, ((Literal)value).getInt());		
				else if (XSDDatatype.XSDstring.equals(datatype)) 
					setValue(record,key,  ((Literal)value).getString());	
			}
		}
		return record;
	}
	
	protected abstract PROPERTY newProperty(RDFNode node) ;
	protected abstract void setValue (COMPOUND record, PROPERTY property, double value);
	protected abstract void setValue (COMPOUND record, PROPERTY property, float value);
	protected abstract void setValue (COMPOUND record, PROPERTY property, int value);
	protected abstract void setValue (COMPOUND record, PROPERTY property, String value);
	
	protected PROPERTY getProperty(RDFNode node) {
		PROPERTY p = lookup.get(node);
		if (p==null) {
			p = newProperty(node);
			lookup.put(node,p);
		}
		return p;
	}
	
	
}
