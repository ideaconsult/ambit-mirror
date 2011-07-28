package ambit2.core.smiles;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.external.ShellSDFoutput;

public class OpenBabelShell extends ShellSDFoutput<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -89551999366481056L;

	public static final String OBABEL_EXE = "obabel";
	public static final String OBABEL_HOME = "OBABEL_HOME";
	
	public OpenBabelShell() throws ShellException {
		super();
		prefix = "";
	}
	
	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		String obabel_home = System.getenv(OBABEL_HOME);
		File exe = new File(String.format("%s/%s", obabel_home,OBABEL_EXE));
		File winexe = new File(String.format("%s/%s.exe", obabel_home,OBABEL_EXE));
		
		if (!exe.exists() && !winexe.exists()) {
			throw new ShellException(this,
				String.format("%s does not exist! Have you set %s environment variable?",
						exe.getAbsolutePath(),OBABEL_HOME));
		}
		addExecutable(CommandShell.os_WINDOWS, winexe.getAbsolutePath(),null);
		addExecutable(CommandShell.os_WINDOWSVISTA, winexe.getAbsolutePath(),null);
		addExecutable(CommandShell.os_WINDOWS7, winexe.getAbsolutePath(),null);
		addExecutable(CommandShell.os_FreeBSD, exe.getAbsolutePath(),null);
		addExecutable(CommandShell.os_LINUX, exe.getAbsolutePath(),null);
		setInputFile("obabel.smi");
		setOutputFile("obabel.sdf");		
		setReadOutput(true);
	}
	
	@Override
	protected synchronized List<String> prepareInput(String path, String mol) throws ShellException {
		try {
	    	String homeDir = getHomeDir(null); // getPath(new File(exe));
	    	File dir = new File(homeDir);
	    	if (!dir.exists()) dir.mkdirs();
	    	
	    	String molfile = String.format("%s%s%s",homeDir,File.separator,getInputFile());
	    	String outfile = String.format("%s%s%s",homeDir,File.separator,getOutputFile());
	    	
			FileWriter writer = new FileWriter(molfile);
			writer.write(mol);
			writer.write('\t');
			writer.write(mol);
			writer.write('\n');
			writer.flush();
			writer.close();
			
			List<String> list = new ArrayList<String>();
			list.add("-h");
			list.add("-ismi");
			list.add(molfile);
			list.add("-osdf");
			list.add(String.format("-O%s",outfile));
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
	@Override
	protected String getPath(File file) {
		return String.format("%s",getHomeDir(null));
	}
	@Override
    protected String getHomeDir(File file) {
    	return String.format("%s%s.ambit2%sobabel",System.getProperty("user.home"),File.separator,File.separator);
    }	
	
}
