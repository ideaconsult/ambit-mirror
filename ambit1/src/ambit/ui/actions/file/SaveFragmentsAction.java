/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit.ui.actions.file;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

import ambit.data.ISharedData;
import ambit.data.descriptors.FuncGroupsDescriptorFactory;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.io.MyIOUtilities;
import ambit.ui.actions.AmbitAction;

public class SaveFragmentsAction extends AmbitAction {
	public SaveFragmentsAction(Object userData, Container mainFrame) {
		super(userData, mainFrame,"Save");
		putValue(SHORT_DESCRIPTION,"Save functional groups into a file");
	}
	@Override
	public void run(ActionEvent arg0) {
		super.run(arg0);
        File file = MyIOUtilities.selectFile(mainFrame,null,
                ((AmbitDatabaseToolsData)userData).getDefaultDir(),
                FuncGroupsDescriptorFactory.extensions,FuncGroupsDescriptorFactory.extensionDescription,false);
        try {
        	FileOutputStream writer = new FileOutputStream(file);
	        FuncGroupsDescriptorFactory.saveFragments(writer,
	        		((AmbitDatabaseToolsData)userData).getFragments());
	        writer.close();
        } catch (Exception x) {
        	((ISharedData) userData).getJobStatus().setError(x);
        }
		
	}
}
