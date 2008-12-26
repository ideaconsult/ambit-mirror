package com.microworkflow.process;

import com.microworkflow.execution.Continuation;

public class WorkflowExceptionHandler implements IWorkflowExceptionHandler {
public RETURN_MODE processException(Exception x, Continuation continuation,
		WorkflowContext context) {
	context.put("ERROR", x);
	return RETURN_MODE.ABORT;
}

}
