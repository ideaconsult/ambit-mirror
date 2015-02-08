package ambit2.rest.model;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.rdf.RDFPropertyIterator;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * RDF output for Models
 * @author nina
 *
 * @param <Q>
 */
public class ModelRDFReporter<Q extends IQueryRetrieval<ModelQueryResults>> extends QueryRDFReporter<ModelQueryResults, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7230106201606494227L;
	protected PropertyURIReporter propertyReporter;
	public ModelRDFReporter(Request request, MediaType mediaType) {
		super(request,mediaType);
		propertyReporter = new PropertyURIReporter(request);
	}
	@Override
	protected QueryURIReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> createURIReporter(
			Request req,ResourceDoc doc) {
		return new ModelURIReporter(req);
	}
	public void header(com.hp.hpl.jena.ontology.OntModel output, Q query) {
		super.header(output,query);
		OT.OTClass.Model.createOntClass(getJenaModel());
		OT.OTClass.Algorithm.createOntClass(getJenaModel());
		OT.OTClass.Feature.createOntClass(getJenaModel());
		OT.OTClass.Parameter.createOntClass(getJenaModel());
		OT.OTClass.Dataset.createOntClass(getJenaModel());
		
		output.createAnnotationProperty(DC.title.getURI());
		output.createAnnotationProperty(DC.description.getURI());
		output.createAnnotationProperty(DC.type.getURI());
		output.createAnnotationProperty(DC.creator.getURI());
		output.createAnnotationProperty(DC.format.getURI());
		output.createAnnotationProperty(DC.date.getURI());
	};
	
	@Override
	public Object processItem(ModelQueryResults item) throws AmbitException {
		Individual model = getJenaModel().createIndividual(uriReporter.getURI(item),
				OT.OTClass.Model.getOntClass(getJenaModel()));
		model.addProperty(DC.title, item.getName());

		//model.addProperty(DC.creator,"N/A");
		//model.addProperty(DC.date,"N/A");
		//model.addProperty(DC.format,"N/A");
		
		Individual algorithm = getJenaModel().createIndividual(item.getAlgorithm(),
				OT.OTClass.Algorithm.getOntClass(getJenaModel()));
		model.addProperty(OT.OTProperty.algorithm.createProperty(getJenaModel()), algorithm);
		
		if (item.getTrainingInstances()!=null) {
			Individual dataset = getJenaModel().createIndividual(item.getTrainingInstances(),
					OT.OTClass.Dataset.getOntClass(getJenaModel()));
			model.addProperty(OT.OTProperty.trainingDataset.createProperty(getJenaModel()), dataset);
		}
		
		readProperties(new Reference(String.format("%s/independent",uriReporter.getURI(item))), 
					OT.OTProperty.independentVariables.createProperty(getJenaModel()), model);
		readProperties(new Reference(String.format("%s/dependent",uriReporter.getURI(item))), 
					OT.OTProperty.dependentVariables.createProperty(getJenaModel()), model);
		readProperties(new Reference(String.format("%s/predicted",uriReporter.getURI(item))), 
					OT.OTProperty.predictedVariables.createProperty(getJenaModel()), model,OT.OTClass.ModelPredictionFeature);
		
		/*
		Template t = item.getPredictors();
		Iterator<Property> i = t.getProperties(true);
		while (i.hasNext()) {
			Property p = i.next();
			Individual feature = getJenaModel().createIndividual(propertyReporter.getURI(p),
					OT.OTClass.Feature.getOntClass(getJenaModel()));
			model.addProperty(OT.OTProperty.independentVariables.createProperty(getJenaModel()), feature);
		}
		
		t = item.getDependent();
		i = t.getProperties(true);
		while (i.hasNext()) {
			Property p = i.next();
			Individual feature = getJenaModel().createIndividual(propertyReporter.getURI(p),
					OT.OTClass.Feature.getOntClass(getJenaModel()));
			model.addProperty(OT.OTProperty.dependentVariables.createProperty(getJenaModel()), feature);
		}		
		
		t = item.getPredicted();
		i = t.getProperties(true);
		while (i.hasNext()) {
			Property p = i.next();
			Individual feature = getJenaModel().createIndividual(propertyReporter.getURI(p),
					OT.OTClass.Feature.getOntClass(getJenaModel()));
			model.addProperty(OT.OTProperty.predictedVariables.createProperty(getJenaModel()), feature);
		}			
		*/
		return model;
	}
	protected void readProperties(Reference reference, com.hp.hpl.jena.rdf.model.Property predicate, Individual model) {
		readProperties(reference, predicate, model,OT.OTClass.Feature);
	}
	protected void readProperties(Reference reference, com.hp.hpl.jena.rdf.model.Property predicate, Individual model, OT.OTClass featureclass) {
		RDFPropertyIterator reader = null;
		try {
			reader = new RDFPropertyIterator(reference);
			reader.setBaseReference(uriReporter.getBaseReference());
			while (reader.hasNext()) {
				Property property = reader.next();
				Individual feature = getJenaModel().createIndividual(propertyReporter.getURI(property),
						featureclass.getOntClass(getJenaModel()));
				model.addProperty(predicate, feature);		
				feature.addOntClass(OT.OTClass.Feature.getOntClass(getJenaModel()));
			}

		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		} finally {
			try {reader.close(); } catch (Exception x) {}
		}
	}
	public void open() throws DbAmbitException {
	}

}
