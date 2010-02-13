package ambit2.fastox.wizard;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.StepProcessor;


public class WizardStep {
	protected String title;
	protected String description;
	protected int index;
	public void setIndex(int index) {
		this.index = index;
	}
	protected Class resourceClass;
	protected Class processor;
	
	public StepProcessor getProcessor() throws ResourceException {
		try {
			return  (StepProcessor) processor.newInstance();
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		}
	}
	public static final String tab = "tab";
	
	public WizardStep(int index, String title, String description, Class  resourceClass) {
		this(index,title,description,resourceClass,StepProcessor.class);
	}	
	public WizardStep(int index, String title, String description, Class resourceClass, Class processor) {
		this.index = index;
		this.title = title;
		this.description = description;
		this.resourceClass = resourceClass;
		this.processor = processor;
	}
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	public int getIndex() {
		return index;
	}
	public String getResource() {
		if (getIndex()>0)
			return String.format("/step%d", getIndex());
		else return "/";
	}
	public String getResourceTab() {
		if (getIndex()>0)
			return String.format("%s/{%s}", getResource(),tab);
		else return "/";
	}

	public Class getResourceClass() {
		return resourceClass;
	}
	@Override
	public String toString() {
		return getResource();
	}
}
