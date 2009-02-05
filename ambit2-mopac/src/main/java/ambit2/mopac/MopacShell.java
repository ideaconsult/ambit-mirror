/* MopacShell.java
 * Author: Nina Jeliazkova
 * Date: 2008-12-13
 * Revision: 1.0 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
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
package ambit2.mopac;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.external.CommandShell;
import ambit2.core.external.ShellException;
import ambit2.core.processors.IProcessor;
import ambit2.smi23d.ShellMengine;
import ambit2.smi23d.ShellSmi2SDF;

/**
 * Runs OpenMopac as an external executable and reads the results back.
 * The resulting molecule is assigned 3D coordinates as well as properties as EHOMO, ELUMO, ELECTRONIC_ENERGY, etc.
 * @author nina
 *
 */
public class MopacShell extends CommandShell<IAtomContainer, IAtomContainer> {
	protected int maxHeavyAtoms = 60;
	protected int maxAllAtoms = 120;
	public static String[] defaultparams = {"PM3 NOINTER NOMM BONDS MULLIK PRECISE GNORM=0.0","bin/mopac/MOPAC_7.1.exe",""}; //mmff94
	protected String mopac_commands = defaultparams[0];
	public String getMopac_commands() {
		return mopac_commands;
	}

	public void setMopac_commands(String mopac_commands) {
		this.mopac_commands = mopac_commands;
	}
	protected ShellSmi2SDF smi2sdf;
	protected ShellMengine mengine;   
    protected String inFile = "mopac.dat";
    protected String[] outFile = {
    		"mopac.dat.out",
    		"mopac.dat.arc",
    		"mopac.dat.log",
    		"mopac.dat.temp",
    		"mopac.dat.end"};
	protected boolean errorIfDisconnected = true;
	public int getMaxHeavyAtoms() {
		return maxHeavyAtoms;
	}

	public void setMaxHeavyAtoms(int maxHeavyAtoms) {
		this.maxHeavyAtoms = maxHeavyAtoms;
	}

	public int getMaxAllAtoms() {
		return maxAllAtoms;
	}

	public void setMaxAllAtoms(int maxAllAtoms) {
		this.maxAllAtoms = maxAllAtoms;
	}

	public boolean isErrorIfDisconnected() {
		return errorIfDisconnected;
	}

