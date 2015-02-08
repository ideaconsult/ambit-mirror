package ambit2.rest.property.annotations;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.rdf.ns.OT;
import net.idea.restnet.rdf.ns.OT.OTClass;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.PropertyAnnotation;
import ambit2.base.interfaces.ICategory;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.reference.ReferenceURIReporter;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Feature reporter
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class PropertyAnnotationRDFReporter<Q extends IQueryRetrieval<PropertyAnnotation<String>>> extends
	QueryRDFReporter<PropertyAnnotation<String>, Q> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -8857789530109166243L;
    protected ReferenceURIReporter referenceReporter;

    public PropertyAnnotationRDFReporter(Request request, MediaType mediaType) {
	super(request, mediaType);
	referenceReporter = new ReferenceURIReporter(request);
    }

    @Override
    protected QueryURIReporter createURIReporter(Request reference, ResourceDoc doc) {
	PropertyAnnotationURIReporter r = new PropertyAnnotationURIReporter(reference);
	r.setPropertyOnly(true);
	return r;
    }

    public void header(com.hp.hpl.jena.ontology.OntModel output, Q query) {
	super.header(output, query);
	OT.OTClass.Feature.createOntClass(getJenaModel());

    }

    @Override
    public Object processItem(PropertyAnnotation item) throws Exception {
	Individual feature = null;
	OTClass featureType = OTClass.Feature;
	String id = uriReporter.getURI(item);
	if (id == null)
	    throw new AmbitException("No property id");

	feature = getOutput().createIndividual(id, featureType.getOntClass(getOutput()));
	annotation2RDF(item, getOutput(), feature, uriReporter.getBaseReference().toString());
	return item;
    }

    public static void annotation2RDF(PropertyAnnotation item, OntModel jenaModel, Individual feature,
	    String rootReference) throws AmbitException {

	com.hp.hpl.jena.rdf.model.Property predicate = null;
	if (item.getPredicate().startsWith("http")) { // there is a chance this
						      // is an URI
	    predicate = jenaModel.createProperty(item.getPredicate());
	} else {
	    predicate = jenaModel.createProperty(String.format("http://www.opentox.org/api/1.1#%s",
		    Reference.encode(item.getPredicate())));
	}
	String object = item.getObject().toString();
	if (item.getType().startsWith("^^")) { // xsd:ToxicCategory a
					       // rdfs:Datatype; rdfs:subClassOf
					       // xsd:string.
	    try {
		// ot:ToxicCategory a rdfs:Datatype; rdfs:subClassOf xsd:string.
		String ctype = item.getType().replace("^^", "");
		Resource rdfDataType = jenaModel.createResource(OT.NS + ctype);
		jenaModel.add(rdfDataType, RDF.type, RDFS.Datatype);
		feature.addProperty(predicate, jenaModel.createTypedLiteral(object, rdfDataType.getURI()));

		try {
		    ICategory.CategoryType.valueOf(ctype);
		    jenaModel.add(rdfDataType, RDFS.subClassOf, jenaModel.getResource(OT.NS + "ToxicityCategory"));
		} catch (Exception x) {
		    // x.printStackTrace();
		    jenaModel.add(rdfDataType, RDFS.subClassOf, XSD.xstring);
		}
		return;
	    } catch (Exception x) {
		x.printStackTrace();
	    }// fallback to string
	} else if (item.getType().equals(OT.OTClass.ModelConfidenceFeature)) {
	    feature.addOntClass(OT.OTClass.ModelConfidenceFeature.getOntClass(jenaModel));
	    if (!object.startsWith("http"))
		object = String.format("%s%s", rootReference, object);
	}
	feature.addProperty(predicate, object);

    }

    @Override
    public void open() throws DbAmbitException {
    }
}
