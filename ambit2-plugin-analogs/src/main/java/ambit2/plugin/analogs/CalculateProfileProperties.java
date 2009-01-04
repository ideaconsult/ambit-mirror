/* CalculateProfileProperties.java
 * Author: nina
 * Date: Dec 28, 2008
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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

import java.util.Iterator;

import ambit2.core.data.Profile;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Iterative;
import com.microworkflow.process.Primitive;

/**
 * Calculates properties for the target structures, defined by the profile. 
 * @author nina
 *
 */
public class CalculateProfileProperties<Target> extends Iterative<Target> {
	public CalculateProfileProperties(Activity body) {
		super(DBWorkflowContext.PROFILE,
				new Performer<Target, Iterator>() {
						public Iterator execute() throws Exception {
							Object target = getTarget();
							if (target instanceof Profile)
								return ((Profile)target).getProperties(true);
							else
								return null;
						};
				},
				"PROFILE_PROPERTY",
				body);
				
	}
	@Override
	public String toString() {
		return "Calculate properties";
	}
	public CalculateProfileProperties() {
		this(new Primitive("PROFILE_PROPERTY","XX",new Performer() {
			@Override
			public Object execute() throws Exception {
				// TODO Auto-generated method stub
				System.out.println(getTarget());
				System.out.println(getResultKey());
				return null;
			}
		}
		) {
			@Override
			public synchronized String getName() {
				return "Calculate";
			}
		});

				
	}	
	
}
