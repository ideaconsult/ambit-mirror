package ambit2.core.smiles;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.base.external.ShellException;
import ambit2.core.external.ShellSDFoutput;

public class OpenBabelShell extends ShellSDFoutput<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -89551999366481056L;
	protected static String[] libFiles = {
			"bin/openbabel/2.2.3/win/iconv.dll",
			"bin/openbabel/2.2.3/win/zlib1.dll",
			"bin/openbabel/2.2.3/win/libstdinchi.dll",
			"bin/openbabel/2.2.3/win/libxml2.dll",
			"bin/openbabel/2.2.3/win/OBDLL.dll",
			"bin/openbabel/2.2.3/win/oberror.dll",
			"bin/openbabel/2.2.3/win/OBConv.dll",
			
			"bin/openbabel/2.2.3/win/License.txt",
			"bin/openbabel/2.2.3/win/obcommon.obf",
			"bin/openbabel/2.2.3/win/OBDESC.obf",
			"bin/openbabel/2.2.3/win/OBFPRT.obf",
			"bin/openbabel/2.2.3/win/OBInchi.obf",
			"bin/openbabel/2.2.3/win/OBMCDL.obf",
			"bin/openbabel/2.2.3/win/OBMore.obf",
			"bin/openbabel/2.2.3/win/OBUtil.obf",
			"bin/openbabel/2.2.3/win/OBXML.obf",
			
				};
	public OpenBabelShell() throws ShellException {
		super();
		prefix = "";
	}
	protected void initialize() throws ShellException {
		super.initialize();
		addExecutableWin("bin/openbabel/2.2.3/win/babel.exe",libFiles);

		setInputFile("obabel.smi");
		setOutputFile("obabel.sdf");		
		setReadOutput(true);
	}
	
	@Override
	protected synchronized List<String> prepareInput(String path, String mol) throws ShellException {
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
