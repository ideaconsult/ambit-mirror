package ambit2.core.external;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.data.MoleculeTools;

public abstract class ShellSDFoutput<INPUT> extends CommandShell<INPUT,IAtomContainer> {
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
	protected synchronized IAtomContainer parseOutput(String path, INPUT mol) throws ShellException {
		try {
			if (isReadOutput()) {
				MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(path + File.separator + getOutputFile()));
				IAtomContainer newmol = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
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

	
	
	protected String writeInputSDF(String path, IAtomContainer mol)
	throws IOException, CDKException {
		SDFWriter writer = null;
		try {
			String input = path + File.separator + getInputFile();
			FileOutputStream out = new FileOutputStream(input);
			writer = new SDFWriter(out);
			writer.write(mol);
			return input;
		} catch (CDKException x) {
			throw x;
		} catch (IOException x) {
			throw x;
		} finally {
			try {
				writer.close();
			} catch (Exception x) {
			}
		}
	}
}
