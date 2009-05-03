/* RetrieveProfileData.java
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

import java.util.List;

import ambit2.base.data.Profile;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.processors.BatchStructuresListProcessor;
import ambit2.db.search.property.ValuesReader;
import ambit2.workflow.DBProcessorPerformer;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

/**
 * Retrieves properties for the target structure, given a profile.
 * @author nina
 *
 */
public class RetrieveProfileData extends Sequence {
	public RetrieveProfileData() {
		
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		final ValuesReader reader = new ValuesReader();
		p1.add(reader);
		
		BatchStructuresListProcessor batch = new BatchStructuresListProcessor();
		batch.setProcessorChain(p1);
		
		DBProcessorPerformer<List<IStructureRecord>,IBatchStatistics> performer = 
			new DBProcessorPerformer<List<IStructureRecord>,IBatchStatistics>(batch) {
			public IBatchStatistics execute() throws Exception {
				Object o = getContext().get(DBWorkflowContext.PROFILE);
				reader.setProfile((Profile)o);
				return super.execute();
			}
		};		
		
		Primitive<List<IStructureRecord>,IBatchStatistics> ap1 = 
			new Primitive<List<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.RECORDS,
				DBWorkflowContext.BATCHSTATS,
				performer);
			
	    ap1.setName(getName());		
	    addStep(ap1);
	}
	@Override
	public synchronized String getName() {
		return "Retrieve profile data";
	}
}