	public void setErrorIfDisconnected(boolean errorIfDisconnected) {
		this.errorIfDisconnected = errorIfDisconnected;
	}
	public static  String MESSAGE_UNSUPPORTED_TYPE = "Unsupported atom type ";
    protected String[] table = new String[] {"C","H","Cl","B","F","I","Br","O","N","S","P"};
	public MopacShell() throws ShellException {
    	smi2sdf = new ShellSmi2SDF();
    	mengine = new ShellMengine();  
    	Arrays.sort(table);
	}
	
	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		addExecutable(CommandShell.os_WINDOWS, "bin/mopac/MOPAC_7.1.exe",null);
		setInputFile("mol.smi");
		setOutputFile("rough.sdf");		
	}	
	@Override
	protected IAtomContainer transform_input(IAtomContainer mol) throws ShellException {
			smi2sdf.setOutputFile("test.sdf");
			smi2sdf.runShell((IMolecule)mol);
			mengine.setInputFile("test.sdf");
			mengine.setOutputFile("good.sdf");
			IMolecule newmol = mengine.runShell((IMolecule)mol);
	    	logger.debug("Writing MOPAC input");
	    	String exe = getExecutable();
	    	String mopac_path = new File(exe).getParent();
	    	try {
		        Mopac7Writer wri = new Mopac7Writer(new FileOutputStream(mopac_path + "/" + inFile));
		        wri.setOptimize(1);
		        wri.setMopacCommands(mopac_commands);
		        wri.write(newmol);
		        wri.close();
	    	} catch (Exception x) {
	    		throw new ShellException(this,x);
	    	}
	        for (int i=0; i< outFile.length;i++) {
	            File f = new File(mopac_path + "/"+outFile[i]);
	            if (f.exists()) f.delete();
	        }
			return newmol;

	}
	@Override
	protected List<String> prepareInput(String path, IAtomContainer mol)	throws ShellException {

    	List<String> params = new ArrayList<String>();
    	params.add(inFile);
		return params;
	}
	@Override
	protected IAtomContainer parseOutput(String mopac_path, IAtomContainer mol)
			throws ShellException {
        for (int i=0; i< 2;i++) {
            String fname = mopac_path+"/" + outFile[i]; 
            File f = new File(fname);
            if (!f.exists()) continue;
            logger.debug("<outfile name=\""+ fname + "\">");
            try {
                Mopac7Reader re = new Mopac7Reader(new FileInputStream(f));
                re.read(mol);
                re.close();
                f.delete();
            } catch (Exception x) {
                logger.debug("<error name=\""+ x.getMessage() + "\"/>");
                logger.debug("</outfile>");
                throw new ShellException(this,x);
            }
            logger.debug("</outfile>");
        }
		return mol;
	}

	@Override
	protected IAtomContainer transform(IAtomContainer mol) {
		return mol;
	}
	
	@Override
	public IAtomContainer runShell(IAtomContainer mol) throws ShellException {
		if (canApply(mol))
			return super.runShell(mol);
		else 
			return mol;
	}	
	protected boolean canApply(IAtomContainer atomcontainer) throws ShellException {
		if ((atomcontainer==null) || (atomcontainer.getAtomCount()==0)) 
			throw new ShellException(this,"Undefined structure");
    	IAtomContainerSet a  = ConnectivityChecker.partitionIntoMolecules(atomcontainer);
    	IAtomContainer mol = null;
    	if (a.getAtomContainerCount()>1)
    		if (errorIfDisconnected)
    			throw new ShellException(this,"Molecule disconnected");
    		else {
    			
    			for (int i=0; i < a.getAtomContainerCount();i++)
    				if ((mol == null) || (mol.getAtomCount() < a.getAtomContainer(i).getAtomCount())) 
    					mol = a.getAtomContainer(i);
    		}
    	else mol = atomcontainer;
    		
    			
        MFAnalyser mfa = new MFAnalyser(mol);
        int heavy = mfa.getHeavyAtoms().size();
        int light = (mol.getAtomCount()-heavy);
        if (heavy>maxHeavyAtoms) {
        	throw new ShellException(this,"Skipping - heavy atoms ("+heavy + ") > " + maxHeavyAtoms );
        } else if (light > maxAllAtoms) {
        	throw new ShellException(this,"Skipping - all atoms ("+light + ") > " + maxAllAtoms );
        }
        
        List v = mfa.getElements();
        for (int i=0; i < v.size();i++) {
        	
            if (Arrays.binarySearch(table, v.get(i).toString().trim())<0) {
                throw new ShellException(this,MESSAGE_UNSUPPORTED_TYPE +v.get(i));
            }
        }
        return true;
	}
	@Override
	public IAtomContainer process(IAtomContainer target) throws AmbitException {

		return super.process(target);
	}
}

/**
*
* 
aigin - use a Gaussian zmatrix geometry specification
biradical- system has two unpaired electrons
bonds - print bond order matrix
C.I. - configuration interaction (useful for excited states)
charge=N - charge for ions
dfp - Davidson-Fletcher-Powell method for geometry optimization
dipole - fit the ESP to the calculated dipole
drc - dynamic reaction coordinate calculation
esp - electrostatic potential calculation
esr - calculate rhf unpaired spin density
excited - optimize first excited singlet state
force - force calculation (vibrational frequencies)
geo-ok - override interatomic distance check
hyperfine- hyperfine coupling constants to be calculated
irc - intrinsic reaction coordinate calculation
k=(n,n) - one dimensional Brillouin zone structure (polymer)
localize - print localized orbitals
mmok - use molecular mechanics correction to CONH bonds
mullik - mulliken population analysis
nomm - do not use molecular mechanics correction to CONH bonds
open - open shell rhf calculation
pi - resolve density matrix into sigma and pi bonds
polar - calculate first, second and third order polarizablilities
precise - criteria to be increased by 100 times
pulay - use Pulay's converger to obtain a scf
restart - calculation restarted
root=n - root n to be optimized in a C.I. calculation
rot=n - symmetry number of the system is n
saddle - optimize transition state
scale - scaling factor for Van der Waals distance in ESP
symmetry - impose symmetry conditions
uhf - unrestricted calculation
vectors - print final eigenvectors (molecular orbital coeficients)
xyz - do all geometric operations in cartesian coordinates



*/