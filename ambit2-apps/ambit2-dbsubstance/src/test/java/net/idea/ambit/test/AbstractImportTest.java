package net.idea.ambit.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.PropertyConfigurator;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ambit2.core.io.FileState;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.dbsubstance.DBSubstanceImport;

public abstract class AbstractImportTest extends DbUnitTest {

	static final String loggingProperties = "config/logging.properties";
	static final String log4jProperties = "config/log4j.properties";
	protected boolean addDefaultComposition = false;
	protected ResourceBundle dataResources;

	protected abstract ResourceBundle getNanoDataProperties();

	protected ResourceBundle getNanoDataProperties(String resource) {
		return ResourceBundle.getBundle(resource, Locale.ENGLISH);
	}

	protected abstract String[] getResourcesList();

	protected String getPrefix() {
		return "XLSX";
	}

	protected abstract String getDirPrefix();

	@Before
	public void init() throws Exception {
		InputStream in = null;
		try {
			URL url = getClass().getClassLoader().getResource(loggingProperties);
			System.setProperty("java.util.logging.config.file", url.getFile());
			in = new FileInputStream(new File(url.getFile()));
			LogManager.getLogManager().readConfiguration(in);
			logger.log(Level.INFO, String.format("Logging configuration loaded from %s", url.getFile()));
		} catch (Exception x) {
			System.err.println("logging configuration failed " + x.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception x) {
			}
		}

		// now log4j for those who use it
		in = null;
		try {
			in = DBSubstanceImport.class.getClassLoader().getResourceAsStream(log4jProperties);
			PropertyConfigurator.configure(in);

		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage());
		} finally {
			try {
				in.close();
			} catch (Exception x) {
			}
		}
		dataResources = getNanoDataProperties();
	}

	private static final String _root = "root";
	private static final String _singledb = "singledb";
	private static final String _release = "release";

	@Test
	public void test() throws Exception {
		if (dataResources != null)
			testResources(dataResources, getPrefix());
	}

	public int testResources(ResourceBundle nanodataResources) throws Exception {
		return testResources(nanodataResources, getPrefix());
	}

	protected List<File> allFiles = null;

	protected String[] splitPath(String root, String data) {
		String sep = System.getProperty("file.separator");
		String pattern = Pattern.quote(sep);
		String r = root.replace("//", sep).replace("/", sep);
		return data.replace(r, "").split(pattern);
	}

	@Test
	public void countImportSources() throws Exception {
		allFiles = null;
		final SuffixFileFilter filefilter = new SuffixFileFilter(new String[] { "xls", "xlsx" });

		String[] resources = getResourcesList();
		if (resources == null)
			throw new Exception("Undefined resources");
		int c = 0;
		String root = null;
		try (BufferedWriter log = new BufferedWriter(new FileWriter(String.format("../import_%s.log", getPrefix())))) {
			for (String resource : resources) {
				ResourceBundle nanodataResources = getNanoDataProperties(resource);
				root = nanodataResources.getString(_root);
				if (allFiles == null)
					allFiles = (List<File>) FileUtils.listFiles(new File(root, getDirPrefix()), filefilter,
							new IOFileFilter() {

								@Override
								public boolean accept(File dir, String name) {
									File tag = new File(dir, "donotimport.properties");
									return !tag.exists();
								}

								@Override
								public boolean accept(File file) {
									return filefilter.accept(file) && !"index.xlsx".equals(file.getName());
								}
							});

				c += testResources(nanodataResources, getPrefix(), true, new IProcessPair() {
					@Override
					public void process(String prefix, String root, File spreadsheet, File json) {
						try {
							if (allFiles.contains(spreadsheet))
								allFiles.remove(spreadsheet);

							String[] l = splitPath(root, spreadsheet.getAbsolutePath());

							log.write(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\n", prefix, l[1], l[2],
									l.length > 4 ? l[3] : "", spreadsheet, json, "IMPORTED"));
						} catch (IOException x) {
							x.printStackTrace();
						}

					}
				});

				log.flush();
			}

			SuffixFileFilter jsonfilter = new SuffixFileFilter(new String[] { "json" });
			List<File> jsonfiles = (List<File>) FileUtils.listFiles(new File(root, getDirPrefix()), jsonfilter,
					TrueFileFilter.INSTANCE);
			if (allFiles != null)
				for (File spreadsheet : allFiles) {
					File jsonFile = new File(spreadsheet.toString().replace(".xlsx", ".json").replace(".xls", ".json"));
					if (!jsonfiles.contains(jsonFile))
						jsonFile = null;
					else if (!jsonFile.exists())
						jsonFile = null;
					String[] l = splitPath(root, spreadsheet.getAbsolutePath());
					log.write(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\n", getPrefix(), l[1], l[2],
							l.length > 4 ? l[3] : "", spreadsheet.getAbsoluteFile(),
							jsonFile == null ? "" : jsonFile.getAbsoluteFile(),
							jsonFile == null ? "JSON CONFIG NOT READY" : "JSON CONFIG READY"));
				}
			log.flush();

		}

	}

	public int testResources(ResourceBundle nanodataResources, String prefix) throws Exception {
		return testResources(nanodataResources, prefix, false, null);
	}

	public int testResources(ResourceBundle nanodataResources, String prefix, boolean dryRun, IProcessPair processpair)
			throws Exception {
		Assert.assertNotNull(nanodataResources);
		final String root = nanodataResources.getString(_root);
		boolean singledb = Boolean.parseBoolean(nanodataResources.getString(_singledb));
		String release = nanodataResources.getString(_release);
		Enumeration<String> enumeration = nanodataResources.getKeys();

		int count = 0;
		int substance = 0;
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			if (_root.equals(key))
				continue;
			if (_singledb.equals(key))
				continue;
			if (_release.equals(key))
				continue;
			// hack to have one file with multiple json configs - the keys
			// should be unique, but files the same
			int ix = key.indexOf("#");
			File file = new File(root, (ix > 0) ? key.substring(0, ix) : key);
			if (file.exists()) {
				logger.log(Level.INFO, String.format("Directory:%s\t%s%s\t%s", file.isDirectory(), root, key,
						nanodataResources.getString(key)));
				if (file.isDirectory()) {
					if (new File(file, "donotimport.properties").exists())
						continue;
					File json = new File(file, nanodataResources.getString(key));
					if (!json.exists()) {
						logger.log(Level.WARNING, String.format("%s not found", json.getAbsolutePath()));
						json = null;
					}
					for (File data : file.listFiles())
						if (acceptDataFile(data))
							if (dryRun && processpair != null)
								processpair.process(prefix, root, data,
										json == null ? getJsonDataName(data, nanodataResources.getString(key)) : json);
							else
								try {
									substance += importFile(data, json == null
											? getJsonDataName(data, nanodataResources.getString(key)) : json, prefix,
											!singledb, release);
								} catch (Exception x) {
									logger.log(Level.WARNING, x.getMessage(), x);
								}
				} else {
					if (acceptDataFile(file)) {
						File json = getJsonDataName(file.getParentFile(), nanodataResources.getString(key));
						if (json == null)
							continue;
						if (!json.exists()) {
							logger.log(Level.WARNING, String.format("%s not found", json.getAbsolutePath()));
						} else {
							if (dryRun)
								processpair.process(prefix, root, file, json);
							else
								substance += importFile(file, json, prefix, !singledb, release);
						}
					}
				}
				count++;
			} else
				logger.log(Level.WARNING, String.format("%s\t%s", file.getAbsolutePath(), "not found"));
		}
		if (!dryRun)
			return verifyFiles(count);
		else
			return count;
	}

	protected File getJsonDataName(File file, String jsonname) {
		File json = new File(file, jsonname);
		if (!json.exists()) {
			json = changeFileExtension(file, ".json");
		}
		return json;
	}

	protected boolean acceptDataFile(File data) {
		return (FileState._FILE_TYPE.XLS_INDEX.hasExtension(data)
				|| FileState._FILE_TYPE.XLSX_INDEX.hasExtension(data));
	}

	protected File changeFileExtension(File data, String newextension) {
		if (FileState._FILE_TYPE.XLS_INDEX.hasExtension(data))
			return new File(data.getAbsolutePath().replaceAll(".xls", newextension));
		else if (FileState._FILE_TYPE.XLS_INDEX.hasExtension(data))
			return new File(data.getAbsolutePath().replaceAll(".xlsx", newextension));
		return null;
	}

	protected int verifyImport(File spreadsheet, File json, IDatabaseConnection c) throws Exception {
		try {

			ITable table = c.createQueryTable("EXPECTED",
					"SELECT name,publicname,owner_name,substanceType as c FROM substance");
			int r = table.getRowCount();
			table = c.createQueryTable("EXPECTED",
					"SELECT name,publicname,owner_name,substanceType as c FROM substance where substanceType = ''");
			Assert.assertEquals(String.format("Missing substance types\t%s\t%s", spreadsheet.getName(), json.getName()),
					0, table.getRowCount());
			return r;
		} finally {
			c.close();
		}
	}

	protected abstract int importFile(File spreadsheet, File json, String prefix, boolean resetdb, String release)
			throws Exception;

	protected String[] getImportOptions(File spreadsheet, File json, String prefix, String release) {
		URL propertiesFile = this.getClass().getClassLoader().getResource("config/ambit.properties");
		Assert.assertNotNull(propertiesFile);
		Assert.assertTrue(new File(propertiesFile.getFile()).exists());
		return new String[] { "-i", spreadsheet.getAbsolutePath(), "-j", json.getAbsolutePath(), "-c",
				propertiesFile.getFile(), "-m", "false", "-t", "true", "-p", "xls", "-e", prefix, "-d",
				Boolean.toString(addDefaultComposition), "-a", release };
	}

	protected int verifyFiles(int count) throws Exception {
		Assert.assertTrue(count > 0);
		return count;
	}

	protected void storeUndefined(Properties properties, String type, String comments) {
		File fileparams = new File(String.format("%s-undefined_%s.properties", getPrefix(), type.replace(" ", "_")));
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileparams),
				Charset.forName("UTF-8"))) {
			properties.store(writer, String.format("%s-undefined %s\t%s", getPrefix(), type, comments));
		} catch (Exception x) {
			x.printStackTrace();
		}
		System.out.println(fileparams.getAbsolutePath());
	}

	protected void testEndpoints(IDatabaseConnection c) throws Exception {
		ITable table = c.createQueryTable("EXPECTED",
				"SELECT p.topcategory,p.endpointcategory,name,publicname,guidance,e.endpoint FROM substance s "
						+ "join substance_protocolapplication p on uuid=substance_uuid join substance_experiment e using(document_uuid)"
						+ "group  by p.topcategory,p.endpointcategory,guidance,e.endpoint");
		for (int i = 0; i < table.getRowCount(); i++) {
			Object endpoint = table.getValue(i, "endpoint");
			System.out.println(String.format("%s\t%s\t%s", table.getValue(i, "endpointcategory"),
					table.getValue(i, "guidance"), table.getValue(i, "endpoint")));
			System.out.println(endpoint);
		}
	}

	protected void testParams(IDatabaseConnection c, String prmname, int expected) throws Exception {
		testParams(c, prmname, expected, false);
	}

	protected void testConditions(IDatabaseConnection c, String prmname, int expected, boolean numeric)
			throws Exception {
		String query = String.format(
				"SELECT count(*) c,JSON_EXTRACT(replace(conditions,'E.','E_'),'$.%s') ref FROM substance_experiment e group by ref",
				prmname.replace("E.", "E_"));
		ITable table = c.createQueryTable("EXPECTED", query);
		System.out.println(query);
		if (!numeric)
			for (int i = 0; i < table.getRowCount(); i++) {
				Object item = table.getValue(i, "ref");
				if (item != null)
					Assert.assertFalse(item.toString(), item.toString().indexOf("loValue") >= 0);
			}
		Assert.assertEquals(expected, table.getRowCount());
	}

	protected void testParams(IDatabaseConnection c, String prmname, int expected, boolean numeric) throws Exception {
		String query = String.format(
				"SELECT count(*) c,JSON_EXTRACT(replace(params,'E.','E_'),'$.%s') ref FROM substance s join substance_protocolapplication p on uuid=substance_uuid join substance_experiment e using(document_uuid) group by ref",
				prmname.replace("E.", "E_"));
		ITable table = c.createQueryTable("EXPECTED", query);
		if (!numeric)
			for (int i = 0; i < table.getRowCount(); i++) {
				Object animal = table.getValue(i, "ref");
				if (animal != null)
					Assert.assertFalse(animal.toString(), animal.toString().indexOf("loValue") >= 0);
			}
		Assert.assertEquals(expected, table.getRowCount());
	}

	protected void testPublicnames(IDatabaseConnection c) throws Exception {
		ITable table = c.createQueryTable("EXPECTED",
				"SELECT publicname,name,substring(content,1,100) c FROM substance	group by publicname,name order by publicname");
		int empty = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			Object pname = table.getValue(i, "publicname");
			if (pname == null || "".equals(pname.toString().trim())) {
				System.out.println(String.format("%s\t%s\t%s", table.getValue(i, "publicname"),
						table.getValue(i, "name"), table.getValue(i, "c")));
				empty++;
			}
		}
		Assert.assertEquals(0, empty);
	}

}

interface IProcessPair {
	public void process(String prefix, String root, File spreadsheet, File json);
}
