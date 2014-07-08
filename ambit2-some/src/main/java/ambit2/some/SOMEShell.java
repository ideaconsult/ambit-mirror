package ambit2.some;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.io.MDLWriter;

/**
 * Wrapper for Site Of Metabolism Estimator (SOME) http://www.dddc.ac.cn/adme/myzheng/SOME_1_0.tar.gz.
 * Expects SOME executable as set by the SOME_HOME environment var.
 * @author nina
 *
 */
public class SOMEShell extends CommandShell<IAtomContainer, IAtomContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5948332340539224507L;
	public static final String SOME_RESULT = "SOME_RESULT";
	public static final String SOME_HOME = "SOME_HOME";
	
	
	protected boolean useOriginalStructure = false;
	public boolean isUseOriginalStructure() {
		return useOriginalStructure;
	}

	public void setUseOriginalStructure(boolean useOriginalStructure) {
		this.useOriginalStructure = useOriginalStructure;
	}

    protected String inFile = "someinput.mol";
    protected String[] outFile = {
    		"someinput.some"};

    public SOMEShell() throws ShellException {
	}
	
    @Override
    protected String getHomeDir(File file) {
    	return System.getenv("SOME_HOME");
    }
	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		String some_home = System.getenv("SOME_HOME");
		File exe = new File(String.format("%s/buildsome/some.exe", some_home));
		if (!exe.exists()) {
			throw new ShellException(this,
				String.format("%s does not exist! Have you set SOME_HOME environment variable?",
						exe.getAbsolutePath()));
		}
		addExecutable(CommandShell.os_WINDOWS, exe.getAbsolutePath(),null);
		addExecutable(CommandShell.os_FreeBSD, exe.getAbsolutePath(),null);
		addExecutable(CommandShell.os_LINUX, exe.getAbsolutePath(),null);
		setInputFile("someinput.mol");
		setOutputFile("someinput.some");		
	}	
	@Override
	protected synchronized IAtomContainer transform_input(IAtomContainer mol) throws ShellException {
			final String msg="Empty molecule after %s processing"; 
		    if ((mol==null) || (mol.getAtomCount()==0)) throw new ShellException(this,"Empty molecule");
		
	    	String homeDir = getHomeDir(null); // getPath(new File(exe));
	    	try {
			    MDLWriter writer = new MDLWriter(new FileOutputStream(homeDir + "/example/" + inFile));
			    writer.writeMolecule(mol);	    		
		        writer.close();
	    	} catch (Exception x) {
	    		throw new ShellException(this,x);
	    	}
	        for (int i=0; i< outFile.length;i++) {
	            File f = new File(homeDir + "/"+outFile[i]);
	            if (f.exists()) f.delete();
	        }
			return mol;

	}
	@Override
	protected String getPath(File file) {
		return String.format("%s/example",getHomeDir(null));
	}
	@Override
	protected synchronized List<String> prepareInput(String path, IAtomContainer mol)	throws ShellException {

    	List<String> params = new ArrayList<String>();
    	params.add(inFile);
		return params;
	}
	@Override
	protected synchronized IAtomContainer parseOutput(String mopac_path, IAtomContainer mol)
			throws ShellException {
        for (int i=0; i< outFile.length;i++) {
            String fname = mopac_path+"/" + outFile[i]; 
            File f = new File(fname);
            if (!f.exists()) continue;
            logger.fine("<outfile name=\""+ fname + "\">");
            try {
                SOMERawReader re = new SOMERawReader(new InputStreamReader(new FileInputStream(f)));
                while (re.hasNext()) {
                	String some = re.nextRecord();
                	mol.setProperty(SOME_RESULT, some);
                }
                re.close();
                f.delete();
            } catch (Exception x) {
                logger.fine("<error name=\""+ x.getMessage() + "\"/>");
                logger.fine("</outfile>");
                throw new ShellException(this,x);
            }
            logger.fine("</outfile>");
        }
		return mol;
	}

	@Override
	protected synchronized IAtomContainer transform(IAtomContainer mol) {
		return mol;
	}
	
	@Override
	public synchronized IAtomContainer runShell(IAtomContainer mol) throws ShellException {
		if (canApply(mol))
			return super.runShell(mol);
		else 
			return mol;
	}	
	protected synchronized boolean canApply(IAtomContainer atomcontainer) throws ShellException {
		if ((atomcontainer==null) || (atomcontainer.getAtomCount()==0)) 
			throw new ShellException(this,"Undefined structure");
        return true;
	}
	@Override
	public String toString() {
		return "SOME";
	}
	@Override
	public synchronized IAtomContainer process(IAtomContainer target)
			throws AmbitException {
		return super.process(target);
	}

}