package ambit2.workflow.library;

import ambit2.core.io.FileInputState;
import ambit2.core.io.FileState;
import ambit2.db.LoginInfo;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.WorkflowContext;

public class InputFileSelection extends Sequence {
	public static String INPUTFILE = "ambit2.workflow.library.INPUTFILE";

	public InputFileSelection(Activity onSuccess, WorkflowContext context) {
        this(onSuccess,getInitialFile(context));
	}
	protected static FileInputState getInitialFile(WorkflowContext context) {
    	Object ol = context.get(INPUTFILE);
    	if ((ol == null) || !(ol instanceof FileInputState)) {
    		ol = new FileInputState();
    	}        
    	return (FileInputState)ol;
	}
	public InputFileSelection(Activity onSuccess) {
		this(onSuccess,new FileInputState());
	}
	public InputFileSelection(Activity onSuccess, FileInputState file) {
        
        UserInteraction<FileInputState> input = new UserInteraction<FileInputState>(
        		file,INPUTFILE,"Select file to import");
        input.setName("Select file");

        Conditional verify = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
                    	Object object = context.get(INPUTFILE);
                    	if ((object != null) && (object instanceof FileState)) {
                    		return ((FileState)object).getFile().exists(); 
                    	} 
                    	return false;
                    }
                }, 
                onSuccess,
                null);
        verify.setName("Verify file");
        setName("[Select file]");
        
        addStep(input);
        addStep(verify);
        
	}
}
