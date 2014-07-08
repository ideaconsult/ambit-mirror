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

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.config.Preferences;
import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.external.ShellSDFoutput;

public class ShellSmi2SDF extends ShellSDFoutput<IAtomContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7647984144381286155L;

	protected boolean dropHydrogens = true;
	    
	public boolean isDropHydrogens() {
		return dropHydrogens;
	}
	public void setDropHydrogens(boolean dropHydrogens) {
		this.dropHydrogens = dropHydrogens;
	}
	protected transient SmilesGenerator gen = new SmilesGenerator(true);
	public ShellSmi2SDF() throws ShellException {
		super();
	}
	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		addExecutable(CommandShell.os_WINDOWS, "bin/smi23d/win/smi2sdf.exe",
				new String[]{"bin/smi23d/win/mmff94.prm","bin/smi23d/win/mmxconst.prm"});
        addExecutable(CommandShell.os_LINUX, "bin/smi23d/linux/smi2sdf",
    		new String[]{"bin/smi23d/linux/mmff94.prm","bin/smi23d/linux/mmxconst.prm"}
    		);  
        addExecutable(CommandShell.os_LINUX64, "bin/smi23d/linux64/smi2sdf",
        		new String[]{"bin/smi23d/linux64/mmff94.prm","bin/smi23d/linux64/mmxconst.prm"}
        		);         
        addExecutable(CommandShell.os_FreeBSD, "bin/smi23d/freebsd/smi2sdf",
        		new String[]{"bin/smi23d/freebsd/mmff94.prm","bin/smi23d/freebsd/mmxconst.prm"}
        		);         
		setInputFile("mol.smi");
		setOutputFile("rough.sdf");		
		setReadOutput(false);
	}
	
	@Override
	protected  synchronized List<String> prepareInput(String path, IAtomContainer mol) throws ShellException {
		try {
			//Object smiles = mol.getProperty("SMILES"); 
			//if (smiles == null) 
			Object smiles = mol.getProperty(Preferences.getProperty(Preferences.SMILES_FIELD)); 
			if (isGenerateSmiles() || (smiles == null)) {
                logger.fine("Generate smiles\t");
                IAtomContainer c = mol;
                if (dropHydrogens) {
                    c = (IAtomContainer) mol.clone();

                    c = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(c);
                }
                gen.setUseAromaticityFlag(true);
			    smiles = gen.createSMILES(c);
            } else logger.fine("Use smiles from file\t"+smiles);
			
			FileWriter writer = new FileWriter(path + File.separator + getInputFile());
			writer.write(smiles.toString());
			/* with long string for title , mengine crashes ...
			Object title = mol.getProperty(CDKConstants.TITLE);
			if (title != null) {
				writer.write('\t');
				writer.write(title.toString());
			}
			*/
			writer.write('\n');
			writer.flush();
			writer.close();
			
			List<String> list = new ArrayList<String>();
			list.add("-o");
			list.add(getOutputFile());
			list.add("-p");
			list.add("mmxconst.prm");
			list.add(getInputFile());
			return list;
		} catch (Exception x) {
			throw new ShellException(this,x);
		}
	}
	@Override
	public String toString() {
		return "smi2sdf";
	}
    protected boolean exitCodeOK(int exitVal) {
    	return exitVal != 0;
    }	
	@Override
	protected  synchronized IAtomContainer transform(IAtomContainer mol) {
		return mol;
	}
    public synchronized boolean isGenerateSmiles() {
        return  Preferences.getProperty(Preferences.SMILES_GEN).equals("true");
    }
    public synchronized void setGenerateSmiles(boolean generateSmiles) {
        if (generateSmiles)
        Preferences.setProperty(Preferences.SMILES_GEN,"true");
        else
            Preferences.setProperty(Preferences.SMILES_GEN,"false");
    }
}


