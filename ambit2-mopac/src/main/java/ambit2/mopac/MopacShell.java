package ambit2.mopac;

import java.util.Arrays;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.external.ShellException;
import ambit2.smi23d.ShellMengine;
import ambit2.smi23d.ShellSmi2SDF;

/**
 * Runs MOPAC and smi2sdf + mengine as means to generate the starting 3D structure
 * @author nina
 *
 */
public class MopacShell extends AbstractMopacShell {
	protected ShellSmi2SDF smi2sdf;
	protected ShellMengine mengine;   
	/**
	 * 
	 */
	private static final long serialVersionUID = -8813390861716760573L;

	public MopacShell() throws ShellException {
    	smi2sdf = new ShellSmi2SDF();
    	mengine = new ShellMengine();  
    	Arrays.sort(table);
	}
	
	protected IAtomContainer generate3DStructure(IAtomContainer mol) throws ShellException {
		smi2sdf.setOutputFile("test.sdf");
		smi2sdf.runShell(mol);
		if ((mol==null) || (mol.getAtomCount()==0)) throw new ShellException(smi2sdf,String.format(getMsgemptymolecule(), smi2sdf.toString()));
		mengine.setInputFile("test.sdf");
		mengine.setOutputFile("good.sdf");
		IAtomContainer newmol = mengine.runShell(mol);
		if ((newmol==null) || (newmol.getAtomCount()==0)) 
			throw new ShellException(mengine,String.format(getMsgemptymolecule(), mengine.toString()));
		return newmol;
	}
}
