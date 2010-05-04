package ambit2.rest.model.predictor;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.rest.model.ModelURIReporter;



/**
 * Wrapper for weka models
 * @author nina
 *
 */
public class WekaPredictor<T> extends ModelPredictor<T,Instance> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1394180227835161276L;
	protected Classifier classifier;
	protected Clusterer clusterer;
	
	public WekaPredictor(Reference applicationRootReference,ModelQueryResults model, ModelURIReporter modelReporter) {
		this(applicationRootReference,model,modelReporter,null);
	}
	public WekaPredictor(Reference applicationRootReference,ModelQueryResults model, ModelURIReporter modelReporter, String[] targetURI) {
		super(applicationRootReference,model,modelReporter,null,targetURI);
	}
	public Classifier getClassifier() {
		return classifier;
	}
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}
	
	public Clusterer getClusterer() {
		return clusterer;
	}
	public void setClusterer(Clusterer clusterer) {
		this.clusterer = clusterer;
	}
	
	@Override
	protected boolean isSupported(Object predictor) throws ResourceException {
		if (predictor instanceof Classifier) {
			classifier = (Classifier) predictor;
			return true;
		} else if  (predictor instanceof Clusterer) {
			clusterer = (Clusterer) predictor;
			return true;
		} return false;
	}
	
	protected Instance predictionInstance(Instance target) throws AmbitException {
		Instance n = new Instance(header.numAttributes());
		for (int i=0; i < header.numAttributes();i++) {
			Attribute attr = target.dataset().attribute(header.attribute(i).name());
			if (attr != null) {
				if (attr.isNominal())
					n.setValue(header.attribute(i),target.stringValue(attr));
				else if (attr.isString())
					n.setValue(header.attribute(i),target.stringValue(attr));						
				else if (attr.isNumeric())
					n.setValue(header.attribute(i),target.value(attr));
				else if (attr.isDate())
					n.setValue(header.attribute(i),target.stringValue(attr));					
			}
		}
		return n;
	}
	@Override
	public String getCompoundURL(Instance target) throws AmbitException {
		return target.stringValue(0);
	}
	@Override
	public Object predict(Instance target) throws AmbitException {

		Instances testInstances = target.dataset();
		try {
			Object value = null;
			//create new instance
			header.add(predictionInstance(target));
			
			if (classIndex>=0) header.setClassIndex(classIndex);
			
	        filter = new ReplaceMissingValues();
	        filter.setInputFormat(header);
	        Instances newInstances = Filter.useFilter(header, filter);   		
	        
			for (int i=0; i < newInstances.numInstances();i++) {
				target = newInstances.instance(i);
				
				if (classifier != null) {
					value = classifier.classifyInstance(target);
					if (newInstances.classAttribute().isNominal())
						value = newInstances.classAttribute().value(((Double)value).intValue());
				} else if (clusterer != null) {
					value = clusterer.clusterInstance(target);
				} else throw new AmbitException();
				
			}
			
			return value;
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			header.delete();
			testInstances.delete();
		}
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("\n-- WEKA Model --\n");
		b.append(super.toString());

		return b.toString();
				
	}	
	
}