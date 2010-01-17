package ambit2.rest.task;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.model.ModelURIReporter;

public class CallableDescriptorCalculator extends CallableModelPredictor<IStructureRecord> {

	public CallableDescriptorCalculator(Form form,
			Reference appReference,Context context,
			ModelQueryResults model,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter) {
		super(form, appReference, context, model, reporter);

	}
	protected IProcessor<IStructureRecord,IStructureRecord> createTranslator(ModelQueryResults model) throws Exception {
		return new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws ambit2.base.exceptions.AmbitException {
				return (IStructureRecord)target;
			};
		};
	}	
	protected IProcessor<IStructureRecord,IStructureRecord> createPredictor(ModelQueryResults model) throws Exception {
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
