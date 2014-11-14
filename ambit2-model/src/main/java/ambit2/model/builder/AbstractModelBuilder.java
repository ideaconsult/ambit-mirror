package ambit2.model.builder;

import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;

/**
 * Abstract class for model building
 * @author nina
 *
 * @param <A>
 * @param <T>
 * @param <TrainingInstances>
 * @param <TestInstances>
 * @param <Content>
 */
public abstract class AbstractModelBuilder<DATA,A extends Algorithm,Model extends ModelQueryResults> 
											extends DefaultAmbitProcessor<A, Model> {

	protected String[] targetURI = null;
	public String[] getTargetURI() {
		return targetURI;
	}
	public void setTargetURI(String[] targetURI) {
		this.targetURI = targetURI;
	}
	protected String[] parameters = null;	
	protected DATA trainingData = null;

	public DATA getTrainingData() {
		return trainingData;
	}
	public void setTrainingData(DATA trainingData) {
		this.trainingData = trainingData;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3721710990307131078L;
	public AbstractModelBuilder() {
		this(null,null);
	}
	public AbstractModelBuilder(
			String[] targetURI,
			String[] parameters) {
		super();
		this.targetURI = targetURI;
		this.parameters = parameters;
	}	

	
	
}
