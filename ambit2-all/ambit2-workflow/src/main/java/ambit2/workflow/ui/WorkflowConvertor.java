package ambit2.workflow.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Hashtable;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Fork;
import com.microworkflow.process.Iterative;
import com.microworkflow.process.JoinActivity;
import com.microworkflow.process.NullActivity;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.While;
import com.microworkflow.process.Workflow;
import com.microworkflow.ui.WorkflowTools;

public abstract class WorkflowConvertor<Result, Node>  extends WorkflowTools<Node> {
	protected Result result;
	protected Hashtable<Activity,Node> lookup;

	public WorkflowConvertor(Result result) {
		this.result = result;
		lookup = new Hashtable<Activity,Node>();
	}
	
	public WorkflowConvertor() {
		this(null);
	}
	
	public void customizeElement(Node element, Activity activity) {
		if (activity instanceof Primitive)
			customizeElement(element, (Primitive)activity);
		else if (activity instanceof JoinActivity)
			customizeElement(element, (JoinActivity)activity);		
		else if (activity instanceof Fork)
			customizeElement(element, (Fork)activity);			
	}
	public abstract void customizeElement(Node element, JoinActivity activity);
	public abstract void customizeElement(Node element, Fork activity);
	public abstract void customizeElement(Node element, Primitive activity);
	public abstract void customizeElement(Node element, Iterative activity);
	public abstract void customizeElement(Node element, While activity);	
	public abstract void customizeElement(Node element, Conditional activity);	
	public abstract void customizeElement(Node element, Sequence activity);	
	public abstract void customizeElement(Node element, NullActivity activity);

	protected abstract Result init(Workflow workflow) throws Exception ;

	public synchronized Result write(Workflow workflow) throws Exception {
			lookup.clear();
	        result = init(workflow);
	        traverseActivity(null,workflow.getDefinition(),0, true);
	        return result;
	        
    }
	
	public synchronized void write(Workflow workflow, Writer writer) throws Exception {
		throw new Exception("Not implemented");
	}	
	public synchronized void write(Workflow workflow, File file) throws Exception {
		FileWriter out = new FileWriter(file);
		try {
			write(workflow,out);
		} catch (Exception x) {
			throw new Exception(x);
		} finally {
			out.close();
		}
	}	
	public synchronized Workflow read(Result result) throws Exception {
		throw new Exception("Not implemented");
	}
	
	
}
