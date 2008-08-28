package nplugins.workflow;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Workflow;

public class DemoWorkflow extends Workflow {
	public DemoWorkflow() {
		Primitive test1 =
			new Primitive("1","2",
				new Performer() {
					public Object execute() {
						System.out.println("first");
						return "3";
					}}
			);
		test1.setName("first");
		Primitive test2 =
			new Primitive("2","3",
				new Performer() {
					public Object execute() {
						System.out.println("second");
		                 try {
		                     Thread.sleep(300);
		                 } catch (Exception x) {
		                  
		                 }						
						return "4";
					}}
			);			
		test2.setName("second");
		Primitive test3 =
			new Primitive("2","3",
				new Performer() {
					public Object execute() {
						System.out.println("third");
						return "4";
					}}
			);			
		test3.setName("third");		
		setDefinition(test1.addStep(test2).addStep(test3));
	}
}
