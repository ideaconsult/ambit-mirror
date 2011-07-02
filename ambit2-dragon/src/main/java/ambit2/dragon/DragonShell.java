package ambit2.dragon;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.io.DelimitedFileFormat;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.io.MDLWriter;

/**
 * Launches Dragon6 as an external executable and reads the results back.
 * @author nina
 *
 */
public class DragonShell extends CommandShell<IAtomContainer, IAtomContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3357377118450137010L;
	
	
	public DragonShell() throws ShellException {
    	super();
	}
	public static final String DRAGON_EXE = "dragon6shell";
	public static final String DRAGON_HOME = "DRAGON_HOME";

    protected String[] inFile = {"allblocks.drs","dragon_input.mol"};
    protected String[] outFile = {"dragon_output.txt"};

    @Override
    protected String getHomeDir(File file) {
    	return System.getProperty("user.home") +"/.ambit2/dragon";
    }
	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		String dragon_home = System.getenv(DRAGON_HOME);
		File exe = new File(String.format("%s/%s", dragon_home,DRAGON_EXE));
		File winexe = new File(String.format("%s/%s.exe", dragon_home,DRAGON_EXE));
		
		if (!exe.exists() && !winexe.exists()) {
			throw new ShellException(this,
				String.format("%s does not exist! Have you set %s environment variable?",
						exe.getAbsolutePath(),DRAGON_HOME));
		}
		addExecutable(CommandShell.os_WINDOWS, winexe.getAbsolutePath(),null);
		addExecutable(CommandShell.os_FreeBSD, exe.getAbsolutePath(),null);
		addExecutable(CommandShell.os_LINUX, exe.getAbsolutePath(),null);
		setInputFile("allblocks.drs");
		setOutputFile("dragon_output.txt");		
	}	
	@Override
	protected synchronized IAtomContainer transform_input(IAtomContainer mol) throws ShellException {
			final String msg="Empty molecule after %s processing"; 
		    if ((mol==null) || (mol.getAtomCount()==0)) throw new ShellException(this,"Empty molecule");
		
	    	String homeDir = getHomeDir(null); // getPath(new File(exe));
	    	File dir = new File(homeDir);
	    	if (!dir.exists()) dir.mkdirs();
	    	
	    	String molfile = String.format("%s/%s",homeDir,inFile[1]);
	    	String txtfile = String.format("%s/%s",homeDir,outFile[0]);
	    	try {
			    MDLWriter writer = new MDLWriter(new FileOutputStream(molfile));
			    writer.writeMolecule(mol);	    		
		        writer.close();
	    	} catch (Exception x) {
	    		throw new ShellException(this,x);
	    	}

    		URL in = DragonShell.class.getClassLoader().getResource("ambit2/dragon/allblocks.drs");
   		
    		String filePath = in.getFile();
	    		
    		byte[] buffer = new byte[(int) new File(filePath).length()];
    	    BufferedInputStream f = null;
    	    try {
    	        f = new BufferedInputStream(new FileInputStream(filePath));
    	        f.read(buffer);
    	    } catch (Exception x) {
    	    	
    	    } finally {
    	        if (f != null) try { f.close(); } catch (IOException ignored) { }
    	    }
    	    String script = new String(buffer);	    		
    	   
    	    FileWriter writer = null;
    	    try {
	    	    writer = new FileWriter(new File(String.format("%s/%s",homeDir,inFile[0])));
	    	    String datePattern = "generation_date=\"%s\"";
	    	    String inPattern = "<molFile value=\"%s\"/>";
	    	    String outPattern = "<SaveFilePath value=\"%s\"/>";
	    	    //lazy xml update
	    	    script = script.replace(datePattern, String.format(datePattern,new Date()));
	    	    script = script.replace(inPattern, String.format(inPattern,molfile));
	    	    script = script.replace(outPattern, String.format(outPattern,txtfile));
	    	    writer.write(script);
	    	    
    	    } catch (Exception x) {
    	    	throw new ShellException(this,"Can't write Dragon script!",x);
    	    } finally {
    	    	if (writer!=null) try { writer.close();} catch (Exception x) {}
    	    }
    	    
    	    
	        for (int i=0; i< outFile.length;i++) {
	            File file = new File(homeDir + "/"+outFile[i]);
	            if (file.exists()) file.delete();
	        }
			return mol;

	}
	@Override
	protected String getPath(File file) {
		return String.format("%s",getHomeDir(null));
	}
	@Override
	protected synchronized List<String> prepareInput(String path, IAtomContainer mol)	throws ShellException {

    	List<String> params = new ArrayList<String>();
    	params.add("-s");
    	params.add(inFile[0]);
		return params;
	}
	@Override
	protected synchronized IAtomContainer parseOutput(String mopac_path, IAtomContainer mol)
			throws ShellException {
		mol.getProperties().clear();
        for (int i=0; i< outFile.length;i++) {
            String fname = mopac_path+"/" + outFile[i]; 
            File f = new File(fname);
            if (!f.exists()) continue;
            logger.debug("<outfile name=\""+ fname + "\">");
            try {
            	IteratingDelimitedFileReader re = new IteratingDelimitedFileReader(new InputStreamReader(new FileInputStream(f)),
                		new DelimitedFileFormat("\t",'"'));
            	while (re.hasNext()) {
            		IAtomContainer m = (IAtomContainer) re.next();
            		mol.setProperties(m.getProperties());
            		break;
            	}
                re.close();
               // f.delete();
            } catch (Exception x) {
                logger.debug("<error name=\""+ x.getMessage() + "\"/>");
                logger.debug("</outfile>");
                throw new ShellException(this,x);
            } finally {
            	
            }
            logger.debug("</outfile>");
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
		return "DRAGON6";
	}
	@Override
	public synchronized IAtomContainer process(IAtomContainer target)
			throws AmbitException {
		return super.process(target);
	}
}
