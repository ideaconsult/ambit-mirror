package ambit2.dbcli;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ConnectException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.c.DBConnectionConfigurable;
import net.idea.modbcum.c.MySQLSingleConnection;
import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.LoginInfo;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.batch.IBatchStatistics.RECORDS_STATS;
import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.modbcum.q.update.AbstractUpdate;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.JsonNode;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.fingerprint.ICountFingerprint;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IChemical;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.io.FileInputState;
import ambit2.core.io.FileOutputState;
import ambit2.core.io.FilesWithHeaderWriter;
import ambit2.core.io.IRawReader;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.io.fp.MultiFingerprintsWriter;
import ambit2.core.processors.StructureNormalizer;
import ambit2.core.processors.structure.InchiProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.key.NoneKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.db.DbReader;
import ambit2.db.processors.AbstractRepositoryWriter.OP;
import ambit2.db.processors.AbstractUpdateProcessor;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.QuickImportBatchProcessor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.structure.FingerprintsByStatus;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.db.search.structure.MissingInChIsQuery;
import ambit2.db.update.chemical.UpdateChemical;
import ambit2.db.update.qlabel.smarts.SMARTSAcceleratorWriter;
import ambit2.dbcli.CliOptions._commandmode;
import ambit2.dbcli.CliOptions._preprocessingoptions;
import ambit2.dbcli.CliOptions._subcommandmode;
import ambit2.dbcli.descriptors.AtomEnvironmentGeneratorApp;
import ambit2.dbcli.exceptions.InvalidCommand;
import ambit2.descriptors.processors.BitSetGenerator;
import ambit2.descriptors.processors.FPTable;
import ambit2.descriptors.simmatrix.SimilarityMatrix;
import ambit2.smarts.processors.SMARTSPropertiesGenerator;
import ambit2.smarts.processors.SMIRKSProcessor;
import ambit2.tautomers.processor.StructureStandardizer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.mysql.jdbc.CommunicationsException;

/**
 * 
 * @author nina
 * 
 */
public class AmbitCli {
	static Logger logger_cli = Logger.getLogger("ambitcli", "ambit2.dbcli.msg");
	static final String loggingProperties = "ambit2/dbcli/logging.properties";
	static final String log4jProperties = "ambit2/dbcli/log4j.properties";

	CliOptions options;

