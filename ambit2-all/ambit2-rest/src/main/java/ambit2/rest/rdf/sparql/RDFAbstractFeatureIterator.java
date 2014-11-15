package ambit2.rest.rdf.sparql;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import net.idea.restnet.rdf.ns.OT;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.rest.OpenTox;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Parses an OpenTox RDF via SPAQRL query and iterates over property instances.
 * All compound properties are represented via ot:Feature class.
 * An ot:Feature class can have name (dc:title); units (ot:units); 
 * source of origin (ot:hasSource) , which is either ot:Algorithm, ot:Model or ot:Dataset;
 * owl:sameAs - specifies an URI of an individual from another ontology, 
 * which this property is considered equivalent with.
 * @author nina
 *
 * @param <PROPERTY>
 */
public abstract class RDFAbstractFeatureIterator<PROPERTY> extends RDFSparqlIterator<PROPERTY> {
	protected Template featureTemplate;
	protected final static String sparql = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX dc:<http://purl.org/dc/elements/1.1/#>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"select ?feature ?title ?units ?src ?sameas\n"+
		"where {\n"+
		"     ?feature rdf:type ot:Feature.\n"+
		"     OPTIONAL {?feature dc:title ?title}.\n"+
		"     OPTIONAL {?feature ot:units ?units}.\n"+
		"     OPTIONAL {?feature ot:hasSource ?src}.\n"+
		"     OPTIONAL {?feature owl:sameAs ?sameas}.\n"+
		"}\n"+
		"ORDER by ?feature ?title ?units ?src ?sameas\n"
		;	
	public RDFAbstractFeatureIterator(Representation representation, MediaType mediaType) throws ResourceException , MalformedURLException,IOException{
		this(OT.createModel(null,representation,mediaType),sparql);
	}
	
	public RDFAbstractFeatureIterator(Reference reference) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,reference, MediaType.APPLICATION_RDF_XML),sparql);
	}
	
	public RDFAbstractFeatureIterator(Reference reference,MediaType mediaType) throws ResourceException, MalformedURLException,IOException {
		this(OT.createModel(null,reference, mediaType),sparql);
	}
	
	public RDFAbstractFeatureIterator(InputStream in,MediaType mediaType) throws ResourceException, MalformedURLException,IOException {
		this(OT.createModel(null,in, mediaType),sparql);
	}	
	public RDFAbstractFeatureIterator(Model model, String sparql) throws ResourceException {
		super(model,sparql);
		recordIDVar = "feature";
		featureTemplate = OpenTox.URI.feature.getTemplate(baseReference);

	}
	public RDFAbstractFeatureIterator(Model model) throws ResourceException {
		this(model,sparql);

	}	
	@Override
	public void setBaseReference(Reference baseReference) {
		super.setBaseReference(baseReference);
		featureTemplate = OpenTox.URI.feature.getTemplate(baseReference);
	}
	/**
	 * 
	 * @param node
	 * @return new PROPERTY instance
	 */
	protected abstract PROPERTY newProperty(RDFNode node) ;
	@Override
	protected PROPERTY parse(QuerySolution querySolution, PROPERTY record) {
	//?dataset ?dataEntry ?compound ?feature ?value\n"+
		
		RDFNode node = getRecordURI(querySolution);
		if (record ==null) {
			record = newProperty(node);
		}
		
		Literal  title = querySolution.getLiteral("title");
		setName(record,title==null?null:title.getString());
		
		Literal  units = querySolution.getLiteral("units");
		setUnits(record,units==null?null:units.getString());		
		
		RDFNode source = querySolution.get("src");
		setOrigin(record,source);
		RDFNode sameAs = querySolution.getResource("sameas");
		setSameAs(record,sameAs);
		return record;
	}
	/**
	 * Property name
	 * @param record
	 * @param name
	 */
	protected abstract void setName(PROPERTY record, String name);
	/**
	 * Property units
	 * @param record
	 * @param units
	 */
	protected abstract void setUnits(PROPERTY record, String units);
	/**
	 * 
	 * @param record
	 * @param node URI resource of the algorithm or model, generating the property 
	 * or URI to the dataset or a bibliographic reference the property originates. 
	 */
	protected abstract void setOrigin(PROPERTY record, RDFNode node);
	/**
	 * URI of an individual in an ontology, owl:sameAs this property
	 * e.g. if BlueObelisk cheminformatics algorithm ontology
	 * or ECHA endpoints ontology, or an individual from another OWL
	 * @param record
	 * @param node
	 */
	protected abstract void setSameAs(PROPERTY record, RDFNode node);
	
}
