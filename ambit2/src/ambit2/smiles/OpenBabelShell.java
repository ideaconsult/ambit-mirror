package ambit2.smiles;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.external.CommandShell;
import ambit2.external.ShellException;
import ambit2.external.ShellSDFoutput;

public class OpenBabelShell extends ShellSDFoutput<String> {
	public OpenBabelShell() throws ShellException {
		super();
	}
	protected void initialize() throws ShellException {
		super.initialize();
		addExecutable(CommandShell.os_WINDOWS, "helper/openbabel/win/babel.exe");
		setInputFile("obabel.smi");
		setOutputFile("obabel.sdf");		
		setReadOutput(true);
	}
	
	@Override
	protected List<String> prepareInput(String path, String mol) throws ShellException {
		try {
			FileWriter writer = new FileWriter(path + File.separator + getInputFile());
			writer.write(mol);
			writer.write('\t');
			writer.write(mol);
			writer.write('\n');
			writer.flush();
			writer.close();
			
			List<String> list = new ArrayList<String>();
			list.add("-h");
			list.add("-ismi");
			list.add(getInputFile());
			list.add("-osdf");
			list.add(getOutputFile());
			return list;
		} catch (Exception x) {
			throw new ShellException(this,x);
		}
	}
	@Override
	protected IMolecule transform(String mol) {
		return null;
	}	
	@Override
	public String toString() {
		return "OpenBabel";
	}
}
