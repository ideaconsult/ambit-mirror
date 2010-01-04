package ambit2.core.data.model;

import java.io.Serializable;

import ambit2.base.data.Template;


/**
 * Encapsulated a Model, defined by {@link Template} predictors, {@link Template} dependent variables
 * and instances.
 * @author nina
 *
 */
public abstract class ModelWrapper<T,TrainingInstances extends T,TestInstances extends T,Content> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9217727533985055359L;
	protected Template predictors;
	protected Template dependent;
	protected Template predicted;
	protected String parameters;
	
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String[] parameters) {
		StringBuilder b = new StringBuilder();
		for (String p:parameters) {b.append(p);b.append('\t');}
		this.parameters = b.toString();
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public Template getPredicted() {
		return predicted;
	}
	public void setPredicted(Template predicted) {
		this.predicted = predicted;
	}
	protected TrainingInstances instances;
	protected TestInstances testInstances;
	protected String algorithm;
	
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public TestInstances getTestInstances() {
		return testInstances;
	}
	public void setTestInstances(TestInstances testInstances) {
		this.testInstances = testInstances;
	}
	protected Content content;
	protected Integer id;
	protected String name;
	protected String contentMediaType = "application/java";
	public String getContentMediaType() {
		return contentMediaType;
	}
	public void setContentMediaType(String contentMediaType) {
		this.contentMediaType = contentMediaType;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if ((name!=null) && (name.length()>255))
			this.name = name.substring(0,255);
		else
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
	public void setQueryID(Object id) {
		if (id instanceof Integer) {
			setId((Integer)id);
		} else 
			setName(id.toString());
	}
	public Object getQueryID() {
		return (getId()==null)?getName():getId(); 
	}
	
}	
