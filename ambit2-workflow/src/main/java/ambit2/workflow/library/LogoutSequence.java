/* LogoutSequence.java
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

import ambit2.db.DatasourceFactory;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

/**
 * Log out
 * @author nina
 *
 */
public class LogoutSequence extends Sequence {
	public LogoutSequence() {
		Primitive<String, String> p = new Primitive<String, String>(
				DBWorkflowContext.DBCONNECTION_URI,
				DBWorkflowContext.DATASOURCE,
				new Performer<String, String>() {
					@Override
					public String execute() throws Exception {
						DatasourceFactory.logout(getTarget());
						getContext().put(getTargetKey(),null); 
						return null;
					}
				}
				) ;
		p.setName("Log out");
	}
}
