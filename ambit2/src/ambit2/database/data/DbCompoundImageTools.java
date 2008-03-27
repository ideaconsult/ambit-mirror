/*
Copyright (C) 2005-2006  

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

package ambit2.database.data;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.sql.Connection;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.database.AmbitID;
import ambit2.database.processors.ReadStructureProcessor;
import ambit2.database.processors.ReadSubstanceProcessor;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.ui.data.CompoundImageTools;

public class DbCompoundImageTools extends CompoundImageTools {

	public DbCompoundImageTools() {
	}

	public DbCompoundImageTools(Dimension cellSize) {
		super(cellSize);
	}
	public synchronized BufferedImage getImage(Connection connection, AmbitID id) throws AmbitException {
		IAmbitProcessor processor = null;
		if (id.getIdStructure() > 0) processor = new ReadStructureProcessor(connection);
		else if (id.getIdSubstance() > 0) processor = new ReadSubstanceProcessor(connection);
		else return null;
        Object o = processor.process(id);
        if (o instanceof IAtomContainer) 
            return getImage((IAtomContainer)o);
        else return null;        
	}
}


