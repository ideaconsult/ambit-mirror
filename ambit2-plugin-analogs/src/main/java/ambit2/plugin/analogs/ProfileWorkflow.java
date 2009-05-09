/* ProfileWorkflow.java
 * Author: nina
 * Date: May 3, 2009
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

package ambit2.plugin.analogs;

import ambit2.workflow.library.DefineProfile;
import ambit2.workflow.library.DefineStructure;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class ProfileWorkflow extends Workflow {
	public ProfileWorkflow() {
		
        Sequence seq=new Sequence();
        seq.setName("Profile");    	
        
        seq.addStep(new DefineStructure());
        seq.addStep(new DefineProfile());
        seq.addStep(new RetrieveProfileData()); // for .RECORDS
        seq.addStep(new StructureListDescriptorCalculator());

        setDefinition(new LoginSequence(seq));
       // setDefinition(seq);
	}
}
