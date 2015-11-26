package ambit2.core.smiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.external.ShellSDFoutput;

public class OpenBabelAbstractShell<X> extends ShellSDFoutput<X> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8439060274752318591L;
	public static final String OBABEL_EXE = "obabel";
	public static final String OBABEL_HOME = "OBABEL_HOME";

	protected boolean hydrogens = true;
	public boolean isHydrogens() {
		return hydrogens;
	}

	public void setHydrogens(boolean hydrogens) {
		this.hydrogens = hydrogens;
	}

	public OpenBabelAbstractShell() throws ShellException {
		super();
		prefix = "";
	}
	
	protected String getOBabelHome() throws ShellException {
		return System.getenv(OBABEL_HOME);
	}

	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		String obabel_home = getOBabelHome();
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
		setInputFile(null);
		setOutputFile(getOutputFileName());		
		setReadOutput(true);
	}
	
	protected String getOutputFileName() {
		return "obabel.sdf";
	}
	protected String getOutputOption() {
		return "-osdf";
	}
	@Override
	protected IAtomContainer transform(X mol) {
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
		return String
		.format("%s%s.ambit2%s%s%sobabel",
				System.getProperty("java.io.tmpdir"), File.separator,
				File.separator,getWorkFolder(),
				File.separator);    	
    }	
	@Override
	protected synchronized java.util.List<String> prepareInput(String path, X mol) throws ShellException {

		try {
	    	String homeDir = getHomeDir(null); // getPath(new File(exe));
	    	File dir = new File(homeDir);
	    	if (!dir.exists()) dir.mkdirs();
	    	
	    	String outfile = String.format("%s%s%s",homeDir,File.separator,getOutputFile());
	    	
			List<String> list = new ArrayList<String>();
			if (hydrogens)
				list.add("-h");
			list.add(String.format("-:%s",mol));
			list.add(getOutputOption());
			list.add(String.format("-O%s",outfile));
			return list;
		} catch (Exception x) {
			throw new ShellException(this,x);
		}
	}
	
}
