package ambit2.rest.task.waffles;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.core.Attribute;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.model.Algorithm;
import ambit2.db.model.ModelQueryResults;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.ModelBuilder;
import ambit2.waffles.ShellWafflesLearn;
import ambit2.waffles.ShellWafflesLearn.WafflesLearnOption;
import ambit2.waffles.learn.options.WafflesLearnCommand;

/**
 * Builds waffles model
 * @author nina
 *
 */
public class WafflesModelBuilder  extends ModelBuilder<File,Algorithm, ModelQueryResults> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2430059881213107595L;
	protected String[] parameters = null;	
	protected String dataOptions ;
	
	protected List<Attribute> predictors_URI;
	public List<Attribute> getPredictors_URI() {
		return predictors_URI;
	}

	public void setPredictors_URI(List<Attribute> predictors_URI) {
		this.predictors_URI = predictors_URI;
	}

	public List<Attribute> getDependent_URI() {
		return dependent_URI;
	}

	public void setDependent_URI(List<Attribute> dependent_URI) {
		this.dependent_URI = dependent_URI;
	}

	public List<Attribute> getPredicted_URI() {
		return predicted_URI;
	}

	public void setPredicted_URI(List<Attribute> predicted_URI) {
		this.predicted_URI = predicted_URI;
	}

	protected List<Attribute> dependent_URI;
	protected List<Attribute> predicted_URI;
	
	public String getDataOptions() {
		return dataOptions;
	}

	public void setDataOptions(String dataOptions) {
		this.dataOptions = dataOptions;
	}

	public WafflesModelBuilder(Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI,
			String[] parameters) {
		super(applicationRootReference,model_reporter,alg_reporter);
		this.targetURI = targetURI;
		this.parameters = parameters;

	}

	@Override
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {
		ModelQueryResults model = new ModelQueryResults();
		
		File dataset = trainingData;
		if ((dataset==null) || !dataset.exists()) 
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty dataset!");
		
		ShellWafflesLearn waffles = null;
		try {
			Class clazz = this.getClass().getClassLoader().loadClass(algorithm.getContent().toString());
			waffles = (ShellWafflesLearn) clazz.newInstance();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,algorithm.getContent().toString(),x);
		}
		//return the model representation as string, not file, so we store it into the DB
		waffles.setOutputFile(null);
		waffles.setOutProperty(WafflesLearnOption.model.name());
		Properties in = new Properties();
		in.put(WafflesLearnOption.command.name(), WafflesLearnCommand.train.name());
		if ((parameters!=null) && (parameters.length>0))
				in.put(WafflesLearnOption.alg_opts.name(), parameters[0]);
		in.put(WafflesLearnOption.dataset.name(), dataset.getAbsolutePath());
		in.put(WafflesLearnOption.algorithm.name(), waffles.getAlgorithm().name());
		if (dataOptions!=null)
			in.put(WafflesLearnOption.data_opts.name(), dataOptions);
		try {
			Properties out = waffles.runShell(in);
			if ((out==null) || (out.getProperty(waffles.getOutProperty())==null))
				throw new AmbitException(String.format("Model not generated",waffles.getOutProperty()));
			if (!"0".equals(out.getProperty(waffles.getExitcodeProperty())))
				throw new AmbitException(String.format("Model not generated [exit code = %s] %s",out.getProperty(waffles.getExitcodeProperty()),out.getProperty(waffles.getOutProperty())));
			
			String name= String.format("%s.%s.%s",waffles.getAlgorithm().name(),UUID.randomUUID().toString(),waffles.getClass().getName());
			
			model.setContent(out.getProperty(waffles.getOutProperty()));
			model.setContentMediaType(algorithm.getFormat().getMediaType());
			model.setParameters(parameters);
			model.setId(null);
			model.setName(name);
			model.setAlgorithm(alg_reporter.getURI(algorithm));
			
			/**
			 * Variables
			 */
			AlgorithmURIReporter r = new AlgorithmURIReporter();
			LiteratureEntry entry = new LiteratureEntry(name,
						algorithm==null?waffles.getClass().getName():
						r.getURI(applicationRootReference.toString(),algorithm));

			LiteratureEntry prediction = new LiteratureEntry(model.getName(),
					model_reporter.getURI(applicationRootReference.toString(),model));		
			prediction.setType(_type.Model);
			
			Template predictors = null;
			Template dependent = null;
			PredictedVarsTemplate predicted = null;
			
			dependent = new Template(name+"#Dependent");
			predicted = new PredictedVarsTemplate(name+"#Predicted");
			for (int i=0; i < dependent_URI.size(); i++) {
				//dependent var
				Property property = createPropertyFromReference(new Reference(dependent_URI.get(i).name()), entry);
				property.setOrder(i+1);
				dependent.add(property);
				//and predicted
				Property predictedProperty = new Property(property.getName(),prediction); 
				predictedProperty.setOrder(i+1);
				predictedProperty.setLabel(property.getLabel());
				predictedProperty.setUnits(property.getUnits());
				predictedProperty.setClazz(property.getClazz());
				predictedProperty.setNominal(property.isNominal());
				predictedProperty.setEnabled(true);
				predicted.add(predictedProperty);
				
				if (dependent_URI.get(i).isNominal()) {
					Enumeration e = dependent_URI.get(i).enumerateValues();
					PropertyAnnotations annotations = new PropertyAnnotations();
					while (e.hasMoreElements()) {
						String value = e.nextElement().toString();
						predictedProperty.addAllowedValue(value);
						PropertyAnnotation annotation = new PropertyAnnotation();
						annotation.setObject(value);
						annotation.setPredicate("acceptValue");
						annotation.setType("Feature");
						annotations.add(annotation);
					}
					predictedProperty.setAnnotations(annotations);
				}
			}	
			
			predictors = new Template(name+"#Independent");
			for (int i=0; i < predictors_URI.size(); i++) {
				Property property = createPropertyFromReference(new Reference(predictors_URI.get(i).name()), entry);
				property.setOrder(i+1);
				predictors.add(property);
			}	

			//if (supportsDistribution(classifier)) {}
				
			
			model.setPredictors(predictors);
			model.setDependent(dependent);
			model.setPredicted(predicted);
			return model;
			
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
}
