package ambit2.core.smiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.base.external.ShellException;

public class OpenBabelGen3D extends OpenBabelAbstractShell<IAtomContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9048435750478862463L;

	public OpenBabelGen3D() throws ShellException {
		super();
		setOutputFile(getOutputFileName());
		setInputFile(outputFile.replace("o_bbl", "i_bbl"));
		setReadOutput(true);
	}
	
	@Override
	protected String getOBabelHome() throws ShellException {
		try {
			AMBITConfigProperties properties = new AMBITConfigProperties();
			String wheredragonlives = properties.getPropertyWithDefault(OpenBabelGen3D.OBABEL_HOME,AMBITConfigProperties.ambitProperties,null);
			if (wheredragonlives==null) 
				throw new ShellException(null,String.format("Can't find where OpenBabel is located. No property %s in %s",OpenBabelGen3D.OBABEL_HOME,AMBITConfigProperties.ambitProperties));
			return wheredragonlives;
		} catch (ShellException x) {
			throw x;
		} catch (Exception x) {
			throw new ShellException(null,x);
		}
	}

	@Override
	protected String getOutputFileName() {
		return "o_bbl" + UUID.randomUUID().toString() + ".sdf";
	}
	@Override
	protected String getOutputOption() {
		return "-osdf";
	}
	
	@Override
	protected synchronized java.util.List<String> prepareInput(String path, IAtomContainer mol) throws ShellException {

		try {
	    	String homeDir = getHomeDir(null); // getPath(new File(exe));
	    	File dir = new File(homeDir);
	    	if (!dir.exists()) dir.mkdirs();
	    	
	    	String inFile = writeInputSDF(path,mol);
	    	
	    	String outFile = String.format("%s%s%s",homeDir,File.separator,getOutputFile());
	    	
			List<String> list = new ArrayList<String>();
			list.add("--gen3d");
			list.add("-isdf");
			list.add(inFile);
			list.add(getOutputOption());
			list.add("-O");
			list.add(outFile);
			return list;
		} catch (Exception x) {
			throw new ShellException(this,x);
		}
	}	
	
	@Override
	protected synchronized IAtomContainer parseOutput(String path,
			IAtomContainer mol) throws ShellException {
		File out = new File(String.format("%s%s%s", path, File.separator,getOutputFile()));
		IAtomContainer newmol = super.parseOutput(path, mol);
		try {
			File file = new File(String.format("%s%s%s", path, File.separator,	getInputFile()));
			file.delete();
		} catch (Exception x) {	}
		try {	File file = out; file.delete();	} catch (Exception x) {}
		return newmol;
	}
}
