package ambit2.rest.task.weka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import org.apache.xerces.impl.dv.util.Base64;
import org.opentox.rdf.OT;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.attributeSelection.PrincipalComponents;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.clusterers.Clusterer;
import weka.clusterers.FilteredClusterer;
import weka.core.Instances;
import weka.core.WekaException;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.IEvaluation;
import ambit2.core.data.model.IEvaluation.EVStatsType;
import ambit2.core.data.model.IEvaluation.EVType;
import ambit2.db.model.ModelQueryResults;
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
public class FilteredWekaModelBuilder extends ModelBuilder<Instances,Algorithm, ModelQueryResults> {
	protected FilteredClusterer fclusterer = null;
	protected FilteredClassifier fclassifier = null;
	protected PrincipalComponents pca = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4372904815135663667L;
	public FilteredWekaModelBuilder(Reference applicationRootReference,
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
		
		fclusterer = null; fclassifier = null; pca = null;
		if (weka instanceof Clusterer) {
			fclusterer = new FilteredClusterer();
			fclusterer.setClusterer((Clusterer) weka);
		}
		else if (weka instanceof Classifier) {
			fclassifier = new FilteredClassifier();
			fclassifier.setClassifier((Classifier) weka);
			if (targetURI == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No target variable! "+OpenTox.params.target);
		} else if (weka instanceof PrincipalComponents) {
			pca = (PrincipalComponents) weka;
		}
		else throw new AmbitException(String.format("Unknown algorithm %s",algorithm.toString()));

		
		String[] prm = algorithm.getParametersAsArray();
		if (prm!=null)
		try {
			if (fclassifier!= null) fclassifier.getClassifier().setOptions(prm);
			else if (pca != null)  pca.setOptions(prm);
			else if (fclusterer != null) {
					fclusterer.getClusterer().getClass().getMethod(
			                "setOptions",
			                new Class[] {}).
			        invoke(fclusterer.getClusterer(), prm);					
			}
		} catch (Exception x) {
			Context.getCurrentLogger().warning("Error setting algorithm parameters, assuming defaults" + x.getMessage());
		}	
		
		

		
		Filter filters[] = new Filter[2];
		MultiFilter filter = new MultiFilter();
		filter.setFilters(filters);
		
		//remove firstCompoundID attribute
		try {
			String[] options = new String[2];
			options[0] = "-R";options[1] = "1";      
			Remove remove = new Remove();     
			remove.setOptions(options);                          
			filters[0] = remove;
		} catch (Exception x) {throw new AmbitException(x);}
		
		try { //remove missing values
			ReplaceMissingValues replace = new ReplaceMissingValues();
			String[] options = new String[1];
			options[0] = "-M";
			replace.setOptions(options);                          
//			remove.setInputFormat(instances);
			filters[1] = replace;
		} catch (Exception x) {throw new AmbitException(x);}
		
		
		Instances newInstances = instances;
		if (fclassifier!=null) fclassifier.setFilter(filter);
		else if (fclusterer!=null) fclusterer.setFilter(filter);
		else {
			try {
		        newInstances = Filter.useFilter(instances, filter);
		        //Filter filter = new RemoveUseless();
		        //filter.setInputFormat(newInstances);
		       // newInstances = Filter.useFilter(newInstances, filter);
		       // filter = new ReplaceMissingValues();
		       // filter.setInputFormat(newInstances);
		       // newInstances = Filter.useFilter(newInstances, filter);    
			} catch (Exception x) {
				throw new AmbitException(x);
			}
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
		
		if (fclusterer!= null) {
			try {
				fclusterer.buildClusterer(newInstances);
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
		} else if (fclassifier != null) {
			
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
					eval.crossValidateModel(fclassifier, newInstances, 10, new Random(1));
					evaluationString.append("Crossvalidation 10 folds\n");
				} else {
					eval.crossValidateModel(fclassifier, newInstances, 2, new Random(1));
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
				fclassifier.buildClassifier(newInstances);
				eval = new Evaluation(newInstances);
				eval.evaluateModel(fclassifier,newInstances);
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
			
			if (supportsDistribution(fclassifier)) {
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
				if ("CompoundURI".equals(newInstances.attribute(i).name())) continue;
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
				if ("CompoundURI".equals(newInstances.attribute(i).name())) continue;
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
			serializeModel(fclusterer==null?fclassifier==null?pca:fclassifier:fclusterer, newInstances, m);
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
		return (classifier instanceof J48) || ((classifier instanceof FilteredClassifier)&& 
				(((FilteredClassifier)classifier).getClassifier() instanceof J48));
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
