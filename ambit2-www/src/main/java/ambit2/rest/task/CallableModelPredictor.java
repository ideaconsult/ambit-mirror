package ambit2.rest.task;

import java.io.Serializable;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ClientResource;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.ValuesReader;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.AmbitApplication;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.model.ModelURIReporter;

/**
 * 
 * @author nina
 *
 */
public class CallableModelPredictor extends CallableQueryProcessor<Object, IStructureRecord> {

	protected ModelQueryResults model;
	protected Reference datasetURI;
	protected ModelURIReporter<IQueryRetrieval<ModelQueryResults>> modelUriReporter;
	
	public CallableModelPredictor(Reference target, 
			Reference appReference,
			AmbitApplication application,
			ModelQueryResults model,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter		
				) {
		super(target,appReference,application);
		this.model = model;
		this.modelUriReporter = reporter;
	}	

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		ClientResource resource  = new ClientResource(reference);
		resource.setMethod(Method.GET);
		resource.get(MediaType.APPLICATION_JAVA_OBJECT);
		if (resource.getStatus().isSuccess()) {
			ObjectRepresentation<Serializable> repObject = new ObjectRepresentation<Serializable>(resource.getResponseEntity());
			return repObject.getObject();
		}
		return reference;
	}
	protected IProcessor<IStructureRecord,IStructureRecord> createPredictor(ModelQueryResults model) throws Exception {
		Profile<Property> p = new Profile<Property>();
		Property property = DescriptorsFactory.createDescriptor2Property(model.getContent());
		property.setEnabled(true);
		p.add(property);
		
		DescriptorsCalculator calculator = new DescriptorsCalculator();
		calculator.setDescriptors(p);	
		return calculator;
	}
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors() throws Exception {
	
		IProcessor<IStructureRecord,IStructureRecord> calculator = createPredictor(model);
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		//p1.add(new ProcessorStructureRetrieval());	
		
		ValuesReader readProfile = new ValuesReader();
		Template template = new Template();
		template.setName("DailyIntake");
		Property di = new Property("DailyIntake");
		di.setEnabled(true);
		template.add(di); // this is a hack for TTC application, TODO make it generic!!!
		readProfile.setProfile(template);
		
		p1.add(readProfile);
		
		p1.add(calculator);
		p1.setAbortOnError(true);
		
		return p1;
	}
	/**
	 * Returns reference to the same dataset, with additional features, predicted by the model
	 */
	@Override
	protected Reference createReference() throws Exception {
		String predicted = String.format("%s/predicted", 
				(new Reference(modelUriReporter.getURI(model))).toString());
		return new Reference(
				String.format("%s?feature_uris[]=%s",
						sourceReference.toString(),
						Reference.encode(predicted)));
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(request);
	}
	

}
