/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import java.util.ArrayList;

public interface WorkflowDomainObject {
	public Object executeDomainOperation(ArrayList arguments);

}
