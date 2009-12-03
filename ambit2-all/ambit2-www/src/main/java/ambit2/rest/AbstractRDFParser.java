package ambit2.rest;

import java.io.InputStream;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * RDF parsing
 * @author nina
 *
 * @param <T>
 */
public abstract class AbstractRDFParser<T> extends DefaultAmbitProcessor<InputStream, T>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3213089443392677346L;
	public T process(InputStream in) throws AmbitException {
		try {
			OntModel jenaModel = OT.createModel();
			jenaModel.read(in, null);
			test(jenaModel);
			return read(jenaModel);
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public abstract T read(OntModel model)  throws AmbitException;
	
	public void test(OntModel jenaModel)  throws AmbitException {
		OntClass c = OT.OTClass.Feature.getOntClass(jenaModel);
		ExtendedIterator<? extends OntResource> features = c.listInstances();
		if (features!=null)
		while (features.hasNext()) {
			OntResource feature = features.next();
			System.out.println(feature);
			System.out.println(feature.getProperty(DC.title));
			System.out.println(feature.getProperty(DC.identifier));
			System.out.println(feature.getProperty(OT.units));
			System.out.println(feature.getProperty(OT.hasSource));
			System.out.println(feature.getProperty(OWL.sameAs));
		
		}
		
		OntClass modelClass = OT.OTClass.Model.getOntClass(jenaModel);
		ExtendedIterator<? extends OntResource> models = modelClass.listInstances();
		if (models!=null)
		while (models.hasNext()) {
			OntResource model = models.next();
			System.out.println(model);
			System.out.println(model.getProperty(DC.title));
			System.out.println(model.getProperty(DC.identifier));
			System.out.println(model.getProperty(DC.creator));
			System.out.println(model.getProperty(DC.date));
			System.out.println(model.getProperty(OT.algorithm));
			System.out.println(model.getProperty(OT.independentVariables));
			System.out.println(model.getProperty(OT.dependentVariables));
			System.out.println(model.getProperty(OT.predictedVariables));
			System.out.println(model.getProperty(OT.trainingDataset));
			Statement st = model.getProperty(OT.algorithm);
			System.out.println(st.getObject());
		
		}		
	}

}
