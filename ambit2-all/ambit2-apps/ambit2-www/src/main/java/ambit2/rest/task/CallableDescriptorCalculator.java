package ambit2.rest.task;

import java.io.File;

import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.model.predictor.DescriptorPredictor;

public class CallableDescriptorCalculator<USERID> extends
	CallableModelPredictor<IStructureRecord, DescriptorPredictor, USERID> {

    public CallableDescriptorCalculator(Form form, Reference appReference, Context context,
	    DescriptorPredictor predictor, USERID token) {
	super(form, appReference, context, predictor, token);

    }

    @Override
    protected IProcessor<IStructureRecord, IStructureRecord> getWriter() throws Exception {
	if (foreignInputDataset) {
	    File file = File.createTempFile("dresult_", ".rdf");
	    tmpFileName = file.getAbsolutePath();
	    rdfFileWriter = new RDFFileWriter(file, applicationRootReference, null);
	    return rdfFileWriter;
	} else
	    // DescriptorCalculator has a writer embedded
	    return null;
    }

    protected static IProcessor<IStructureRecord, IStructureRecord> createPredictor(ModelQueryResults model)
	    throws Exception {
	if (model.getContentMediaType().equals(AlgorithmFormat.JAVA_CLASS.getMediaType()))
	    try {
		Profile<Property> p = new Profile<Property>();
		Property property = DescriptorsFactory.createDescriptor2Property(model.getContent());
		property.setEnabled(true);
		p.add(property);

		DescriptorsCalculator calculator = new DescriptorsCalculator();
		calculator.setDescriptors(p);
		return calculator;
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	    }
	else
	    throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, model.getContentMediaType());
    }
}
