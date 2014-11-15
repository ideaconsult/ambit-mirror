package ambit2.rest.property;

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

import ambit2.base.data.Dictionary;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.property.annotations.PropertyAnnotationRDFReporter;
import ambit2.rest.reference.ReferenceURIReporter;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Feature reporter
 * @author nina
 *
 * @param <Q>
 */
public class PropertyRDFReporter<Q extends IQueryRetrieval<Property>> extends QueryRDFReporter<Property, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected ReferenceURIReporter referenceReporter;
	public PropertyRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
		referenceReporter = new ReferenceURIReporter(request);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new PropertyURIReporter(reference);
	}
	public void header(com.hp.hpl.jena.ontology.OntModel output, Q query) {
		super.header(output, query);
		OT.OTClass.Feature.createOntClass(getJenaModel());
	
	}
	@Override
	public Object processItem(Property item) throws AmbitException {
		return addToModel(getJenaModel(), item, uriReporter,referenceReporter);

	}

	public static Individual addToModel(OntModel jenaModel,Property item, 
			QueryURIReporter<Property, IQueryRetrieval<Property>> uriReporter,
			ReferenceURIReporter referenceReporter
			) {
		Individual feature = null;
		OTClass featureType = OTClass.Feature;
		
		String id = uriReporter.getURI(item);
		if ((uriReporter==null) || (uriReporter.getBaseReference()==null) || (item.getId()<0)) {
			if (item.getClazz() == Dictionary.class) {
				feature = jenaModel.createIndividual(id,featureType.getOntClass(jenaModel));
			} else
				feature = jenaModel.createIndividual(featureType.getOntClass(jenaModel));
		} else {
			feature = jenaModel.createIndividual(id,featureType.getOntClass(jenaModel));
		}
		if (item.isNominal())
			feature.addOntClass(OTClass.NominalFeature.getOntClass(jenaModel));
		
		if (item.getClazz()==Number.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Double.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Float.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Integer.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Long.class) feature.addOntClass(OTClass.NumericFeature.getOntClass(jenaModel));
		else if (item.getClazz()==Dictionary.class) feature.addOntClass(OTClass.TupleFeature.getOntClass(jenaModel));
		
		if (item.getName()!=null) feature.addProperty(DC.title, item.getName());
		if (item.getUnits()!=null) feature.addProperty(OT.DataProperty.units.createProperty(jenaModel),item.getUnits());
		
		String uri = item.getLabel();
		if(uri==null) uri  = Property.guessLabel(item.getName());
		if ((uri!=null) && (uri.indexOf("http://")<0)  && (uri.indexOf("https://")<0)) {
			uri = String.format("%s%s",OT.NS,Reference.encode(uri));
		}
		feature.addProperty(OWL.sameAs,jenaModel.createResource(uri));
		
		//ot:hasSource  ; reference.title used as source URI, reference.url used as object type  -
		//somewhat awkward, but title is the unique field in the catalog_references table
		
		uri = item.getTitle();
		
		//catch all 
		if (uri.indexOf("/model/")>0) 
			feature.addOntClass(OT.OTClass.ModelPredictionFeature.getOntClass(jenaModel));	
		
		//drop using /reference objects
		if ((uri.indexOf("http://")<0) && (uri.indexOf("https://")<0)) {
			Individual source  = null;
		
			
			if (_type.Algorithm.equals(item.getReference().getType())) {
				uri = String.format("%s/algorithm/%s",uriReporter.getBaseReference(),Reference.encode(uri));
				source = jenaModel.createIndividual(uri,OT.OTClass.Algorithm.createOntClass(jenaModel));
				feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), source);
			} else if (_type.Model.equals(item.getReference().getType())) {
				uri = String.format("%s/model/%s",uriReporter.getBaseReference(),Reference.encode(uri));
				source = jenaModel.createIndividual(uri,OT.OTClass.Model.createOntClass(jenaModel));
				feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), source);
				feature.addOntClass(OT.OTClass.ModelPredictionFeature.getOntClass(jenaModel));
			} else if (_type.Feature.equals(item.getReference().getType())) {
				uri = String.format("%s/feature/%s",uriReporter.getBaseReference(),Reference.encode(uri));
				source = jenaModel.createIndividual(uri,OT.OTClass.Feature.createOntClass(jenaModel));
				feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), source);				
			} else if (_type.Dataset.equals(item.getReference().getType())) {
				//this seems to confuse everybody's else parsers ...
				uri = String.format("%s/dataset/%s",uriReporter.getBaseReference(),Reference.encode(uri));
				Resource src = jenaModel.createResource(uri);
				feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), src);
			} else {
				feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), uri);
			}
			feature.addProperty(DC.creator, item.getReference().getURL());

		}  else {
			feature.addProperty(OT.OTProperty.hasSource.createProperty(jenaModel), jenaModel.createResource(uri));
			feature.addProperty(DC.creator, item.getReference().getURL());
		}
		
		if (item.getAnnotations()!=null)
			for (PropertyAnnotation a : item.getAnnotations()) try {
				PropertyAnnotationRDFReporter.annotation2RDF(a, jenaModel, feature,uriReporter.getBaseReference().toString());
			} catch (Exception x) {
				x.printStackTrace();
			}

		return feature;
	}	
	
	public void open() throws DbAmbitException {
		
	}

	public static String hasSourceURI(Property item, QueryURIReporter<Property, IQueryRetrieval<Property>> uriReporter) {
		String uri = item.getTitle();
		if ((uri.indexOf("http://")<0) && (uri.indexOf("https://")<0)) {
			Individual source  = null;
			
			
			if (_type.Algorithm.equals(item.getReference().getType())) {
				return String.format("%s/algorithm/%s",uriReporter.getBaseReference(),Reference.encode(uri));
			} else if (_type.Model.equals(item.getReference().getType())) {
				return String.format("%s/model/%s",uriReporter.getBaseReference(),Reference.encode(uri));
			} else if (_type.Feature.equals(item.getReference().getType())) {
				return String.format("%s/feature/%s",uriReporter.getBaseReference(),Reference.encode(uri));
	
			} else if (_type.Dataset.equals(item.getReference().getType())) {
				//this seems to confuse everybody's else parsers ...
				return String.format("%s/dataset/%s",uriReporter.getBaseReference(),Reference.encode(uri));

			} else {
				return uri;
			}
		}  else {
			return uri;
		}
	}
}
