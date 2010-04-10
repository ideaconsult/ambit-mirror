/* OutputFileSelection.java
 * Author: nina
 * Date: Feb 25, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.workflow.library;

import ambit2.core.io.FileOutputState;
import ambit2.core.io.FileState;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.WorkflowContext;

/**
 * Workflow sequence to select output file
 * @author nina
 *
 */
public class OutputFileSelection extends Sequence {
	public static String OUTPUTFILE = "ambit2.workflow.library.OUTPUTFILE";

	public OutputFileSelection(Activity onSuccess, WorkflowContext context) {
        this(onSuccess,getInitialFile(context));
	}
	protected static FileOutputState getInitialFile(WorkflowContext context) {
    	Object ol = context.get(OUTPUTFILE);
    	if ((ol == null) || !(ol instanceof FileOutputState)) {
    		ol = new FileOutputState();
    	}        
    	return (FileOutputState)ol;
	}
	public OutputFileSelection(Activity onSuccess) {
		this(onSuccess,new FileOutputState());
	}
	public OutputFileSelection(Activity onSuccess, FileOutputState file) {
        
        UserInteraction<FileOutputState> output = new UserInteraction<FileOutputState>(
        		file,OUTPUTFILE,"Select file to export to");
        output.setName("Select file");
        
        Conditional verify = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
                    	Object object = context.get(OUTPUTFILE);
                    	if ((object != null) && (object instanceof FileState)) {
                    		return ((FileState)object).getFile().getName() != null;
                    	} 
                    	return false;
                    }
                }, 
                onSuccess,
                null);
        verify.setName("Verify file");
        setName("[Select file]");
        
        addStep(output);
        addStep(verify);
        
	}
}
