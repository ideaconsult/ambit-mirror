package ambit2.rest.task.weka;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import weka.attributeSelection.PrincipalComponents;
import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.predictor.ModelPredictor;



/**
 * Wrapper for weka models
 * @author nina
 *
 */
public class FilteredWekaPredictor<T> extends ModelPredictor<T,Instance> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1394180227835161276L;
	protected Classifier classifier;
	protected Clusterer clusterer;
	protected PrincipalComponents pca;
	
	public PrincipalComponents getPca() {
		return pca;
	}
	public void setPca(PrincipalComponents pca) {
		this.pca = pca;
	}
	public FilteredWekaPredictor(Reference applicationRootReference,ModelQueryResults model, ModelURIReporter modelReporter) {
		this(applicationRootReference,model,modelReporter,null);
	}
	public FilteredWekaPredictor(Reference applicationRootReference,ModelQueryResults model, ModelURIReporter modelReporter, String[] targetURI) {
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
		} else if (predictor instanceof PrincipalComponents) {
			pca = (PrincipalComponents) predictor;
			return true;
		} return false;
	}
	
	protected Instance predictionInstance(Instance target) throws AmbitException {
		Instance n = new SparseInstance(header.numAttributes());
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
			Object[] value = new Object[] {null,null}; //value itself (Instance) + confidence

			//create new instance
			header.add(predictionInstance(target));
			
			if (classIndex>=0) header.setClassIndex(classIndex);
			
	        //Instances newInstances = Filter.useFilter(header, filter);
			Instances newInstances = header;
	        
			for (int i=0; i < newInstances.numInstances();i++) {
				target = newInstances.instance(i);
				
				if (classifier != null) {
					double result = classifier.classifyInstance(target);
					if (newInstances.classAttribute().isNominal()) {
						value[0] = newInstances.classAttribute().value((int) result);
						try {
							value[1] = classifier.distributionForInstance(target)[(int) result];
						} catch (Exception x) {
							value[1] = null;
						}
					} else {
						value[0] = result;
						value[1] = null;
					}
				} else if (clusterer != null) {
					value[0] = clusterer.clusterInstance(target);
					
				} else if (pca != null) {
					
					value[0] = pca.convertInstance(target);
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
	public void assignResults(IStructureRecord record, Object value)
			throws AmbitException {
		Object[] values = (Object[]) value;
		if (values[0] instanceof Instance)  {
			if (values[1] != null)
				assignTransformedInstance(record, (Instance) values[0], (Double) values[1]);
			else
				assignTransformedInstance(record, (Instance) values[0], 1);
		}
		else {
			
			Iterator<Property> predicted = model.getPredicted().getProperties(true);
			int count = 0;
			while (predicted.hasNext()) {
				Property property = predicted.next();
				if (Property.opentox_ConfidenceFeature.equals(property.getLabel())) {
					try { 
						record.setProperty(property,values[1]);
					} catch (Exception x) { record.setProperty(property,"NA");}
				} else {
					record.setProperty(property,values[0]);
					count++;
				}
			}
			if (count==0) throw new AmbitException("No property to assign results!!!");
		}
		
	}	
	public void assignTransformedInstance(IStructureRecord record, Instance value, double confidence) throws AmbitException {
		Iterator<Property> predicted = model.getPredicted().getProperties(true);
		int count = 0;
		while (predicted.hasNext()) {
			Property property = predicted.next();
			if (Property.opentox_ConfidenceFeature.equals(property.getLabel())) {
				try { 
					record.setProperty(property,confidence);
				} catch (Exception x) { record.setProperty(property,"NA");}
			} else {
				record.setProperty(property,value.value(count));
				count++;
			}
		}
		if (count==0) throw new AmbitException("No property to assign results!!!");
	}	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("\n-- WEKA Model --\n");
		b.append(super.toString());

		return b.toString();
				
	}	

	/**
Evaluation evaluation = new Evaluation( trainDataset );
evaluation.evaluateModel( classifier, testDataset );
for (int i = 0; i < evaluation.predictions(); i++) {
 NominalPrediction prediction = (NominalPrediction)
evaluation.predictions().elementAt(i);
 double[] distribution = prediction.distribution();
}
	 */
	@Override
	public BufferedImage getLegend(int width, int height) throws AmbitException {
		String msg = (classifier!=null?classifier.toString():clusterer!=null?clusterer.toString():"").trim();
		try {
			String[] m = (msg.split("\n")[0]).split(" ");
			String[] t = new String[m.length+1];
			for (int i=0; i < m.length;i++) t[i+1]= m[i];
			t[0] = "WEKA";
			return writeMessages(t, width, height);
		} catch (Exception x) {
			return writeMessages(new String[] {"WEKA"}, width, height);	
		}
		
	}
}

