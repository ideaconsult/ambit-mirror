package ambit2.mopac;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.external.ShellException;
import ambit2.core.smiles.OpenBabelGen3D;

public class MopacShellOB extends AbstractMopacShell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2622221792381035489L;
	protected OpenBabelGen3D shell;
	
	public MopacShellOB() throws ShellException {
		this(new OpenBabelGen3D());
	}
	
	public MopacShellOB(OpenBabelGen3D shell) throws ShellException {
		super();
		this.shell = shell;
	}
	@Override
	protected IAtomContainer generate3DStructure(IAtomContainer mol) throws AmbitException {
		return shell.process(mol);
	}

}
