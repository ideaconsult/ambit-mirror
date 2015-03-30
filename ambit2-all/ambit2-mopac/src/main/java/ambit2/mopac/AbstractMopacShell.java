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

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.data.MoleculeTools;

/**
 * Runs OpenMopac as an external executable and reads the results back.
 * The resulting molecule is assigned 3D coordinates as well as properties as EHOMO, ELUMO, ELECTRONIC_ENERGY, etc.
 * @author nina
 *
 */
public abstract class AbstractMopacShell extends CommandShell<IAtomContainer, IAtomContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5948332340539224507L;

	protected int maxHeavyAtoms = 60;
	protected int maxAllAtoms = 120;
	//30 minutes max, sorry
	public static String[] defaultparams = {"PM3 NOINTER MMOK BONDS MULLIK GNORM=1.0 T=30.00M","bin/mopac/MOPAC_7.1.exe",""}; //mmff94
	protected String mopac_commands = defaultparams[0];
	protected boolean useOriginalStructure = false;
	public boolean isUseOriginalStructure() {
		return useOriginalStructure;
	}

	public void setUseOriginalStructure(boolean useOriginalStructure) {
		this.useOriginalStructure = useOriginalStructure;
	}

	public boolean isOptimize() {
		return optimize;
	}

	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}
	protected boolean optimize = true;
	public String getMopac_commands() {
		return mopac_commands;
	}

	public void setMopac_commands(String mopac_commands) {
		this.mopac_commands = mopac_commands;
	}

    protected String inFile = "mopac.dat";
    protected String[] outFiles = {
    		"mopac.dat.out",
    		"mopac.dat.arc",
    		"mopac.dat.log",
    		"mopac.dat.temp",
    		"mopac.dat.end"};
	protected boolean errorIfDisconnected = false;
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
	public AbstractMopacShell() throws ShellException {
		super();
    	Arrays.sort(table);
	}
	
	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		addExecutable(CommandShell.os_WINDOWS, "bin/mopac/MOPAC_7.1.exe",null);
		addExecutable(CommandShell.os_WINDOWS7, "bin/mopac/MOPAC_7.1.exe",null);
		addExecutable(CommandShell.os_WINDOWSVISTA, "bin/mopac/MOPAC_7.1.exe",null);
		addExecutable(CommandShell.os_FreeBSD, "/usr/local/mopac/mopac",null);
		addExecutable(CommandShell.os_LINUX, "/usr/local/mopac/mopac",null);
		setInputFile("mol.smi");
		setOutputFile("rough.sdf");		
	}	
	protected abstract IAtomContainer generate3DStructure(IAtomContainer mol) throws AmbitException;

	/**
	 * This is not thread safe!
	 */
	@Override
	protected synchronized IAtomContainer transform_input(IAtomContainer mol) throws AmbitException {

		    if ((mol==null) || (mol.getAtomCount()==0)) throw new ShellException(this,"Empty molecule");
		    IAtomContainer newmol=null;
		    if (!useOriginalStructure) {
				newmol = generate3DStructure(mol);
		    } else newmol=mol;
	    	String exe = getExecutable();
	    	//String mopac_path = new File(exe).getParent();
	    	String homeDir = getHomeDir(null); // getPath(new File(exe));
	    	logger.fine("Writing MOPAC input  into " + homeDir + " for "+exe);
	    	try {
		        Mopac7Writer wri = new Mopac7Writer(new FileOutputStream(homeDir + "/" + inFile));
		        wri.setOptimize(isOptimize()?1:0);
		        wri.setMopacCommands(mopac_commands);
		        wri.write(newmol);
		        wri.close();
	    	} catch (Exception x) {
	    		throw new ShellException(this,x);
	    	}
	    	String os = System.getProperty("os.name");
	        for (int i=0; i< outFiles.length;i++) {
	            File f = new File(homeDir + "/"+getOutFile(i, os));
	            if (f.exists()) f.delete();
	        }
			return newmol;

	}
	
	protected String getOutFile(int i,String os) {
		if (os_LINUX.equals(os) || os_FreeBSD.equals(os)) //generates mopac.out from mopac.dat
			return outFiles[i].replace(".dat", "");
		else return outFiles[i]; //generates mopac.dat.out from mopac.dat
	}
	
	@Override
	protected String getPath(File file) {
		return getHomeDir(null);
	}
	@Override
	protected synchronized List<String> prepareInput(String path, IAtomContainer mol)	throws ShellException {

    	List<String> params = new ArrayList<String>();
    	params.add(inFile);
		return params;
	}
	@Override
	protected synchronized IAtomContainer parseOutput(String mopac_path, IAtomContainer mol)
			throws ShellException {
		String os = System.getProperty("os.name");
        for (int i=0; i< 2;i++) {
            String fname = mopac_path+"/" + getOutFile(i, os); 
            File f = new File(fname);
            if (!f.exists()) continue;
            logger.fine("<outfile name=\""+ fname + "\">");
            try {
                Mopac7Reader re = new Mopac7Reader(new FileInputStream(f));
                re.read(mol);
                re.close();
                f.delete();
            } catch (Exception x) {
                logger.fine("<error name=\""+ x.getMessage() + "\"/>");
                logger.fine("</outfile>");
                throw new ShellException(this,x);
            }
            logger.fine("</outfile>");
        }
		return mol;
	}

	@Override
	protected synchronized IAtomContainer transform(IAtomContainer mol) {
		return mol;
	}
	
	@Override
	public synchronized IAtomContainer runShell(IAtomContainer mol) throws ShellException {
		if ((mol==null) || (mol.getAtomCount()==0)) 
			throw new ShellException(this,"Undefined structure");
    		
		try {
			return applyEvenIfDisconnected(mol);
		} catch (ShellException x) {
			throw x;
		} catch (Exception x) {
			return mol;
		}
	}	
	protected synchronized IAtomContainer applyEvenIfDisconnected(IAtomContainer atomcontainer) throws ShellException {
		
    	IAtomContainerSet a  = ConnectivityChecker.partitionIntoMolecules(atomcontainer);
    	IAtomContainer mol = null;
    	if (a.getAtomContainerCount()>1)
    		if (errorIfDisconnected)
    			throw new ShellException(this,"Molecule disconnected");
    		else {
    			IAtomContainer result = MoleculeTools.newMolecule(atomcontainer.getBuilder());
    			for (int i=0; i < a.getAtomContainerCount();i++) {
    				if ((a.getAtomContainer(i)!=null) && (a.getAtomContainer(i).getAtomCount()>1)) 
    					mol = super.runShell(a.getAtomContainer(i));
    				else mol = a.getAtomContainer(i);
    				result.add(mol);
    			}
    			return result;
    		}
    	else 
    		return super.runShell(atomcontainer);
        
        /*    	
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(mol);

        List<IElement> v = MolecularFormulaManipulator.elements(formula);
        for (int i=0; i < v.size();i++) {
        	
            if (Arrays.binarySearch(table, v.get(i).getSymbol().trim())<0) {
                throw new ShellException(this,MESSAGE_UNSUPPORTED_TYPE +v.get(i).getSymbol());
            }
        }
        try {
	        IElement h = IsotopeFactory.getInstance(mol.getBuilder()).getElement("H");    	
	        int light = MolecularFormulaManipulator.getElementCount(formula,h);
	        int heavy = (mol.getAtomCount()-light);
	        if (heavy>maxHeavyAtoms) {
	        	throw new ShellException(this,"Skipping - heavy atoms ("+heavy + ") > " + maxHeavyAtoms );
	        } else if (light > maxAllAtoms) {
	        	throw new ShellException(this,"Skipping - all atoms ("+light + ") > " + maxAllAtoms );
	        }
        } catch (IOException x) {
        	throw new ShellException(this,x);
        }
   

        return true;
             */
	}
	@Override
	public String toString() {
		return "mopac";
	}
	@Override
	public synchronized IAtomContainer process(IAtomContainer target)
			throws AmbitException {
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
