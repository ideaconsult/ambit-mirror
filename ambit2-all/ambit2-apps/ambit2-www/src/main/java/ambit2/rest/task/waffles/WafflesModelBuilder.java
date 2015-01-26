package ambit2.rest.task.waffles;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.apache.xerces.impl.dv.util.Base64;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.core.Instances;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.ModelBuilder;
import ambit2.waffles.ShellWafflesLearn;
import ambit2.waffles.ShellWafflesLearn.WafflesLearnOption;
import ambit2.waffles.learn.options.WafflesLearnCommand;

/**
 * Builds waffles model
 * 
 * @author nina
 * 
 */
public class WafflesModelBuilder extends ModelBuilder<File, Algorithm, ModelQueryResults> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2430059881213107595L;
    protected String dataOptions;
    protected Instances header;

    public Instances getHeader() {
	return header;
    }

    public void setHeader(Instances header) {
	this.header = header;
    }

    public String getDataOptions() {
	return dataOptions;
    }

    public void setDataOptions(String dataOptions) {
	this.dataOptions = dataOptions;
    }

    public WafflesModelBuilder(Reference applicationRootReference, ModelURIReporter model_reporter,
	    AlgorithmURIReporter alg_reporter, String[] targetURI, String[] parameters) {
	super(applicationRootReference, model_reporter, alg_reporter);
	this.targetURI = targetURI;
	this.parameters = parameters;

    }

    @Override
    public ModelQueryResults process(Algorithm algorithm) throws AmbitException {
	ModelQueryResults model = new ModelQueryResults();

	File dataset = trainingData;
	if ((dataset == null) || !dataset.exists())
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty dataset!");

	ShellWafflesLearn waffles = null;
	try {
	    Class clazz = this.getClass().getClassLoader().loadClass(algorithm.getContent().toString());
	    waffles = (ShellWafflesLearn) clazz.newInstance();
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, algorithm.getContent().toString(), x);
	}
	// return the model representation as string, not file, so we store it
	// into the DB
	waffles.setOutputFile(null);
	waffles.setOutProperty(WafflesLearnOption.model.name());
	Properties in = new Properties();
	in.put(WafflesLearnOption.command.name(), WafflesLearnCommand.train.name());
	if ((parameters != null) && (parameters.length > 0))
	    in.put(WafflesLearnOption.alg_opts.name(), parameters[0]);
	in.put(WafflesLearnOption.dataset.name(), dataset.getAbsolutePath());
	in.put(WafflesLearnOption.algorithm.name(), waffles.getAlgorithm().name());
	if (dataOptions != null)
	    in.put(WafflesLearnOption.data_opts.name(), dataOptions);
	try {
	    Properties out = waffles.runShell(in);
	    if ((out == null) || (out.getProperty(waffles.getOutProperty()) == null))
		throw new AmbitException(String.format("Model not generated", waffles.getOutProperty()));
	    if (!"0".equals(out.getProperty(ShellWafflesLearn.getExitcodeProperty())))
		throw new AmbitException(String.format("Model not generated [exit code = %s] %s",
			out.getProperty(ShellWafflesLearn.getExitcodeProperty()),
			out.getProperty(waffles.getOutProperty())));

	    String name = String.format("%s.%s.%s", waffles.getAlgorithm().name(), UUID.randomUUID().toString(),
		    waffles.getClass().getName());

	    model.setContentMediaType(algorithm.getFormat().getMediaType());
	    model.setParameters(parameters);
	    model.setId(null);
	    model.setName(name);
	    model.setAlgorithm(alg_reporter.getURI(algorithm));

	    /**
	     * Variables
	     */
	    AlgorithmURIReporter r = new AlgorithmURIReporter();
	    LiteratureEntry entry = new LiteratureEntry(name, algorithm == null ? waffles.getClass().getName()
		    : r.getURI(applicationRootReference.toString(), algorithm));

	    LiteratureEntry prediction = new LiteratureEntry(model.getName(), model_reporter.getURI(
		    applicationRootReference.toString(), model));
	    prediction.setType(_type.Model);

	    Template predictors = null;
	    Template dependent = null;
	    PredictedVarsTemplate predicted = null;

	    dependent = new Template(name + "#Dependent");
	    predicted = new PredictedVarsTemplate(name + "#Predicted");
	    predictors = new Template(name + "#Independent");

	    StringBuilder labels = null;
	    for (int i = 0; i < header.numAttributes(); i++) {
		boolean isTarget = false;
		for (String t : getTargetURI())
		    if (header.attribute(i).name().equals(t)) {
			header.setClassIndex(header.attribute(i).index());
			// but could be multiple targets
			if (labels == null)
			    labels = new StringBuilder();
			else
			    labels.append(",");
			labels.append(i);
			// dependent property
			Property property = createPropertyFromReference(new Reference(header.attribute(i).name()),
				entry);
			property.setOrder(i + 1);
			dependent.add(property);
			// predicted property
			Property predictedProperty = new Property(property.getName(), prediction);
			predictedProperty.setOrder(i + 1);
			predictedProperty.setLabel(property.getLabel());
			predictedProperty.setUnits(property.getUnits());
			predictedProperty.setClazz(property.getClazz());
			predictedProperty.setNominal(property.isNominal());
			predictedProperty.setEnabled(true);
			predicted.add(predictedProperty);
			// link to dependent - necessary for multilabel

			PropertyAnnotations annotations = new PropertyAnnotations();
			PropertyAnnotation annotation = new PropertyAnnotation();
			annotation.setObject(header.attribute(i).name());
			annotation.setPredicate("predictionOf");
			annotation.setType("Feature");
			annotations.add(annotation);
			predictedProperty.setAnnotations(annotations);

			if (header.attribute(i).isNominal()) {
			    Enumeration e = header.attribute(i).enumerateValues();
			    while (e.hasMoreElements()) {
				String value = e.nextElement().toString();
				predictedProperty.addAllowedValue(value);
				annotation = new PropertyAnnotation();
				annotation.setObject(value);
				annotation.setPredicate("acceptValue");
				annotation.setType("Feature");
				annotations.add(annotation);
			    }
			    predictedProperty.setAnnotations(annotations);
			}
			isTarget = true;
			break;
		    }
		if (!isTarget) {
		    Property property = createPropertyFromReference(new Reference(header.attribute(i).name()), entry);
		    property.setOrder(i + 1);
		    predictors.add(property);
		}
	    }

	    model.setContent(serializeModel(out.getProperty(waffles.getOutProperty()), header));
	    // if (supportsDistribution(classifier)) {}
	    if (labels != null)
		setDataOptions(String.format("-labels %s", labels));
	    model.setPredictors(predictors);
	    model.setDependent(dependent);
	    model.setPredicted(predicted);
	    // model.setParameters(new String[] {"-labels",labels.toString()});
	    return model;

	} catch (AmbitException x) {
	    throw x;
	} catch (Exception x) {
	    throw new AmbitException(x);
	}
    }

    protected String serializeModel(String json, Instances newInstances) throws IOException {
	// serialize model
	Form form = new Form();

	form.add("wafflesmodel", Base64.encode(json.getBytes()));
	newInstances.delete();
	if (newInstances.classIndex() >= 0)
	    form.add("classIndex", Integer.toString(newInstances.classIndex()));
	newInstances.delete();
	form.add("header", newInstances.toString());
	return form.getWebRepresentation().getText();
    }
}
