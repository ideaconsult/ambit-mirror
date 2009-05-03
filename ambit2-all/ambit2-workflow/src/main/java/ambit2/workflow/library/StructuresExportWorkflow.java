/* StructuresExportWorkflow.java
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

package ambit2.workflow.library;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.ProcessorFileExport;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.workflow.DBWorkflowContext;

public class StructuresExportWorkflow extends ExportWorkflow<IQueryRetrieval<IStructureRecord>> {

	@Override
	protected String getContentTag() {
		return DBWorkflowContext.STOREDQUERY;
	}

	@Override
	protected ProcessorFileExport<IQueryRetrieval<IStructureRecord>> getProcessor() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected String getOutputTag() {
		// TODO Auto-generated method stub
		return null;
	}
}

