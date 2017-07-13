package ambit2.vega;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SMILESWriter;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.DelimitedFileFormat;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.dragon.AbstractDescriptorShell;
import net.idea.modbcum.i.exceptions.AmbitException;

/**
 * Launches VEGA as external executable
 * 
 * @author nina
 *
 */
public class VegaShell extends AbstractDescriptorShell {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4109994330375555731L;

	public VegaShell() throws ShellException {
		super();
		// descriptors = new DragonDescriptorDictionary();
	}

	public static final String JAVA_EXE = "java";
	public static final String VEGA_HOME = "VEGA_HOME";
	public static final String JAVA_HOME = "JAVA_HOME";

	protected String[] inFile;
	protected String[] outFile;

	@Override
	protected String getHomeDir(File file) {
		return String.format("%s%s.ambit2%svega", System.getProperty("user.home"), File.separator, File.separator);
	}

	@Override
	protected String getHome() throws ShellException {
		return System.getenv(VEGA_HOME);
	}

	@Override
	protected synchronized List<String> prepareInput(String path, IAtomContainer mol) throws ShellException {

		List<String> params = new ArrayList<String>();
		params.add(String.format("-Duser.dir=%s", getHomeDir(null)));
		params.add("-jar");
		params.add(String.format("%s%sVEGA-CLI.jar", getHome(), File.separator));
		params.add("-script");
		params.add(String.format("%s%s%s", getHomeDir(null), File.separator, inFile[0]));
		return params;
	}

	@Override
	protected void initialize() throws ShellException {
		super.initialize();

		inFile = new String[] { "script.xml", "vega_input.smi" };
		outFile = new String[] { "results.txt" };

		File exe = new File(String.format("%s/bin/%s", System.getenv(JAVA_HOME), JAVA_EXE));
		File winexe = new File(String.format("%s/bin/%s.exe", System.getenv(JAVA_HOME), JAVA_EXE));

		if (!exe.exists() && !winexe.exists()) {
			throw new ShellException(this, String.format("%s does not exist! Have you set %s environment variable?",
					exe.getAbsolutePath(), JAVA_HOME));
		}
		addExecutable(CommandShell.os_WINDOWS, winexe.getAbsolutePath(), null);
		addExecutable(CommandShell.os_WINDOWS7, winexe.getAbsolutePath(), null);
		addExecutable(CommandShell.os_WINDOWSVISTA, winexe.getAbsolutePath(), null);
		addExecutable(CommandShell.os_FreeBSD, exe.getAbsolutePath(), null);
		addExecutable(CommandShell.os_LINUX, exe.getAbsolutePath(), null);
		addExecutable(CommandShell.os_LINUX64, exe.getAbsolutePath(), null);
		setInputFile(String.format("%s/%s", getHomeDir(null), inFile[1]));
		setOutputFile(String.format("%s/%s", getHome(), outFile[0]));
	}

	@Override
	protected synchronized IAtomContainer transform_input(IAtomContainer mol) throws ShellException {
		final String msg = "Empty molecule after %s processing";
		if ((mol == null) || (mol.getAtomCount() == 0))
			throw new ShellException(this, "Empty molecule");

		String homeDir = getHomeDir(null);
		File dir = new File(homeDir);
		if (!dir.exists())
			dir.mkdirs();

		String molfile = String.format("%s%s%s", homeDir, File.separator, inFile[1]);
		String predfile = String.format("%s%s%s", homeDir, File.separator, outFile[0]);
		try (SMILESWriter writer = new SMILESWriter(new FileOutputStream(molfile))) {
			writer.write(mol);
		} catch (Exception x) {
			throw new ShellException(this, x);
		}
		// reading the script from resource
		String script = null;
		try (InputStream in = VegaShell.class.getClassLoader().getResourceAsStream("ambit2/vega/script.xml")) {
			script = String.format(IOUtils.toString(in), molfile, predfile);
		} catch (Exception x) {
			throw new ShellException(this, x);
		}
		// writing the script with proper file name
		try (FileWriter writer = new FileWriter(new File(String.format("%s/%s", homeDir, inFile[0])))) {
			writer.write(script);
		} catch (Exception x) {
			throw new ShellException(this, String.format("Can't write Vega script! %s", x.getMessage()));
		}

		for (int i = 0; i < outFile.length; i++) {
			File file = new File(homeDir + "/" + outFile[i]);
			if (file.exists())
				file.delete();
		}
		return mol;
	}

	@Override
	protected IAtomContainer parseOutput(String path, IAtomContainer mol) throws ShellException {
		// mol.getProperties().clear();
		MoleculeTools.clearProperties(mol);
		for (int i = 0; i < outFile.length; i++) {
			String fname = path + "/" + outFile[i];
			File f = new File(fname);
			if (!f.exists())
				continue;
			logger.fine("<outfile name=\"" + fname + "\">");
			try {
				IteratingDelimitedFileReader re = new IteratingDelimitedFileReader(
						new InputStreamReader(new FileInputStream(f)), new DelimitedFileFormat("\t", '"')) {
					@Override
					public void setReader(Reader reader) throws CDKException {
						super.setReader(reader);
						try {
							skipLines(35);
						} catch (Exception x) {
							logger.log(Level.WARNING, x.getMessage());
						}
					}
				};
				while (re.hasNext()) {
					IAtomContainer m = (IAtomContainer) re.next();

					mol.setProperties(m.getProperties());
					break;
				}
				re.close();
				// f.delete();
			} catch (Exception x) {
				logger.fine("<error name=\"" + x.getMessage() + "\"/>");
				logger.fine("</outfile>");
				throw new ShellException(this, x);
			} finally {

			}
			logger.fine("</outfile>");
		}
		return mol;
	}

	@Override
	public String toString() {
		return "VEGA";
	}

	@Override
	public synchronized IAtomContainer process(IAtomContainer target) throws AmbitException {
		return super.process(target);
	}
}
