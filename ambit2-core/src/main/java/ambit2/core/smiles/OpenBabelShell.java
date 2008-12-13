package ambit2.core.smiles;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.core.external.CommandShell;
import ambit2.core.external.ShellException;
import ambit2.core.external.ShellSDFoutput;

public class OpenBabelShell extends ShellSDFoutput<String> {
	protected static String[] libFiles = {
			"bin/openbabel/win/iconv.dll",
				"bin/openbabel/win/libinchi.dll",
				"bin/openbabel/win/libxml2.dll",
				"bin/openbabel/win/OpenBabelDLL.dll",
				"bin/openbabel/win/zlib1.dll"};
	public OpenBabelShell() throws ShellException {
		super();
	}
	protected void initialize() throws ShellException {
		super.initialize();
		addExecutable(CommandShell.os_WINDOWS, "bin/openbabel/win/babel.exe",libFiles);
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
