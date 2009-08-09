package ambit2.core.data.model;

import org.apache.poi.hssf.record.formula.functions.T;

import ambit2.base.data.Template;


/**
 * Encapsulated a Model, defined by {@link Template} predictors, {@link Template} dependent variables
 * and instances.
 * @author nina
 *
 */
public abstract class ModelWrapper<T,TrainingInstances extends T,TestInstances extends T,Content> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9217727533985055359L;
	protected Template predictors;
	protected Template dependent;
	protected TrainingInstances instances;
	protected TestInstances testInstances;
	
	public TestInstances getTestInstances() {
		return testInstances;
	}
	public void setTestInstances(TestInstances testInstances) {
		this.testInstances = testInstances;
	}
	protected Content content;
	protected int id;
	protected String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TrainingInstances getTrainingInstances() {
		return instances;
	}
	public void setTrainingInstances(TrainingInstances instances) {
		this.instances = instances;
	}
	
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	
	public Template getPredictors() {
		return predictors;
	}
	public void setPredictors(Template predictors) {
		this.predictors = predictors;
	}

	public Template getDependent() {
		return dependent;
	}
	public void setDependent(Template dependent) {
		this.dependent = dependent;
	}
	
}	
