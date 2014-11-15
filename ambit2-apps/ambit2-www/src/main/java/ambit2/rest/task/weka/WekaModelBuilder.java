package ambit2.rest.task.weka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.rdf.ns.OT;

import org.apache.xerces.impl.dv.util.Base64;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.attributeSelection.PrincipalComponents;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.clusterers.Clusterer;
import weka.core.Instances;
import weka.core.WekaException;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.IEvaluation;
import ambit2.core.data.model.IEvaluation.EVStatsType;
import ambit2.core.data.model.IEvaluation.EVType;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.model.evaluation.EvaluationStats;
import ambit2.rest.OpenTox;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.ModelBuilder;

/**
 * Builds WEKA model from ModelQueryResults. Input variables and (optionally) target variables are required.
 * @author nina
 *
 */
public class WekaModelBuilder extends ModelBuilder<Instances,Algorithm, ModelQueryResults> {
	protected Clusterer clusterer = null;
	protected Classifier classifier = null;
	protected PrincipalComponents pca = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4372904815135663667L;
	public WekaModelBuilder(Reference applicationRootReference,
			ModelURIReporter model_reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI,
			String[] parameters) {
		super(applicationRootReference,model_reporter,alg_reporter);
		this.targetURI = targetURI;
		this.parameters = parameters;

	}
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {

		Instances instances = trainingData;
		if ((instances==null) || (instances.numInstances()==0) || (instances.numAttributes()==0)) 
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty dataset!");
		
		Object weka = null;
		try {
			Class clazz = this.getClass().getClassLoader().loadClass(algorithm.getContent().toString());
			weka = clazz.newInstance();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		
		clusterer = null; classifier = null; pca = null;
		if (weka instanceof Clusterer) clusterer = (Clusterer) weka;
		else if (weka instanceof Classifier) {
			classifier = (Classifier) weka;
			if (targetURI == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No target variable! "+OpenTox.params.target);
		} else if (weka instanceof PrincipalComponents) {
			pca = (PrincipalComponents) weka;
		}
		else throw new AmbitException(String.format("Unknown algorithm %s",algorithm.toString()));

		
		String[] prm = algorithm.getParametersAsArray();
		if (prm!=null)
		try {
			if (classifier!= null) classifier.setOptions(prm);
			else if (pca != null)  pca.setOptions(prm);
			else if (clusterer != null) {
					clusterer.getClass().getMethod(
			                "setOptions",
			                new Class[] {}).
			        invoke(clusterer, prm);					
			}
		} catch (Exception x) {
			Context.getCurrentLogger().warning("Error setting algorithm parameters, assuming defaults" + x.getMessage());
		}	
		
		

		//remove firstCompoundID attribute
		String[] options = new String[2];
		options[0] = "-R";                                   
		options[1] = "1";                                   
		Remove remove = new Remove();          
		try {
			remove.setOptions(options);                          
			remove.setInputFormat(instances);
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		 
		/*
        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters(new Filter[] {
        		remove
        //        new ReplaceMissingValues()
                });
        multiFilter.setInputFormat(instances);
        		*/
		//Filter filter = new RemoveUseless();
		//filter.setInputFormat(instances);
		Instances newInstances = null;
		try {
	        newInstances = Filter.useFilter(instances, remove);
	        //Filter filter = new RemoveUseless();
	        //filter.setInputFormat(newInstances);
	       // newInstances = Filter.useFilter(newInstances, filter);
	       // filter = new ReplaceMissingValues();
	       // filter.setInputFormat(newInstances);
	       // newInstances = Filter.useFilter(newInstances, filter);    
		} catch (Exception x) {
			throw new AmbitException(x);
		}
        
		String name = String.format("%s.%s",UUID.randomUUID().toString(),weka.getClass().getName());
		ModelQueryResults m = new ModelQueryResults();
		m.setParameters(parameters);
		m.setId(null);
		m.setContentMediaType(AlgorithmFormat.WEKA.getMediaType());
		m.setName(name);
		m.setAlgorithm(alg_reporter.getURI(algorithm));

		
		AlgorithmURIReporter r = new AlgorithmURIReporter();
		LiteratureEntry entry = new LiteratureEntry(name,
					algorithm==null?weka.getClass().getName():
					r.getURI(applicationRootReference.toString(),algorithm));

		LiteratureEntry prediction = new LiteratureEntry(m.getName(),
				model_reporter.getURI(applicationRootReference.toString(),m));		
		prediction.setType(_type.Model);
		
		Template predictors = null;
		Template dependent = null;
		PredictedVarsTemplate predicted = null;
		
		if (clusterer!= null) {
			try {
				clusterer.buildClusterer(newInstances);
			} catch (Exception x) {
				throw new AmbitException(x);
			}
			predicted = new PredictedVarsTemplate(name+"#Predicted");
			Property property = new Property("Cluster",prediction);
			property.setNominal(true);
			predicted.add(property);

			dependent = new Template("Empty");
			
			predictors = new Template(name+"#Independent");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				property = createPropertyFromReference(new Reference(newInstances.attribute(i).name()), entry);
				property.setOrder(i+1);
				predictors.add(property);
			}				
		} else if (classifier != null) {
			
			for (String t : targetURI) 
				for (int i = 0; i< newInstances.numAttributes(); i++)
					if (newInstances.attribute(i).name().equals(t)) {
						newInstances.setClassIndex(i);
						break;
					}
			try {
				//if (classifier instanceof LinearRegression) //don't do feature selection!
					//classifier.setOptions(new String[] {"-S","1"});
				StringBuilder evaluationString = new StringBuilder();
				EvaluationStats<String> stats = new EvaluationStats<String>(EVType.crossvalidation,null);
				Evaluation eval = new Evaluation(newInstances);
				if (newInstances.numInstances()>20) {
					eval.crossValidateModel(classifier, newInstances, 10, new Random(1));
					evaluationString.append("Crossvalidation 10 folds\n");
				} else {
					eval.crossValidateModel(classifier, newInstances, 2, new Random(1));
					evaluationString.append("Crossvalidation 2 folds\n");
				}	
				try {
					evaluationString.append(eval.toSummaryString());
					evaluationString.append("\n");
				} catch (Exception x) {}
				try {
					evaluationString.append(eval.toClassDetailsString());
					evaluationString.append("\n");
					evaluationString.append(eval.toMatrixString());
					evaluationString.append("\n");
				} catch (Exception x) {}
				try {
					evaluationString.append(eval.weightedAreaUnderROC());
				} catch (Exception x) {}
				try {
					stats.setMAE(eval.meanAbsoluteError());
				} catch (Exception x) {}
				try {
					stats.setRMSE(eval.rootMeanSquaredError());
				} catch (Exception x) {}
				try {
					stats.setPctCorrect(eval.pctCorrect());
					stats.setPctInCorrect(eval.pctIncorrect());
				} catch (Exception x) {}							
				stats.setContent(evaluationString.toString());
				m.addEvaluation(stats);
				
				stats = new EvaluationStats<String>(EVType.evaluation_training,null);
				evaluationString = new StringBuilder();
				classifier.buildClassifier(newInstances);
				eval = new Evaluation(newInstances);
				eval.evaluateModel(classifier,newInstances);
				try {
					evaluationString.append("\nTraining dataset statistics\n");
					evaluationString.append(eval.toSummaryString());
					evaluationString.append("\n");
				} catch (Exception x) {}				
				try {
					evaluationString.append(eval.toMatrixString());
					evaluationString.append("\n");
				} catch (Exception x) {	}
				try {
					stats.setMAE(eval.meanAbsoluteError());
				} catch (Exception x) {}
				try {
					stats.setRMSE(eval.rootMeanSquaredError());
				} catch (Exception x) {}
				try {
					stats.setPctCorrect(eval.pctCorrect());
					stats.setPctInCorrect(eval.pctIncorrect());
				} catch (Exception x) {}
				stats.setContent(evaluationString.toString());
				m.addEvaluation(stats);
			} catch (WekaException x) {
				throw new AmbitException(x);
			} catch (Exception x) {
				throw new AmbitException(x);
			}			
			
			;
			dependent = new Template(name+"#Dependent");
			Property property = createPropertyFromReference(new Reference(newInstances.attribute(newInstances.classIndex()).name()), entry); 
			dependent.add(property);
			
			predicted = new PredictedVarsTemplate(name+"#Predicted");
			Property predictedProperty = new Property(property.getName(),prediction); 
			predictedProperty.setLabel(property.getLabel());
			predictedProperty.setUnits(property.getUnits());
			predictedProperty.setClazz(property.getClazz());
			predictedProperty.setNominal(property.isNominal());
			predicted.add(predictedProperty);
			predictedProperty.setEnabled(true);
			
			if (supportsDistribution(classifier)) {
				Property confidenceProperty = new Property(String.format("%s Confidence",property.getName()),prediction); 
				confidenceProperty.setLabel(Property.opentox_ConfidenceFeature);
				confidenceProperty.setUnits("");
				confidenceProperty.setClazz(Number.class);
				confidenceProperty.setEnabled(true);
				PropertyAnnotation<Property> a = new PropertyAnnotation<Property>();
				a.setType(OT.OTClass.ModelConfidenceFeature.name());
				a.setPredicate(OT.OTProperty.confidenceOf.name());
				a.setObject(predictedProperty);
				PropertyAnnotations aa = new PropertyAnnotations();
				aa.add(a);
				confidenceProperty.setAnnotations(aa);
				predicted.add(confidenceProperty);
			}
			
			predictors = new Template(name+"#Independent");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				if (newInstances.classIndex()==i) continue;
				property = createPropertyFromReference(new Reference(newInstances.attribute(i).name()), entry);
				property.setOrder(i+1);
				predictors.add(property);
			}				
		} else if (pca != null) {
			
			try {
				pca.setVarianceCovered(1.0);
				pca.buildEvaluator(newInstances);
			} catch (Exception x) {
				throw new AmbitException(x);
			}
			
			Property property; 
			
			dependent = new Template("Empty");
			
			predictors = new Template(name+"#Independent");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				if (newInstances.classIndex()==i) continue;
				property = createPropertyFromReference(new Reference(newInstances.attribute(i).name()), entry);
				property.setOrder(i+1);
				predictors.add(property);
			}	

			predicted = new PredictedVarsTemplate(name+"#Predicted");
			for (int i=0; i < newInstances.numAttributes(); i++) {
				if (newInstances.classIndex()==i) continue;
				property = createPropertyFromReference(new Reference(String.format("PCA_%d",i+1)), entry);
				property.setClazz(Number.class);
				property.setOrder(i+1);
				predicted.add(property);
			}			
		}


		m.setPredictors(predictors);
		m.setDependent(dependent);
		m.setPredicted(predicted);
		
		try {
			serializeModel(clusterer==null?classifier==null?pca:classifier:clusterer, newInstances, m);
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		return m;
	}
	/**
	 * TODO - verify via reflection, not fixed check
	 * @param classifier
	 * @return true if supports distributionForInstance method
	 */
	protected boolean supportsDistribution(Classifier classifier) {
		return classifier instanceof J48;
	}
	protected void serializeModel(Object predictor, Instances newInstances, ModelQueryResults m) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		 // serialize model
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(predictor);
		oos.flush();
		oos.close();	
		
		byte[] content = out.toByteArray();
		
		Form form = new Form();
		
		form.add("model", Base64.encode(content));
		newInstances.delete();
		if (newInstances.classIndex()>=0)
			form.add("classIndex",Integer.toString(newInstances.classIndex()));		
		newInstances.delete();
		form.add("header", newInstances.toString());
		
		if (m.getEvaluation()!=null) 
			for (IEvaluation stats : m.getEvaluation()) {
				form.add(stats.getType().name(),stats.getContent().toString());
				if (stats instanceof EvaluationStats) 
					for (EVStatsType st : EVStatsType.values()) try {
						Object val = ((EvaluationStats)stats).getStats().get(st);
						if (val==null) continue;
						form.add(stats.getType().name()+"_"+st.name(),
								String.format(Locale.ENGLISH,"%6.3f",(Double)val));
					} catch (Exception x) {logger.warning(x.getMessage());}
			}
		m.setContent(form.getWebRepresentation().getText());
	}
	
	
}
