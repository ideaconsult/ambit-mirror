package ambit2.workflow;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.ValueLatchPair;


public class UserInteraction<T> extends Primitive {
	public UserInteraction(final T query, String resultTag) {
		this(query,resultTag,"User input");
	}
	public UserInteraction(final T query, final String resultTag, String title) {
	    super(DBWorkflowContext.USERINTERACTION,resultTag,new Performer() {
	        @Override
	        public Object execute() throws Exception {
	            Object o = getTarget();
	            
            	Object ol = context.get(resultTag);
            	if (ol == null) {
            		ol = query;
            	}
            		
	            if (o == null) {
	                ValueLatchPair<T> latch = new ValueLatchPair<T>((T)ol);
	                context.put(DBWorkflowContext.USERINTERACTION,latch);
	                //This is a blocking operation!
	                T li = null;
	                try {
	                	li = latch.getLatch().getValue();
	                } catch (InterruptedException x) {
	                    context.put(DBWorkflowContext.ERROR, x);
	                    return null;
	                } finally {
	                	context.remove(DBWorkflowContext.USERINTERACTION);
	                }
	                if (li == null) throw new Exception("Workflow cancelled.");
                    return li;	                
	                
	            } else return o;    
	        }
	    }); 
	    setName(title);   
	}
	

}
  