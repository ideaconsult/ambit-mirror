package ambit2.workflow.ui;

import java.beans.PropertyChangeEvent;

import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.ValueLatchPair;

public class UserInteractionEvent<V> extends PropertyChangeEvent {
	public static final String PROPERTYNAME=DBWorkflowContext.USERINTERACTION;
	protected String title="";
	/**
	 * 
	 */
	private static final long serialVersionUID = -7592763278027218383L;	
	
	public UserInteractionEvent(Object source,String title, ValueLatchPair<V> newValue) {
		super(source, PROPERTYNAME, null, newValue);
		setTitle(title);
	}

	public ValueLatchPair<V> getLatch() {
		return (ValueLatchPair<V>) getNewValue();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