	static {
		logger_cli = Logger.getLogger("ambitcli", "ambit2.dbcli.msg");
		Locale.setDefault(Locale.ENGLISH);
		String dOption = System.getProperty("java.util.logging.config.file");
		if (dOption == null || "".equals(dOption)) {
			InputStream in = null;
			try {
				in = AmbitCli.class.getClassLoader().getResourceAsStream(
						loggingProperties);
				LogManager.getLogManager().readConfiguration(in);

			} catch (Exception x) {
				logger_cli.log(Level.WARNING, x.getMessage());
			} finally {
				try {
					in.close();
				} catch (Exception x) {
				}
			}
		}
		// now log4j for those who use it
		InputStream in = null;
		try {
			in = AmbitCli.class.getClassLoader().getResourceAsStream(
					log4jProperties);
			PropertyConfigurator.configure(in);

		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage());
		} finally {
			try {
				in.close();
			} catch (Exception x) {
			}
		}

	}

	public AmbitCli(CliOptions options) {
		this.options = options;
	}

	public long go(String command, String subcommand) throws Exception {
		try {
			inchi_warmup(3);
		} catch (Exception x) {
			logger_cli.log(Level.SEVERE, "MSG_INCHI",
					new Object[] { "ERROR", x.getMessage() });
		}
		long now = System.currentTimeMillis();
		_commandmode cmd = null;
		try {
			cmd = _commandmode.valueOf(command);
		} catch (Exception x) {
			if ("import".equals(command))
				cmd = _commandmode.imports;
			else
				return -1;
		}
		switch (cmd) {
		case imports: {
			parseCommandImport();
			break;
		}
		case preprocessing: {
			parseCommandPreprocessing();
			break;
		}
		case dataset: {
			return parseCommandDataset(now);
		}
		case split: {
			return parseCommandSplit(subcommand, now);
		}
		case standardize: {
			parseCommandStandardize(subcommand, now);
			break;
		}
		case simmatrix: {
			parseCommandSimilarityMatrix(subcommand, now);
			break;
		}
		case fingerprint: {
			parseCommandFingerprints(subcommand, now);
		}
		case atomenvironments: {
			return parseCommandAE(subcommand, now);
		}
		case dbmigrate: {
			parseDBMigrate(subcommand, now);
		}
		default:
			break;
		}
		return -1;
	}

	public static void main(String[] args) {
		logger_cli.log(Level.INFO, "MSG_INFO_VERSION");
		long now = System.currentTimeMillis();
		int code = 0;
		try {
			CliOptions options = new CliOptions();
			if (options.parse(args)) {
				AmbitCli navigator = new AmbitCli(options);
				logger_cli.log(
						Level.INFO,
						"MSG_INFO_RUNNING",
						new Object[] {
								options.getCommand().get("description"),
								options.getConfigFile() == null ? "" : String
										.format("-c \"%s\"",
												options.getConfigFile()),
								options.input == null ? "" : String.format(
										"-i \"%s\"", options.input),
								options.output == null ? "" : String.format(
										"-o \"%s\"", options.output),
								options.printParameters(options.getCommand()
										.get("name").asText(),
										options.getSubcommand()) });
				navigator.go(options.getCmd(), options.getSubcommand().name());
			} else {
				code = -1;
			}
		} catch (ConnectException x) {
			logger_cli.log(Level.SEVERE, "MSG_CONNECTION_REFUSED",
					new Object[] { x.getMessage() });
			Runtime.getRuntime().runFinalization();
			code = -1;
		} catch (CommunicationsException x) {
			logger_cli.log(Level.SEVERE, "MSG_ERR_CONNECTION_FAILED",
					new Object[] { x.getMessage() });
			code = -1;
		} catch (SQLException x) {
			logger_cli.log(Level.SEVERE, "MSG_ERR_SQL",
					new Object[] { x.getMessage() });
			code = -1;
		} catch (InvalidCommand x) {
			logger_cli.log(Level.SEVERE, "MSG_INVALIDCOMMAND",
					new Object[] { x.getMessage() });
			code = -1;
		} catch (FileNotFoundException x) {
			logger_cli.log(Level.SEVERE, "MSG_INVALIDFILE",
					new Object[] { x.getMessage() });
			code = -1;
		} catch (Exception x) {
			logger_cli.log(Level.SEVERE, "MSG_ERR",
					new Object[] { x.getMessage() });
			code = -1;
		} finally {
			if (code >= 0)
				logger_cli.log(Level.INFO, "MSG_INFO_COMPLETED",
						(System.currentTimeMillis() - now));
		}

		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().exit(code);
	}

	protected RepositoryWriter initWriter(RepositoryWriter writer,
			PropertyKey key, SourceDataset dataset) throws Exception {

		try {
			if (writer != null) {
				logger_cli.log(Level.FINE, "MSG_FINE_CONNCLOSE");
				writer.close();
			}
			writer = null;
		} catch (Exception xx) {
			logger_cli.log(Level.WARNING, xx.getMessage(), xx);
		}

		logger_cli.log(Level.FINE, "MSG_FINE_CONNOPEN");

		Connection c = null;
		DBConnectionConfigurable<Context> dbc = null;
		dbc = getConnection(options.getSQLConfig());
		c = dbc.getConnection();
		c.setAutoCommit(true);
		writer = new RepositoryWriter();
		if (key != null)
			writer.setPropertyKey(key);
		writer.setDataset(dataset);
		writer.setConnection(c);
		writer.setCloseConnection(true);
		writer.open();
		logger_cli.log(Level.FINE, "MSG_FINE_CONNOPENED");
		return writer;
	}

	public long write(IRawReader<IStructureRecord> reader, PropertyKey key,
			SourceDataset dataset, int maxrecords) throws Exception {

		RepositoryWriter writer = null;
		long records = 0;
		long now = System.currentTimeMillis();
		long start = now;
		long connectionOpened = now;
		try {
			writer = initWriter(writer, key, dataset);
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();

				try {
					writer.write(record);
				} catch (SQLException x) {

					try {
						writer = initWriter(writer, key, dataset);
						connectionOpened = System.currentTimeMillis();
					} catch (Exception xx) {
						writer = null;
						throw xx;
					}
				} catch (Exception x) {
					if (writer == null)
						throw x;
				}
				records++;

				if ((System.currentTimeMillis() - connectionOpened) > options.connectionLifeTime) // restart
																									// the
																									// connection
					try {
						writer = initWriter(writer, key, dataset);
						connectionOpened = System.currentTimeMillis();
					} catch (Exception xx) {
						writer = null;
						throw xx;
					}

				if ((records % 1000) == 0) {
					now = System.currentTimeMillis();
					logger_cli.info(String.format(
							"Records read %d ; %f msec per record\t", records,
							((now - start) / 1000.0)));
					start = now;
				}
				if (maxrecords <= 0 || (records <= maxrecords)) {
					continue;
				} else
					break;
			}
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				reader.close();
			} catch (Exception x) {
			}
			try {
				writer.close();
			} catch (Exception x) {
			}
		}

		return records;
	}

	protected DBConnectionConfigurable<Context> getConnection(String configFile)
			throws SQLException, AmbitException {
		try {
			Context context = initContext();
			String driver = context.get(Preferences.DRIVERNAME);

			if ((driver != null) && (driver.contains("mysql"))) {
				MySQLSingleConnection<Context> mc = new MySQLSingleConnection<Context>(
						context, configFile) {
					@Override
					protected void configurefromContext(LoginInfo li,
							Context context) {

						if (context.get(Preferences.DATABASE) != null)
							li.setDatabase(context.get(Preferences.DATABASE));
						if (context.get(Preferences.USER) != null)
							li.setUser(context.get(Preferences.USER));
						if (context.get(Preferences.PASSWORD) != null)
							li.setPassword(context.get(Preferences.PASSWORD));
						if (context.get(Preferences.HOST) != null)
							li.setHostname(context.get(Preferences.HOST));
						if (context.get(Preferences.PORT) != null)
							li.setPort(context.get(Preferences.PORT));
						if (context.get(Preferences.DRIVERNAME) != null)
							li.setDriverClassName(context
									.get(Preferences.DRIVERNAME));
					}

					@Override
					protected Logger getLogger() {
						return logger_cli;
					}
				};
				return mc;
			} else
				throw new AmbitException("Driver not supported");
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	protected synchronized Context initContext() throws Exception {
		if ((options.getConfigFile() == null)
				&& (options.getSQLConfig() == null))
			return new Context();

		InputStream in = null;
		try {
			Properties properties = new Properties();
			if (options.getConfigFile() != null)
				in = new FileInputStream(options.getConfigFile());
			else
				in = getClass().getClassLoader().getResourceAsStream(
						options.getSQLConfig());
			properties.load(in);
			Context context = new Context();
			context.put(Preferences.DATABASE,
					properties.get(Preferences.DATABASE).toString());
			context.put(Preferences.USER, properties.get(Preferences.USER)
					.toString());
			context.put(Preferences.PASSWORD,
					properties.get(Preferences.PASSWORD).toString());
			context.put(Preferences.HOST, properties.get(Preferences.HOST)
					.toString());

			context.put(Preferences.PORT, properties.get(Preferences.PORT)
					.toString());
			context.put(Preferences.DRIVERNAME,
					properties.get(Preferences.DRIVERNAME).toString());
			return context;

		} catch (IOException x) {
			throw x;
		} finally {
			try {
				in.close();
			} catch (Exception x) {
			}
		}
	}

	protected void disableIndices(Connection connection) throws SQLException {
		Statement t = null;
		try {
			t = connection.createStatement();
			t.addBatch("SET FOREIGN_KEY_CHECKS = 0;");
			t.addBatch("SET UNIQUE_CHECKS = 0;");
			t.addBatch("SET AUTOCOMMIT = 0;");
			t.executeBatch();
			logger_cli.log(Level.FINE, "MSG_FINE_INDEXDISABLED");
		} catch (SQLException x) {
			logger_cli.log(Level.WARNING, "MSG_ERR_INDEX", new Object[] {
					"disabling", x.getMessage() });

			throw x;
		} finally {
			try {
				if (t != null)
					t.close();
			} catch (Exception x) {
			}
		}
	}

	protected void enableIndices(Connection connection) throws SQLException {
		Statement t = null;
		try {
			t = connection.createStatement();
			t.addBatch("SET UNIQUE_CHECKS = 1;");
			t.addBatch("SET FOREIGN_KEY_CHECKS = 1;");
			t.executeBatch();
			connection.commit();
			logger_cli.log(Level.FINE, "MSG_FINE_INDEXENABLED");
		} catch (SQLException x) {
			logger_cli.log(Level.WARNING, "MSG_ERR_INDEX", new Object[] {
					"enabling", x.getMessage() });
			throw x;
		} finally {
			try {
				if (t != null)
					t.close();
			} catch (Exception x) {
			}
		}
	}

	protected long parseCommandDataset(long now) throws Exception {
		RawIteratingSDFReader reader = null;
		try {
			File file = new File(options.input);

			reader = new RawIteratingSDFReader(new FileReader(file));
			reader.setReference(LiteratureEntry.getInstance(file.getName()));
			SourceDataset dataset = new SourceDataset(file.getName(),
					LiteratureEntry.getInstance("File", file.getName()));
			return write(reader, new NoneKey(), dataset, -1);
		} catch (Exception x) {
			throw x;
		} finally {
			logger_cli.log(Level.INFO, "MSG_INFO_COMPLETED",
					(System.currentTimeMillis() - now));
			try {
				if (reader != null)
					reader.close();
			} catch (Exception x) {
			}
			if (options.output != null) {
				logger_cli.log(Level.INFO, "MSG_INFO_RESULTSWRITTEN",
						options.output);
			}
		}
	}

	protected long parseCommandAE(String subcommand, long now) throws Exception {

		AtomEnvironmentGeneratorApp test = new AtomEnvironmentGeneratorApp(
				logger_cli);
		String file = options.input;
		String outdir = options.output;

		String id_tag = null;
		String activityTag = null;
		String mergeResultsFile = null;
		boolean generate_csv = false;
		boolean generate_mm = false;
		boolean generate_json = false;
		boolean normalize = true;
		Double laplace_smoothing = null;
		boolean cost_sensitive = true;
		boolean levels_as_namespace = false;

		try {
			Object o = options.getParam(":id_tag");
			if (o != null)
				id_tag = o.toString();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":activity_tag");
			if (o != null)
				activityTag = o.toString();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":merge_results_file");
			if (o != null)
				mergeResultsFile = o.toString();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":generate_csv");
			if (o != null)
				generate_csv = ((Boolean) o).booleanValue();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":generate_mm");
			if (o != null)
				generate_mm = ((Boolean) o).booleanValue();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":generate_json");
			if (o != null)
				generate_json = ((Boolean) o).booleanValue();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":normalize");
			if (o != null)
				normalize = ((Boolean) o).booleanValue();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":laplace_smoothing");
			if (o != null)
				laplace_smoothing = ((Double) o).doubleValue();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":cost_sensitive");
			if (o != null)
				cost_sensitive = ((Boolean) o).booleanValue();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			Object o = options.getParam(":levels_as_namespace");
			if (o != null)
				levels_as_namespace = ((Boolean) o).booleanValue();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		try {
			if (outdir == null)
				throw new Exception("Output folder not specified.");
			if (file == null)
				throw new Exception("SDF file not specified.");
			return test.runAtomTypeMatrixDescriptor(outdir, new File(file),
					id_tag, activityTag, mergeResultsFile, !generate_csv,
					!generate_mm, !generate_json, normalize, laplace_smoothing,
					cost_sensitive, levels_as_namespace);
		} catch (Exception x) {
			throw x;
		}
	}

	protected long parseCommandSplit(String subcommand, long now)
			throws Exception {
		RawIteratingSDFReader reader = null;
		Writer writer = null;
		long chunksize = 10000;

		JsonNode scmd = options.command.get(subcommand);
		try {
			JsonNode scommand = scmd.get("params");
			JsonNode chunkNode = scommand.get(":chunk");
			chunksize = Long.parseLong(chunkNode.get("value").getTextValue());
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		}

		int chunk = 1;
		long chunk_started = System.currentTimeMillis();
		try {
			File file = new File(options.input);
			File outdir = new File(options.output);
			logger_cli.log(
					Level.INFO,
					"MSG_INFO_COMMAND_SPLIT",
					new Object[] { file.getAbsoluteFile(), chunksize,
							outdir.getAbsolutePath() });

			if (outdir.exists() && outdir.isDirectory()) {
				reader = new RawIteratingSDFReader(new FileReader(file));
				File outfile = new File(outdir, String.format("%d_%s", chunk,
						file.getName()));
				chunk_started = System.currentTimeMillis();
				logger_cli.log(Level.INFO, "MSG_INFO_COMMAND_CHUNK",
						new Object[] { chunk, outfile.getAbsolutePath() });
				writer = new FileWriter(outfile);
				int records = 0;
				while (reader.hasNext()) {
					if (records >= chunksize) {
						try {
							if (writer != null)
								writer.close();
						} catch (Exception x) {
						}
						logger_cli
								.log(Level.INFO,
										"MSG_INFO_COMMAND_CHUNKWRITTEN",
										new Object[] {
												chunk,
												(System.currentTimeMillis() - chunk_started) });
						chunk++;
						outfile = new File(outdir, String.format("%d_%s",
								chunk, file.getName()));
						writer = new FileWriter(outfile);
						records = 0;
						chunk_started = System.currentTimeMillis();

						logger_cli
								.log(Level.INFO,
										"MSG_INFO_COMMAND_CHUNK",
										new Object[] { chunk,
												outfile.getAbsolutePath() });
					}
					IStructureRecord record = reader.nextRecord();
					writer.write(record.getContent());
					if ((records % 10000) == 0) {
						System.out.print('.');
						writer.flush();
					}
					records++;
				}
				return chunk;
			} else
				throw new Exception(String.format(
						"ERROR: %s is not an existing directory.",
						options.output));
		} catch (Exception x) {
			throw x;
		} finally {
			logger_cli.log(Level.INFO, "MSG_INFO_COMPLETED",
					(System.currentTimeMillis() - now));
			try {
				if (reader != null)
					reader.close();
			} catch (Exception x) {
			}
			try {
				if (writer != null)
					writer.close();
				logger_cli.log(Level.INFO, "MSG_INFO_COMMAND_CHUNKWRITTEN",
						new Object[] { chunk,
								(System.currentTimeMillis() - chunk_started) });
			} catch (Exception x) {
			}
			if (options.output != null) {
				logger_cli.log(Level.INFO, "MSG_INFO_RESULTSWRITTEN",
						options.output);
			}
		}
	}

	protected int parsePageParam() {
		try {
			return (Integer) options.getParam(":page");
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.toString());
			return 0;
		}
	}

	protected int parsePageSizeParam() {
		try {
			return (Integer) options.getParam(":pagesize");
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.toString());
			return -1;
		}
	}

	protected double parseThresholdParam() {
		try {
			return (Double) options.getParam(":threshold");
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.toString());
			return 0.75;
		}
	}

	protected String parseStringParam(String paramname, String defautlvalue) {
		try {
			return options.getParam(paramname).toString();
		} catch (Exception x) {
			return defautlvalue;
		}
	}

	protected Boolean parseBooleanParam(String paramname, boolean defautlvalue) {
		try {
			return Boolean.parseBoolean(options.getParam(paramname).toString());
		} catch (Exception x) {
			return defautlvalue;
		}
	}

	protected String parseInputTag_Param(String type, String defaultValue) {
		return parseStringParam(":inputtag_" + type, defaultValue);
	}

	protected Object parseSdfTitleParam() {
		try {
			return options.getParam(":sdftitle");
		} catch (Exception x) {
			return null;
		}
	}

	protected boolean parseWriteCountParam() {
		try {
			return Boolean.parseBoolean(options.getParam(":write_count")
					.toString());
		} catch (Exception x) {
			return false;
		}
	}

	protected boolean parseWriteRawParam() {
		try {
			return Boolean.parseBoolean(options.getParam(":write_raw")
					.toString());
		} catch (Exception x) {
			return false;
		}
	}

	protected File getInputFile() throws Exception {
		if (options.input == null)
			throw new FileNotFoundException(
					"Input file not specified! Please use -i {file}");
		return new File(options.input);
	}

	public void parseCommandSimilarityMatrix(String subcommand, long now)
			throws Exception {
		final File file = getInputFile();
		int page = parsePageParam();
		int pagesize = parsePageSizeParam();
		double threshold = parseThresholdParam();
		SimilarityMatrix matrix = new SimilarityMatrix();
		matrix.setLogger(logger_cli);
		matrix.createMatrix(file.getAbsolutePath(), false, threshold, page,
				pagesize);
	}

	public void parseCommandFingerprints(String subcommand, long now)
			throws Exception {
		boolean multifile = true;
		int page = parsePageParam();
		int pagesize = parsePageSizeParam();
		final boolean fp_count = parseWriteCountParam();
		final boolean fp_raw = parseWriteRawParam();
		String smiles_header = parseInputTag_Param("smiles",
				IteratingDelimitedFileReader.defaultSMILESHeader);
		String inchi_header = parseInputTag_Param("inchi", "InChI");
		String inchikey_header = parseInputTag_Param("inchikey", "InChIKey");

		Object tmpTag = parseSdfTitleParam();

		final String[] tags_to_keep = parsetags_to_keep();

		final String sdf_title = tmpTag == null ? null : tmpTag.toString()
				.toLowerCase();

		final int startRecord = pagesize > 0 ? (page * pagesize + 1) : 1;
		final int maxRecord = pagesize > 0 ? ((page + 1) * pagesize + 1)
				: pagesize;

		final File file = getInputFile();
		FileInputState input = new FileInputState(file);
		input.setOptionalSMILESHeader(smiles_header);
		input.setOptionalInChIHeader(inchi_header);
		input.setOptionalInChIKeyHeader(inchikey_header);

		if (options.output == null)
			throw new FileNotFoundException(
					"Output file not specified. Please use -o {file}");
		final File outfile = new File(options.output);

		logger_cli.log(Level.INFO, "MSG_INFO_READINGWRITING", new Object[] {
				file.getAbsoluteFile(), outfile.getAbsolutePath() });

		final List<IFingerprinter> fps = parseFingerprinterParams();

		FileOutputState out = null;
		final IChemObjectWriter awriter;
		if (multifile) {
			awriter = new MultiFingerprintsWriter(outfile, fps, tags_to_keep);
		} else {
			out = new FileOutputState(outfile);
			awriter = out.getWriter();
			if (awriter instanceof FilesWithHeaderWriter)
				((FilesWithHeaderWriter) awriter).setAddSMILEScolumn(false);
		}
		final IChemObjectWriter writer = awriter;
		final boolean writesdf = writer instanceof SDFWriter;

		final Map<Object, Property> tags = new HashMap<>();
		Property newtag = Property.getSMILESInstance();
		newtag.setName("SMILES");
		newtag.setEnabled(false);
		tags.put(Property.opentox_SMILES, newtag);
		tags.put(Property.getSMILESInstance(), newtag);

		newtag = Property.getInChIInstance();
		newtag.setEnabled(false);
		tags.put(Property.opentox_InChI, newtag);
		tags.put("InChI", newtag);
		tags.put(Property.getInChIInstance(), newtag);

		newtag = Property.getInChIKeyInstance();
		newtag.setEnabled(true);
		newtag.setName("InChIKey");
		tags.put(Property.opentox_InChIKey, newtag);
		tags.put(Property.getInChIKeyInstance(), newtag);

		newtag = Property.getInstance(CDKConstants.TITLE, CDKConstants.TITLE);
		newtag.setEnabled(false);
		tags.put(CDKConstants.TITLE, newtag);
		tags.put(newtag, newtag);

		newtag = Property.getInstance("CHEMBL", "CHEMBL");
		newtag.setEnabled(false);
		tags.put("CHEMBL", newtag);
		tags.put(newtag, newtag);

		final BatchDBProcessor<IStructureRecord> batch = new BatchDBProcessor<IStructureRecord>() {

			@Override
			public void onItemRead(IStructureRecord input,
					IBatchStatistics stats) {
				super.onItemRead(input, stats);
				if ((maxRecord > 0)
						&& stats.getRecords(RECORDS_STATS.RECORDS_READ) >= (maxRecord))
					cancel();
			};

			@Override
			public boolean skip(IStructureRecord input, IBatchStatistics stats) {
				return (stats.getRecords(RECORDS_STATS.RECORDS_READ) < startRecord)
						|| ((maxRecord > 0) && (stats
								.getRecords(RECORDS_STATS.RECORDS_READ) >= maxRecord));
			}

			@Override
			public void onItemSkipped(IStructureRecord input,
					IBatchStatistics stats) {
				super.onItemSkipped(input, stats);
				if (stats.isTimeToPrint(getSilentInterval() * 2))
					propertyChangeSupport.firePropertyChange(
							PROPERTY_BATCHSTATS, null, stats);
			}

			@Override
			public void onItemProcessing(IStructureRecord input, Object output,
					IBatchStatistics stats) {
			}

			@Override
			public void onError(IStructureRecord input, Object output,
					IBatchStatistics stats, Exception x) {
				super.onError(input, output, stats, x);
				logger_cli.log(Level.SEVERE, "MSG_ERR",
						new Object[] { x.getMessage() });
			}

			@Override
			public long getSilentInterval() {
				return 30000L;
			}

			@Override
			public void close() throws Exception {
				try {
					writer.close();
				} catch (Exception x) {
				} finally {
				}
				super.close();

			}

		};

		batch.setProcessorChain(new ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor>());
		batch.getProcessorChain()
				.add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {

					protected MoleculeReader molReader = new MoleculeReader(
							true, false);

					@Override
					public IStructureRecord process(IStructureRecord record)
							throws Exception {

						IAtomContainer mol;
						IAtomContainer processed = null;

						try {
							mol = molReader.process(record);

							if (mol != null) {
								for (Property p : record.getRecordProperties()) {
									Object v = record.getRecordProperty(p);

									String pname = p.getName().replace(
											"http://www.opentox.org/api/1.1#",
											"");

									// initially to get rid of smiles, inchi ,
									// etc, these are
									// already parsed
									if (tags_to_keep != null
											&& Arrays.binarySearch(
													tags_to_keep, pname) < 0)
										continue;
									else
										mol.setProperty(p, v);
								}
								if (tags_to_keep != null) {
									List<String> toRemove = null;
									Iterator pi = mol.getProperties().keySet()
											.iterator();
									while (pi.hasNext()) {
										Object p = pi.next();
										if (Arrays.binarySearch(tags_to_keep,
												p.toString()) < 0) {
											if (toRemove == null)
												toRemove = new ArrayList<String>();
											toRemove.add(p.toString());
										}

									}
									if (toRemove != null)
										for (String propertyToRemove : toRemove)
											mol.removeProperty(propertyToRemove);

								}
							} else {
								logger_cli.log(Level.SEVERE,
										"MSG_FINGEPRINTGEN", new Object[] {
												"Empty molecule",
												getIds(record) });
								return record;
							}

							AtomContainerManipulator
									.percieveAtomTypesAndConfigureAtoms(mol);

						} catch (Exception x) {

							logger_cli
									.log(Level.SEVERE,
											"MSG_ERR_MOLREAD",
											new Object[] { getIds(record),
													x.toString() });
							return record;
						} finally {

						}
						processed = mol;

						for (IFingerprinter fp : fps) {
							ICountFingerprint cfp = null;
							try {
								cfp = fp.getCountFingerprint(processed);
							} catch (Exception x) {
								logger.log(Level.FINER, x.getMessage());
							}
							IBitFingerprint bfp = null;
							try {
								bfp = fp.getBitFingerprint(processed);
							} catch (Exception x) {
							}
							Map<String, Integer> fpraw = null;
							try {
								if (fp_raw)
									fpraw = fp.getRawFingerprint(processed);
							} catch (Exception x) {

							}

							try {
								if (cfp != null) {
									if (fp_count)
										processed.setProperty(fp.getClass()
												.getName() + ".count", cfp);
									processed.setProperty(fp.getClass()
											.getName(), cfp);
								}
								if (bfp != null)
									processed.setProperty(fp.getClass()
											.getName() + ".hashed", bfp);
								if (fpraw != null)
									processed.setProperty(fp.getClass()
											.getName() + ".raw", fpraw);

							} catch (Exception x) {
								// StringWriter w = new StringWriter();
								// x.printStackTrace(new PrintWriter(w));
								logger_cli.log(Level.SEVERE,
										"MSG_FINGEPRINTGEN",
										new Object[] { x.getMessage(),
												getIds(record) });
								if (processed != null)
									processed.setProperty("ERROR."
											+ fp.getClass().getName(),
											x.getMessage());
							} finally {
								if (processed != null)
									processed.addProperties(mol.getProperties());
							}
						}
						if (processed != null)
							try {
								if (writesdf && sdf_title != null) {

									for (Entry<Object, Object> p : processed
											.getProperties().entrySet())
										if (sdf_title.equals(p.getKey()
												.toString().toLowerCase())) {
											processed.setProperty(
													CDKConstants.TITLE,
													p.getValue());
											break;
										}
								}
								StructureStandardizer.renameTags(processed,
										tags, true);
								writer.write(processed);
							} catch (Exception x) {
								// StringWriter w = new StringWriter();
								// x.printStackTrace(new PrintWriter(w));
								logger_cli.log(Level.SEVERE,
										"MSG_FINGEPRINTGEN",
										new Object[] { x.getMessage(),
												getIds(record) });
							}
						else {
							logger_cli.log(Level.SEVERE, "MSG_FINGEPRINTGEN",
									new Object[] { "Empty molecule",
											getIds(record) });
						}
						return record;

					}

				});
		batch.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (AbstractBatchProcessor.PROPERTY_BATCHSTATS.equals(evt
						.getPropertyName()))
					logger_cli.log(Level.INFO, evt.getNewValue().toString());
			}
		});
		/*
		 * standardprocessor.setCallback(new
		 * DefaultAmbitProcessor<IAtomContainer, IAtomContainer>() {
		 * 
		 * @Override public IAtomContainer process(IAtomContainer target) throws
		 * Exception { try { //writer.write(target); } catch (Exception x) {
		 * logger.log(Level.SEVERE, x.getMessage()); } return target; } });
		 */

		IBatchStatistics stats = null;
		try {
			stats = batch.process(input);
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		} finally {
			try {
			} catch (Exception x) {
				logger_cli.warning(x.getMessage());
			}
			try {
				if (batch != null)
					batch.close();
			} catch (Exception x) {
				logger_cli.warning(x.getMessage());
			}
			if (stats != null)
				logger_cli.log(Level.INFO, stats.toString());
		}
	}

	protected List<IFingerprinter> parseFingerprinterParams() {
		final List<IFingerprinter> fps = new ArrayList<IFingerprinter>();
		try {
			Object o = options.getParam(":fpclass");
			if (o != null) {
				String[] fpclass = o.toString().split(",");
				Class clazz = null;
				for (String fp : fpclass)
					try {
						if (fp.indexOf(".") < 0)
							fp = "org.openscience.cdk.fingerprint." + fp.trim();
						else
							fp = fp.trim();
						clazz = Class.forName(fp);
						Object fingerprinter = clazz.newInstance();
						if (fingerprinter instanceof IFingerprinter)
							fps.add((IFingerprinter) fingerprinter);
					} catch (Exception x) {
						if (clazz != null)
							try {
								Object fingerprinter = clazz
										.getDeclaredConstructor(
												IChemObjectBuilder.class)
										.newInstance(
												SilentChemObjectBuilder
														.getInstance());
								if (fingerprinter instanceof IFingerprinter)
									fps.add((IFingerprinter) fingerprinter);
							} catch (Exception xx) {
								logger_cli.log(Level.SEVERE, "MSG_ERR_CLASS",
										new Object[] { fp, x.toString() });
							}

					}
			}
		} catch (Exception x) {
		}
		if (fps.size() == 0) {
			fps.add(new org.openscience.cdk.fingerprint.CircularFingerprinter());
			// fps.add(new AtomEnvironentMatrix());
		}
		StringBuilder b = new StringBuilder();
		for (IFingerprinter fp : fps) {
			b.append("\n");
			b.append(fp.getClass().getName());
		}
		logger_cli.log(Level.INFO, "MSG_FP", new Object[] { b.toString() });
		return fps;
	}

	protected String[] parsetags_to_keep() {
		String[] fields_to_keep = null;
		try {
			Object tag_to_keep = options.getParam(":tag_tokeep");
			if (tag_to_keep != null
					&& !"".equals(tag_to_keep.toString().trim()))
				fields_to_keep = tag_to_keep.toString().split(",");
			if (fields_to_keep != null && fields_to_keep.length == 0)
				fields_to_keep = null;
		} catch (Exception x) {
			fields_to_keep = null;
			logger_cli.log(Level.WARNING, x.toString());
		}
		if (fields_to_keep != null) {
			Arrays.sort(fields_to_keep);
			for (int i = 0; i < fields_to_keep.length; i++)
				fields_to_keep[i] = fields_to_keep[i].trim();
		}
		return fields_to_keep;
	}

	public void parseCommandStandardize(String subcommand, long now)
			throws Exception {
		int page = parsePageParam();
		int pagesize = parsePageSizeParam();
		Object tmpTag = parseSdfTitleParam();

		String smiles_header = parseInputTag_Param("smiles",
				IteratingDelimitedFileReader.defaultSMILESHeader);
		String inchi_header = parseInputTag_Param("inchi", "InChI");
		String inchikey_header = parseInputTag_Param("inchikey", "InChIKey");

		final String sdf_title = tmpTag == null ? null : tmpTag.toString()
				.toLowerCase();

		final StructureStandardizer standardprocessor = new StructureStandardizer(
				logger_cli);
		standardprocessor
				.setGenerate2D(parseBooleanParam(":generate2D", false));
		standardprocessor.setGenerateTautomers(parseBooleanParam(":tautomers",
				false));

		SMIRKSProcessor tmp = null;
		try {
			Object o = options.getParam(":smirks");
			if (o != null) {
				File smirksConfig = new File(o.toString());
				if (smirksConfig.exists()) {
					tmp = new SMIRKSProcessor(smirksConfig, logger_cli);
					tmp.setEnabled(true);
				} else
					logger_cli.log(Level.WARNING,
							"SMIRKS transformation file not found");
			}
		} catch (Exception x) {
			logger_cli.log(Level.SEVERE, x.getMessage());
			tmp = null;
		}
		final SMIRKSProcessor smirksProcessor = tmp;

		standardprocessor.setSplitFragments(parseBooleanParam(
				":splitfragments", false));
		standardprocessor.setImplicitHydrogens(parseBooleanParam(":implicith",
				false));
		standardprocessor
				.setNeutralise(parseBooleanParam(":neutralise", false));

		final String[] tags_to_keep = parsetags_to_keep();

		standardprocessor.setRankTag(parseStringParam(":tag_rank", "RANK"));
		standardprocessor.setInchiTag(parseStringParam(":tag_inchi", "InChI"));
		standardprocessor.setInchiKeyTag(parseStringParam(":tag_inchikey",
				"InChIKey"));
		standardprocessor
				.setSMILESTag(parseStringParam(":tag_smiles", "SMILES"));

		standardprocessor.setGenerateInChI(parseBooleanParam(":inchi", true));
		standardprocessor.setGenerateSMILES(parseBooleanParam(":smiles", true));
		standardprocessor.setGenerateSMILES_Canonical(parseBooleanParam(
				":smilescanonical", false));
		standardprocessor.setGenerateSMILES_Aromatic(parseBooleanParam(
				":smilesaromatic", false));
		standardprocessor.setGenerateStereofrom2D(parseBooleanParam(
				":generatestereofrom2d", false));
		standardprocessor.setClearIsotopes(parseBooleanParam(
				":setClearIsotopes", false));

		final boolean debugatomtypes = parseBooleanParam(":debugatomtypes",
				false);
		final int startRecord = pagesize > 0 ? (page * pagesize + 1) : 1;
		final int maxRecord = pagesize > 0 ? ((page + 1) * pagesize + 1)
				: pagesize;

		final File file = getInputFile();
		FileInputState in = new FileInputState(file);
		in.setOptionalInChIHeader(inchi_header);
		in.setOptionalInChIKeyHeader(inchikey_header);
		in.setOptionalSMILESHeader(smiles_header);

		if (options.output == null)
			throw new FileNotFoundException(
					"Output file not specified. Please use -o {file}");
		final File outfile = new File(options.output);

		logger_cli.log(Level.INFO, "MSG_INFO_READINGWRITING", new Object[] {
				file.getAbsoluteFile(), outfile.getAbsolutePath() });
		FileOutputState out = new FileOutputState(outfile);
		final IChemObjectWriter writer = out.getWriter();
		if (writer instanceof FilesWithHeaderWriter)
			((FilesWithHeaderWriter) writer).setAddSMILEScolumn(false);
		final boolean writesdf = writer instanceof SDFWriter;
		final BatchDBProcessor<IStructureRecord> batch = new BatchDBProcessor<IStructureRecord>() {

			@Override
			public void onItemRead(IStructureRecord input,
					IBatchStatistics stats) {
				super.onItemRead(input, stats);
				if ((maxRecord > 0)
						&& stats.getRecords(RECORDS_STATS.RECORDS_READ) >= (maxRecord))
					cancel();
			};

			@Override
			public boolean skip(IStructureRecord input, IBatchStatistics stats) {
				return (stats.getRecords(RECORDS_STATS.RECORDS_READ) < startRecord)
						|| ((maxRecord > 0) && (stats
								.getRecords(RECORDS_STATS.RECORDS_READ) >= maxRecord));
			}

			@Override
			public void onItemSkipped(IStructureRecord input,
					IBatchStatistics stats) {
				super.onItemSkipped(input, stats);
				if (stats.isTimeToPrint(getSilentInterval() * 2))
					propertyChangeSupport.firePropertyChange(
							PROPERTY_BATCHSTATS, null, stats);
			}

			@Override
			public void onItemProcessing(IStructureRecord input, Object output,
					IBatchStatistics stats) {
			}

			@Override
			public void onError(IStructureRecord input, Object output,
					IBatchStatistics stats, Exception x) {
				super.onError(input, output, stats, x);
				logger_cli.log(Level.SEVERE, x.getMessage());
			}

			@Override
			public long getSilentInterval() {
				return 30000L;
			}

			@Override
			public void close() throws Exception {
				try {
					writer.close();
				} catch (Exception x) {
				} finally {
				}
				super.close();

			}

		};
		batch.setProcessorChain(new ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor>());
		batch.getProcessorChain()
				.add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {

					protected MoleculeReader molReader = new MoleculeReader(
							true, false);

					@Override
					public IStructureRecord process(IStructureRecord record)
							throws Exception {

						IAtomContainer mol;
						IAtomContainer processed = null;

						try {
							mol = molReader.process(record);
							if (mol != null) {
								for (Property p : record.getRecordProperties()) {
									Object v = record.getRecordProperty(p);

									// initially to get rid of smiles, inchi ,
									// etc, these are
									// already parsed
									if (tags_to_keep != null
											&& Arrays.binarySearch(
													tags_to_keep, p.getName()) < 0)
										continue;
									if (p.getName().startsWith(
											"http://www.opentox.org/api/1.1#"))
										continue;
									else
										mol.setProperty(p, v);
								}
								if (tags_to_keep != null) {
									List<String> toRemove = null;
									Iterator pi = mol.getProperties().keySet()
											.iterator();
									while (pi.hasNext()) {
										Object p = pi.next();
										if (Arrays.binarySearch(tags_to_keep,
												p.toString()) < 0) {
											if (toRemove == null)
												toRemove = new ArrayList<String>();
											toRemove.add(p.toString());
										}

									}
									if (toRemove != null)
										for (String propertyToRemove : toRemove)
											mol.removeProperty(propertyToRemove);

								}

							} else {
								logger_cli
										.log(Level.SEVERE,
												"MSG_STANDARDIZE",
												new Object[] {
														"Empty molecule See the ERROR tag in the output file",
														getIds(record) });
								return record;
							}
						} catch (Exception x) {
							logger_cli
									.log(Level.SEVERE,
											"MSG_ERR_MOLREAD",
											new Object[] { getIds(record),
													x.toString() });
							return record;
						} finally {

						}
						processed = mol;

						// CDK adds these for the first MOL line
						if (!writesdf) {
							// if (mol.getProperty(CDKConstants.TITLE) != null)
							// mol.removeProperty(CDKConstants.TITLE);
							if (mol.getProperty(CDKConstants.REMARK) != null)
								mol.removeProperty(CDKConstants.REMARK);
						}
						if ((smirksProcessor != null)
								&& smirksProcessor.isEnabled()) {
							processed = smirksProcessor.process(processed);
						}

						try {
							processed = standardprocessor.process(processed);

						} catch (Exception x) {
							String err = processed
									.getProperty(StructureStandardizer.ERROR_TAG);
							logger_cli.log(Level.SEVERE, x.getMessage(), x);
							if (processed != null) {
								err = processed
										.getProperty(StructureStandardizer.ERROR_TAG);
								processed.setProperty(
										StructureStandardizer.ERROR_TAG, String
												.format("%s %s %s",
														err == null ? "" : err,
														x.getClass().getName(),
														x.getMessage()));
							}
						} finally {
							if (processed != null) {
								Iterator<Entry<Object, Object>> p = mol
										.getProperties().entrySet().iterator();
								// don't overwrite properties from the source
								// molecule
								while (p.hasNext()) {
									Entry<Object, Object> entry = p.next();
									Object value = processed.getProperty(entry
											.getKey());
									if (value == null
											|| "".equals(value.toString()
													.trim()))
										processed.setProperty(entry.getKey(),
												entry.getValue());
								}
							}
						}
						if (processed != null)
							try {
								if (writesdf && sdf_title != null) {

									for (Entry<Object, Object> p : processed
											.getProperties().entrySet())
										if (sdf_title.equals(p.getKey()
												.toString().toLowerCase())) {
											processed.setProperty(
													CDKConstants.TITLE,
													p.getValue());
											break;
										}
								}
								if (debugatomtypes) {
									Object debug = (processed == null) ? null
											: processed
													.getProperty("AtomTypes");

									if (debug != null && !"".equals(debug))
										writer.write(processed);
								} else
									writer.write(processed);
							} catch (Exception x) {
								logger_cli.log(Level.SEVERE, x.getMessage());
							}
						return record;

					}

				});
		batch.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (AbstractBatchProcessor.PROPERTY_BATCHSTATS.equals(evt
						.getPropertyName()))
					logger_cli.log(Level.INFO, evt.getNewValue().toString());
			}
		});
		/*
		 * standardprocessor.setCallback(new
		 * DefaultAmbitProcessor<IAtomContainer, IAtomContainer>() {
		 * 
		 * @Override public IAtomContainer process(IAtomContainer target) throws
		 * Exception { try { //writer.write(target); } catch (Exception x) {
		 * logger.log(Level.SEVERE, x.getMessage()); } return target; } });
		 */

		IBatchStatistics stats = null;
		try {
			stats = batch.process(in);
		} catch (Exception x) {
			StringWriter w = new StringWriter();
			x.printStackTrace(new PrintWriter(w));
			logger_cli.log(Level.WARNING, "MSG_ERR",
					new Object[] { x.getMessage() });
			logger_cli.log(Level.FINE, "MSG_ERR_DEBUG",
					new Object[] { x.getMessage(), w.toString() });
		} finally {
			try {
				if (batch != null)
					batch.close();
			} catch (Exception x) {
				logger_cli.log(Level.WARNING, "MSG_ERR",
						new Object[] { x.getMessage() });
			}
			if (stats != null)
				logger_cli.log(Level.INFO, "MSG_INFO",
						new Object[] { stats.toString() });
		}
	}

	public void parseDBMigrate(String subcommand, long now) throws Exception {
		File folder = new File(System.getProperty("java.io.tmpdir"));
		String resource = "ambit2/dbcli/sql/export_substances.sql";
		_subcommandmode sc = _subcommandmode.get;
		try {
			sc = _subcommandmode.valueOf(subcommand);
			switch (sc) {
			case get: {
				resource = "ambit2/dbcli/sql/export_substances.sql";
				try {
					folder = new File(options.output);
				} catch (Exception x) {
				}
				break;
			}
			case put: {
				resource = "ambit2/dbcli/sql/import_substances.sql";
				try {
					folder = new File(options.input);
				} catch (Exception x) {
				}
				break;
			}
			default:
				throw new Exception("Unsupported subcommand " + subcommand);
			}
		} catch (Exception x) {
			x.printStackTrace();
			logger_cli.log(Level.SEVERE, "MSG_ERR",
					new Object[] { x.getMessage() });
			return;
		}
		
		URL url = Resources.getResource(resource);
		String text = Resources.toString(url, Charsets.UTF_8);
		text = text.replace("{TMP}",folder==null?"":(folder.getAbsolutePath().replace("\\","/")+"/"));
		
		Connection c = null;
		BufferedReader reader = null;
		try {
			DBConnectionConfigurable<Context> dbc = null;
			dbc = getConnection(options.getSQLConfig());
			c = dbc.getConnection();
			c.setAutoCommit(false);

			ScriptRunner runner = new ScriptRunner(c);

			reader = new BufferedReader(new StringReader(text));
			runner.runScript(reader);
			c.commit();
		} catch (Exception x) {
			logger_cli.log(Level.SEVERE, x.getMessage());
		} finally {
			if (c != null)
				c.close();

		}
	}

	public void parseCommandImport() throws Exception {

		File file = new File(options.input);

		final QuickImportBatchProcessor batch = new QuickImportBatchProcessor(
				file) {
			/**
		     * 
		     */
			private static final long serialVersionUID = -2617748719057089460L;

			@Override
			public void onItemRead(IStructureRecord input,
					IBatchStatistics stats) {
				super.onItemRead(input, stats);
				if ((stats.getRecords(RECORDS_STATS.RECORDS_READ) % 10000) == 0)
					try {
						logger_cli.log(Level.INFO, stats.toString());
						getConnection().commit();
					} catch (Exception x) {
						logger_cli.log(Level.WARNING, x.getMessage());
					}
			};

			@Override
			public void onError(IStructureRecord input, Object output,
					IBatchStatistics stats, Exception x) {
				super.onError(input, output, stats, x);
				logger_cli.log(Level.SEVERE, x.getMessage());
			}
		};

		Connection c = null;
		DBConnectionConfigurable<Context> dbc = null;
		dbc = getConnection(options.getSQLConfig());
		c = dbc.getConnection();
		c.setAutoCommit(false);
		batch.setCloseConnection(true);
		batch.setConnection(c);

		FileInputState in = new FileInputState(file);
		IBatchStatistics stats = null;
		try {
			stats = batch.process(in);
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		} finally {
			try {
				batch.getConnection().commit();
			} catch (Exception x) {
				logger_cli.warning(x.getMessage());
			}
			try {
				if (batch != null)
					batch.close();
			} catch (Exception x) {
				logger_cli.warning(x.getMessage());
			}
			if (stats != null)
				logger_cli.log(Level.INFO, stats.toString());
		}
	}

	public void parseCommandPreprocessing() throws Exception {

		int pagesize = parsePageSizeParam();
		Set<FPTable> preprocessingOption = new TreeSet<FPTable>();
		/*
		 * try { if ((Boolean)options.getParam(":pubchemfp"))
		 * preprocessingOption.add(FPTable.pc1024); } catch (Exception x) {
		 * x.printStackTrace(); }
		 */
		_preprocessingoptions[] po = _preprocessingoptions.values();
		for (_preprocessingoptions p : po)
			try {
				if ((Boolean) options.getParam(p.toString())) {
					FPTable[] to = p.getOption();
					for (FPTable t : to)
						preprocessingOption.add(t);
				}
			} catch (Exception x) {
				logger_cli.log(Level.WARNING, x.toString());
			}

		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>() {
			/**
		     * 
		     */
			private static final long serialVersionUID = 6777121852891369530L;

			@Override
			public void onItemRead(IStructureRecord input,
					IBatchStatistics stats) {
				super.onItemRead(input, stats);
				if ((stats.getRecords(RECORDS_STATS.RECORDS_READ) % 5000) == 0)
					try {
						logger_cli.log(Level.INFO, stats.toString());
						getConnection().commit();
					} catch (Exception x) {
						logger_cli.log(Level.WARNING, x.getMessage());
					}

			};

			@Override
			public void onError(IStructureRecord input, Object output,
					IBatchStatistics stats, Exception x) {
				super.onError(input, output, stats, x);
				logger_cli.log(Level.SEVERE, x.getMessage());
			}
		};
		batch.setProcessorChain(new ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor>());

		/* structure */
		RetrieveStructure queryP = new RetrieveStructure(true);
		queryP.setFieldname(true);
		queryP.setPageSize(1);
		queryP.setPage(0);

		MasterDetailsProcessor<IStructureRecord, IStructureRecord, IQueryCondition> strucReader = new MasterDetailsProcessor<IStructureRecord, IStructureRecord, IQueryCondition>(
				queryP) {
			/**
		     * 
		     */
			private static final long serialVersionUID = -5350168222668294207L;

			@Override
			protected void configureQuery(
					IStructureRecord target,
					IParameterizedQuery<IStructureRecord, IStructureRecord, IQueryCondition> query)
					throws AmbitException {
				query.setValue(target);
				// super.configureQuery(target, query);
			}

			@Override
			protected IStructureRecord processDetail(IStructureRecord master,
					IStructureRecord detail) throws Exception {

				master.setContent(detail.getContent());
				master.setFormat(detail.getFormat());
				master.setType(detail.getType());
				return master;
			}
		};
		strucReader.setCloseConnection(false);
		batch.getProcessorChain().add(strucReader);
		// preprocessing itself

		// query
		IQueryRetrieval<IStructureRecord> query = null;
		AbstractUpdate updateQuery = null;
		if (preprocessingOption.isEmpty())
			preprocessingOption.add(FPTable.inchi);

		if (preprocessingOption.contains(FPTable.inchi)) {
			query = new MissingInChIsQuery("UNKNOWN");
			updateQuery = new UpdateChemical();
			batch.getProcessorChain()
					.add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
						/**
			     * 
			     */
						private static final long serialVersionUID = -7628269103516836861L;
						protected transient StructureNormalizer normalizer = new StructureNormalizer();

						@Override
						public IStructureRecord process(IStructureRecord record)
								throws Exception {
							try {
								normalizer.process(record);
								return record;
							} catch (Exception x) {
								record.setType(STRUC_TYPE.NA);
								return record;
							}
						}
					});
			batch.getProcessorChain().add(
					new AbstractUpdateProcessor<Object, IChemical>(OP.CREATE,
							updateQuery) {
						/**
			     * 
			     */
						private static final long serialVersionUID = 9019409150445247686L;

						@Override
						protected IChemical execute(Object group,
								IQueryUpdate<Object, IChemical> query)
								throws SQLException,
								OperationNotSupportedException, AmbitException {
							if (group instanceof IChemical)
								query.setObject((IChemical) group);
							return super.execute(group, query);
						}
					});
		} else {
			// add generators
			if (preprocessingOption.contains(FPTable.smarts_accelerator)) {
				query = new MissingFingerprintsQuery(FPTable.smarts_accelerator);
				batch.getProcessorChain().add(new SMARTSPropertiesGenerator());
			}
			if (preprocessingOption.contains(FPTable.fp1024)) {
				query = new FingerprintsByStatus(FPTable.fp1024);
				// updateQuery = new CreateFingerprintChemical(FPTable.fp1024);
				batch.getProcessorChain().add(
						new BitSetGenerator(FPTable.fp1024));
			}
			if (preprocessingOption.contains(FPTable.sk1024)) {
				query = new FingerprintsByStatus(FPTable.sk1024);
				batch.getProcessorChain().add(
						new BitSetGenerator(FPTable.sk1024));
			}

			// add writers
			if (preprocessingOption.contains(FPTable.smarts_accelerator)) {
				batch.getProcessorChain().add(new SMARTSAcceleratorWriter());
			}
			if (preprocessingOption.contains(FPTable.fp1024)) {
				batch.getProcessorChain().add(new FP1024Writer(FPTable.fp1024));
			}
			if (preprocessingOption.contains(FPTable.sk1024)) {
				batch.getProcessorChain().add(new FP1024Writer(FPTable.sk1024));
			}

			if (preprocessingOption.contains(FPTable.cf1024)) {
				query = new FingerprintsByStatus(FPTable.cf1024);
				batch.getProcessorChain().add(
						new BitSetGenerator(FPTable.cf1024));
				batch.getProcessorChain().add(new FP1024Writer(FPTable.cf1024));
			}

		}

		batch.setHandlePrescreen(false);

		Connection c = null;
		DBConnectionConfigurable<Context> dbc = null;
		dbc = getConnection(options.getSQLConfig());
		c = dbc.getConnection();
		c.setAutoCommit(false);
		batch.setCloseConnection(true);
		batch.setConnection(c);
		batch.open();

		IBatchStatistics stats = null;
		try {
			query.setPageSize(pagesize);
			logger_cli.info(query.getSQL());
			try {
				disableIndices(batch.getConnection());
			} catch (Exception x) {
				logger_cli.warning(x.getMessage());
			}
			logger_cli.log(Level.INFO, "MSG_INFO_QUERY", pagesize);
			stats = batch.process(query);
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		} finally {
			try {
				batch.getConnection().commit();
			} catch (Exception x) {
				logger_cli.warning(x.getMessage());
			}

			try {
				enableIndices(batch.getConnection());
			} catch (Exception x) {
				logger_cli.warning(x.getMessage());
			}

			try {
				if (batch != null)
					batch.close();
			} catch (Exception x) {
				logger_cli.warning(x.getMessage());
			}
			if (stats != null)
				logger_cli.log(Level.INFO, stats.toString());
		}

	}

	private static void inchi_warmup(int retry) throws Exception {
		Exception err = null;
		long now = System.currentTimeMillis();
		for (int i = 0; i < retry; i++)
			try {
				InchiProcessor p = new InchiProcessor();
				p.process(MoleculeFactory.makeCyclohexane());
				p.close();
				p = null;

				logger_cli
						.log(Level.INFO,
								"MSG_INCHI",
								new Object[] {
										String.format("loaded in %d msec",
												System.currentTimeMillis()
														- now), "" });
				return;
			} catch (Exception x) {
				logger_cli.log(Level.WARNING, "MSG_INCHI", new Object[] {
						"load failed at retry ", (retry + 1) });
				err = x;
			}
		if (err != null)
			throw err;
	}

	protected String getIds(IStructureRecord record) {
		try {
			StringBuilder m = new StringBuilder();
			for (Property p : record.getRecordProperties()) {
				// m.append(p.getName());
				m.append(record.getRecordProperty(p));
				m.append("\t");
			}
			return m.toString();
		} catch (Exception x) {
			return "";
		}
	}
}

class Context extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1993541083813105854L;

}