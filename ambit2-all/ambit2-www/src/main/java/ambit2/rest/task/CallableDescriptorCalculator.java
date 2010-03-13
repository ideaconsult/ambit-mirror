package ambit2.rest.task;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.model.predictor.DescriptorPredictor;

public class CallableDescriptorCalculator extends CallableModelPredictor<IStructureRecord,DescriptorPredictor> {

	public CallableDescriptorCalculator(Form form,
			Reference appReference,Context context,
			DescriptorPredictor predictor) {
		super(form, appReference, context, predictor);

	}

	@Override
	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() {
		//DescriptorCalculator has a writer embedded
		return null;
	}
	
	protected static IProcessor<IStructureRecord,IStructureRecord> createPredictor(ModelQueryResults model) throws Exception {
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
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
		else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE,model.getContentMediaType());
	}
}
