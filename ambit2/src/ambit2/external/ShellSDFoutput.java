package ambit2.external;

import java.io.File;
import java.io.FileInputStream;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;

public abstract class ShellSDFoutput<INPUT> extends CommandShell<INPUT,IMolecule> {
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
	protected IMolecule parseOutput(String path, INPUT mol) throws ShellException {
		try {
			if (isReadOutput()) {
				MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(path + File.separator + getOutputFile()));
				IMolecule newmol = DefaultChemObjectBuilder.getInstance().newMolecule();
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
