package ambit2.rest.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;

import org.restlet.data.Status;
import org.restlet.engine.util.Base64;
import org.restlet.resource.ResourceException;

import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.model.ModelQueryResults;
import ambit2.rest.OpenTox;



/**
 * Wrapper for weka models
 * @author nina
 *
 */
public class WekaPredictor extends DefaultAmbitProcessor<Instance,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1394180227835161276L;
	protected Classifier classifier;
	protected Clusterer clusterer;
	protected ModelURIReporter modelReporter;
	protected IStructureRecord record = new StructureRecord();
	protected ModelQueryResults model;
	protected Filter filter;
	protected String[] targetURI;
	
	public WekaPredictor(ModelURIReporter modelReporter) {
		this(modelReporter,null);
	}
	public WekaPredictor(ModelURIReporter modelReporter, String[] targetURI) {
		super();
		this.modelReporter = modelReporter;
		this.targetURI = targetURI;
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
	public void setWekaModel(ModelQueryResults model) throws ResourceException {
		 this.model = model;
		 ObjectInputStream ois = null;
		 try {
			InputStream in = new ByteArrayInputStream(Base64.decode(model.getContent()));
			ois =  new ObjectInputStream(in);
			clusterer = null; classifier = null;
		 	Object weka = ois.readObject();
		 	if (weka instanceof Clusterer) {
		 		clusterer = (Clusterer)weka;
		 	}
		 	else if (weka instanceof Classifier) {
		 		classifier = (Classifier)weka;
		 		
		 	}
		 	else throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE,weka.getClass().getName());
		 	
		 	String[] options = new String[2];
			options[0] = "-R";                                   
			options[1] = "1";                                   
			Remove remove  = new Remove();   
			try {                    
			remove.setOptions(options);                          
			} catch (Exception x) {};
		 	filter = remove;
		//Filter filter = new RemoveUseless();
		//filter.setInputFormat(instances);
		
        
		 	
		 } catch (IOException x) { 
			 throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x.getMessage(),x);
		 } catch (ClassNotFoundException x) {
			 throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE,x.getMessage(),x);
		 } finally {
			 try {ois.close();} catch (Exception x) {} 
		 }
	}
	
	public IStructureRecord process(Instance target) throws AmbitException {
		Instances instances = target.dataset();
		try {
			Object value;
			record.clear();
			Object id = OpenTox.URI.compound.getId(target.stringValue(0), modelReporter.getBaseReference());
			if (id != null) record.setIdchemical((Integer)id);
			else {
				Object[] ids = OpenTox.URI.conformer.getIds(target.stringValue(0), modelReporter.getBaseReference());
				if (ids[0]!=null) record.setIdchemical((Integer)ids[0]);
				if (ids[1]!=null) record.setIdstructure((Integer)ids[1]);
			}
			
			filter.setInputFormat(instances);
			Instances newInstances = Filter.useFilter(instances, filter);	
			
			for (String t : targetURI) 
				for (int i = 0; i< newInstances.numAttributes(); i++)
					if (newInstances.attribute(i).name().equals(t)) {
						newInstances.setClassIndex(i);
						break;
					}
			
			for (int i=0; i < newInstances.numInstances();i++) {
				target = newInstances.instance(i);
				if (classifier != null) {
					value = classifier.classifyInstance(target);
				} else if (clusterer != null) {
					value = clusterer.clusterInstance(target);
				} else throw new AmbitException();
				
				Iterator<Property> predicted = model.getDependent().getProperties(true);
				while (predicted.hasNext()) {
					record.setProperty(predicted.next(),value);
				}
			}
			
			return record;
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			instances.delete();
		}
	}
}