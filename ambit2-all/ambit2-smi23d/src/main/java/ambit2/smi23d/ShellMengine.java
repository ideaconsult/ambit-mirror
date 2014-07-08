/*
Copyright (C) 2005-2007  

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

package ambit2.smi23d;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.external.ShellSDFoutput;

/**
 * A wrapper for Mengine
 * @author nina
 *
 */
public class ShellMengine extends ShellSDFoutput<IAtomContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9089097054499846056L;
	public ShellMengine() throws ShellException {
		super();
	}
	@Override
	protected void initialize() throws ShellException {
		addExecutable(CommandShell.os_WINDOWS, "bin/smi23d/win/mengine.exe",
					new String[]{"bin/smi23d/win/mmff94.prm","bin/smi23d/win/mmxconst.prm"});		
        addExecutable(CommandShell.os_LINUX, "bin/smi23d/linux/mengine",
        		new String[]{"bin/smi23d/linux/mmff94.prm","bin/smi23d/linux/mmxconst.prm"}
        		);      
        addExecutable(CommandShell.os_LINUX64, "bin/smi23d/linux64/mengine",
        		new String[]{"bin/smi23d/linux64/mmff94.prm","bin/smi23d/linux64/mmxconst.prm"}
        		);          
        addExecutable(CommandShell.os_FreeBSD, "bin/smi23d/freebsd/mengine",
        		new String[]{"bin/smi23d/freebsd/mmff94.prm","bin/smi23d/freebsd/mmxconst.prm"}
        		);           
		setInputFile("rough.sdf");
		setOutputFile("opt.sdf");
		setReadOutput(true);
	}
	@Override
	protected synchronized List<String> prepareInput(String path, IAtomContainer mol) throws ShellException {
		List<String> list = new ArrayList<String>();
		list.add("-p");
		list.add("mmff94.prm");
		list.add("-c");
		list.add("mmxconst.prm");
		list.add("-o");
		list.add(outputFile);
		list.add(inputFile);
		return list;		
	}
	@Override
	public String toString() {
		return "mengine";
	}
	@Override
	protected IAtomContainer transform(IAtomContainer mol) {
		return mol;
	}

}


