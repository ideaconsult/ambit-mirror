package ambit2.rest.task;

import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.SimpleModelBuilder;

public class OptimizerModelBuilder extends SimpleModelBuilder {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1576610802954465728L;
    protected String mopac_commands;

    public String getMopac_commands() {
	return mopac_commands;
    }

    public void setMopac_commands(String mopac_commands) {
	this.mopac_commands = mopac_commands;
    }

    public OptimizerModelBuilder(Reference applicationRootReference, Form form, ModelURIReporter model_reporter,
	    AlgorithmURIReporter alg_reporter) {
	this(applicationRootReference, form, model_reporter, alg_reporter, false);
    }

    public OptimizerModelBuilder(Reference applicationRootReference, Form form, ModelURIReporter model_reporter,
	    AlgorithmURIReporter alg_reporter, boolean isModelHidden) {
	super(applicationRootReference, model_reporter, alg_reporter);
	this.modelHidden = isModelHidden;
    }

    public ModelQueryResults process(Algorithm algorithm) throws AmbitException {
	try {
	    ModelQueryResults mr = new ModelQueryResults();
	    mr.setHidden(modelHidden);
	    mr.setContentMediaType(AlgorithmFormat.MOPAC.getMediaType());

	    mr.setContent(algorithm.getContent().toString());
	    mr.setAlgorithm(alg_reporter.getURI(algorithm));
	    mr.setPredictors(algorithm.getInput());
	    if (mopac_commands != null) {
		mr.setParameters(new String[] { mopac_commands });
		mr.setName(UUID.nameUUIDFromBytes(mopac_commands.getBytes()) + "-" + algorithm.getName());
	    } else
		mr.setName(algorithm.getName());
	    Template dependent = new Template("Empty");
	    mr.setDependent(dependent);

	    PredictedVarsTemplate predicted = new PredictedVarsTemplate("Empty");
	    mr.setPredicted(predicted);

	    return mr;

	} catch (Exception x) {

	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getCause() == null ? x.getMessage() : x
		    .getCause().getMessage(), x);
	}
    }
}
