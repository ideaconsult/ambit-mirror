package com.microworkflow.process;

import com.microworkflow.execution.Continuation;

public interface IWorkflowExceptionHandler {
	public enum RETURN_MODE {
	    RESUME,ABORT 
	}
	RETURN_MODE processException(Exception x,Continuation continuation, WorkflowContext context);
}
