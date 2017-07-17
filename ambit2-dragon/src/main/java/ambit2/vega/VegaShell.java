package ambit2.vega;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SMILESWriter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
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
	private static Map<String, Property> properties;

	public static Map<String, Property> getProperties() {
		return properties;
	}

	static {
		try {
			properties = loadProperties();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public VegaShell() throws ShellException {
		super();

	}

	protected static synchronized Map<String, Property> loadProperties() {
		Map<String, Property> properties = new HashMap<String, Property>();
		try (InputStream in = VegaShell.class.getClassLoader().getResourceAsStream("ambit2/vega/properties.json")) {
			ObjectMapper m = new ObjectMapper();
			JsonNode root = m.readTree(in);
			Iterator<Map.Entry<String, JsonNode>> pi = root.get("features").fields();
			String version = "https://www.vegahub.eu/";
			try {
				version = root.get("models").get("core").asText();
			} catch (Exception x) {
			}
			while (pi.hasNext()) {
				Map.Entry<String, JsonNode> p = pi.next();

				JsonNode disabled = p.getValue().get("disabled");
				if (disabled != null && disabled.asBoolean())
					continue;

				String creator = p.getValue().get("creator").asText();
				try {
					creator = root.get("models").get(creator).asText();
				} catch (Exception x) {
				}
				ILiteratureEntry ref = LiteratureEntry.getInstance(creator, version);
				// String name = p.getValue().get("title").asText();
				String name = p.getKey();
				String units = p.getValue().get("units").asText();
				String label = p.getValue().get("sameAs").asText();
				Property property = new Property(name, units, ref);
				property.setLabel(label);
				property.setEnabled(true);
				properties.put(p.getKey(), property);
			}

		} catch (Exception x) {

		}
		return properties;
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
		String home = System.getenv(VEGA_HOME);
		if (home == null)
			home = getHomeFromConfig("ambit2/rest/config/ambit2.pref", VegaShell.VEGA_HOME);
		return home;
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
			logger.log(Level.FINE, String.format("%s does not exist! Have you set %s environment variable?",
					exe.getAbsolutePath(), JAVA_HOME));
			// assume there is path set
			exe = new File(JAVA_EXE);
			winexe = new File(String.format("%s.exe", JAVA_EXE));
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
							// skipLines(35); //for all models
							skipLines(13);
						} catch (Exception x) {
							logger.log(Level.WARNING, x.getMessage());
						}
					}
				};
				while (re.hasNext()) {
					IAtomContainer m = (IAtomContainer) re.next();
					for (Entry<Object, Object> e : m.getProperties().entrySet()) {
						if ("-".equals(e.getValue()))
							continue;
						Property p = properties.get(e.getKey().toString().replaceAll("\"", ""));
						if (p == null)
							continue;
						mol.setProperty(p, e.getValue());
					}
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

	public List<Property> createProperties() throws AmbitException {
		List<Property> p = new ArrayList<Property>();
		p.addAll(getProperties().values());
		return p;
	}
}
