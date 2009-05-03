package ambit2.workflow.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.microworkflow.events.WorkflowListener;
import com.microworkflow.process.Workflow;

public class WorkflowConsolePanel<T> extends AbstractEditor<T> implements WorkflowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8977175757180829561L;
	protected JTextArea text = null;

	public WorkflowConsolePanel() {
		this(null);
	}	
	public WorkflowConsolePanel(Workflow workflow) {
		super(null);
		text = new JTextArea();
		if (workflow!= null)
			workflow.addPropertyChangeListener(this);
		setAnimate(true);
		
		setEditable(false);
		JScrollPane pane = new JScrollPane(text);
		pane.setBorder(null);
		add(pane,BorderLayout.CENTER);
		setPreferredSize(new Dimension(100,100));
	}	
	@Override
	protected void animate(PropertyChangeEvent arg0) {
		if (arg0.getNewValue()!=null)
			text.setText(arg0.getNewValue().toString());
		
	}

	@Override
	public void clear() {
		text.setText("");
		
	}

	public boolean isEditable() {
		return text.isEditable();
	}

	public void setEditable(boolean editable) {
		text.setEditable(editable);
		
	}
	@Override
	public String toString() {
		return "Log";
	}

}
