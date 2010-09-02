package ambit2.core.external;

import java.io.File;
import java.io.FileInputStream;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.data.MoleculeTools;

public abstract class ShellSDFoutput<INPUT> extends CommandShell<INPUT,IMolecule> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8770974107691241294L;
	protected boolean readOutput;
	
	public ShellSDFoutput()  throws ShellException {
		super();
	}
	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		setReadOutput(false);
	}
	
	@Override
	protected synchronized IMolecule parseOutput(String path, INPUT mol) throws ShellException {
		try {
			if (isReadOutput()) {
				MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(path + File.separator + getOutputFile()));
				IMolecule newmol = MoleculeTools.newMolecule(NoNotificationChemObjectBuilder.getInstance());
				reader.read(newmol);
				reader.close();
				return newmol;
			} else return transform(mol);
		} catch (Exception x) {
			throw new ShellException(this,x);
		}
	}

	public boolean isReadOutput() {
		return readOutput;
	}
	public void setReadOutput(boolean readOutput) {
		this.readOutput = readOutput;
	}    	

}
