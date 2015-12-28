package ambit2.dbcli;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
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

import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.JsonNode;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectWriter;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IChemical;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.io.FileInputState;
import ambit2.core.io.FileOutputState;
import ambit2.core.io.IRawReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.StructureNormalizer;
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
import ambit2.dbcli.CliOptions._preprocessingoptions;
import ambit2.dbcli.descriptors.AtomEnvironmentGeneratorApp;
import ambit2.descriptors.processors.BitSetGenerator;
import ambit2.descriptors.processors.FPTable;
import ambit2.smarts.processors.SMARTSPropertiesGenerator;
import ambit2.smarts.processors.SMIRKSProcessor;
import ambit2.tautomers.processor.StructureStandardizer;

import com.mysql.jdbc.CommunicationsException;

/**
 * 
 * @author nina
 * 
 */
public class AmbitCli {
	static Logger logger_cli;
	static final String loggingProperties = "ambit2/dbcli/logging.properties";
	static final String log4jProperties = "ambit2/dbcli/log4j.properties";

	CliOptions options;

	public AmbitCli(CliOptions options) {
		this.options = options;
		Locale.setDefault(Locale.ENGLISH);
		logger_cli = Logger.getLogger("ambitcli", "ambit2.dbcli.msg");

		String dOption = System.getProperty("java.util.logging.config.file");
		if (dOption == null || "".equals(dOption)) {
			InputStream in = null;
			try {
				in = getClass().getClassLoader().getResourceAsStream(
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
			in = getClass().getClassLoader().getResourceAsStream(
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

		logger_cli.log(Level.INFO, "MSG_INFO",
				new Object[] { System.currentTimeMillis() });
	}

	public long go(String command, String subcommand) throws Exception {
		long now = System.currentTimeMillis();
		if ("import".equals(command)) {
			parseCommandImport();
		} else if ("preprocessing".equals(command)) {
			parseCommandPreprocessing();
		} else if ("dataset".equals(command)) {
			return parseCommandDataset(now);
		} else if ("split".equals(command)) {
			return parseCommandSplit(subcommand, now);
		} else if ("atomenvironments".equals(command)) {
			return parseCommandAE(subcommand, now);
		} else if ("standardize".equals(command)) {
			parseCommandStandardize(subcommand, now);
		}
		return -1;
	}

	public static void main(String[] args) {
		long now = System.currentTimeMillis();
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
			} else
				System.exit(0);
		} catch (ConnectException x) {
			logger_cli.log(Level.SEVERE, "MSG_CONNECTION_REFUSED");
			System.exit(-1);
		} catch (CommunicationsException x) {
			logger_cli.log(Level.SEVERE, "MSG_ERR_CONNECTION_FAILED");
			System.exit(-1);
		} catch (SQLException x) {
			logger_cli.log(Level.SEVERE, "MSG_ERR_SQL", x);
			System.exit(-1);
		} catch (Exception x) {
			logger_cli.log(Level.SEVERE, x.getMessage());
			System.exit(-1);
		} finally {

			logger_cli.log(Level.INFO, "MSG_INFO_COMPLETED",
					(System.currentTimeMillis() - now));
		}
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

	public void parseCommandStandardize(String subcommand, long now)
			throws Exception {
		int page = 0;
		try {
			page = (Integer) options.getParam(":page");
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.toString());
		}
		int pagesize = -1;
		try {
			pagesize = (Integer) options.getParam(":pagesize");
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.toString());
		}
		final StructureStandardizer standardprocessor = new StructureStandardizer();

		try {
			Object o = options.getParam(":tautomers");
			standardprocessor.setGenerateTautomers(Boolean.parseBoolean(o
					.toString()));
		} catch (Exception x) {
		}
		SMIRKSProcessor tmp = null;
		try {
			Object o = options.getParam(":smirks");
			if (o != null) {
				File smirksConfig = new File(o.toString());
				if (smirksConfig.exists()) {
					tmp = new SMIRKSProcessor(smirksConfig);
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
		try {
			Object o = options.getParam(":splitfragments");
			standardprocessor.setSplitFragments(Boolean.parseBoolean(o
					.toString()));
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":implicith");
			standardprocessor.setImplicitHydrogens(Boolean.parseBoolean(o
					.toString()));
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":neutralise");
			standardprocessor.setNeutralise(Boolean.parseBoolean(o.toString()));
		} catch (Exception x) {
		}

		try {
			Object o = options.getParam(":tag_rank");
			standardprocessor.setRankTag(o.toString());
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":tag_inchi");
			standardprocessor.setInchiTag(o.toString());
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":tag_smiles");
			standardprocessor.setSMILESTag(o.toString());
		} catch (Exception x) {
		}

		try {
			Object o = options.getParam(":tag_inchikey");
			standardprocessor.setInchiKeyTag(o.toString());
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":inchi");
			standardprocessor.setGenerateInChI(Boolean.parseBoolean(o
					.toString()));
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":smiles");
			standardprocessor.setGenerateSMILES(Boolean.parseBoolean(o
					.toString()));
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":smilescanonical");
			standardprocessor.setGenerateSMILES_Canonical(Boolean
					.parseBoolean(o.toString()));
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":stereo");
			standardprocessor.setStereo(Boolean.parseBoolean(o.toString()));
		} catch (Exception x) {
		}
		try {
			Object o = options.getParam(":isotopes");
			standardprocessor.setClearIsotopes(Boolean.parseBoolean(o
					.toString()));
		} catch (Exception x) {
		}

		boolean debug = false;
		try {
			Object o = options.getParam(":debugatomtypes");
			debug = Boolean.parseBoolean(o.toString());
		} catch (Exception x) {
		}
		final boolean debugatomtypes = debug;
		final int startRecord = pagesize > 0 ? (page * pagesize + 1) : 1;
		final int maxRecord = pagesize > 0 ? ((page + 1) * pagesize + 1)
				: pagesize;

		final File file = new File(options.input);
		FileInputState in = new FileInputState(file);

		if (options.output == null)
			throw new FileNotFoundException(
					"Output file not specified. Please use -o {file}");
		final File outfile = new File(options.output);

		logger_cli.log(Level.INFO, "MSG_INFO_READINGWRITING", new Object[] {
				file.getAbsoluteFile(), outfile.getAbsolutePath() });
		FileOutputState out = new FileOutputState(outfile);
		final IChemObjectWriter writer = out.getWriter();

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

					protected MoleculeReader molReader = new MoleculeReader();

					@Override
					public IStructureRecord process(IStructureRecord record)
							throws Exception {

						IAtomContainer mol;
						IAtomContainer processed = null;

						try {
							mol = molReader.process(record);
							if (mol != null)
								for (Property p : record.getRecordProperties()) {
									Object v = record.getRecordProperty(p);
									// initially to get rid of smiles, inchi ,
									// etc, these are
									// already parsed 
									if (p.getName().startsWith(
											"http://www.opentox.org/api/1.1#"))
										continue;
									else
										mol.setProperty(p, v);
								}
							else
								return record;
						} catch (Exception x) {
							logger_cli.log(Level.SEVERE, "MSG_ERR_MOLREAD",
									new Object[] { x.toString() });
							return record;
						} finally {

						}
						processed = mol;

						// CDK adds these for the first MOL line
						if (mol.getProperty(CDKConstants.TITLE) == null)
							mol.removeProperty(CDKConstants.TITLE);
						if (mol.getProperty(CDKConstants.REMARK) == null)
							mol.removeProperty(CDKConstants.REMARK);

						if ((smirksProcessor != null)
								&& smirksProcessor.isEnabled()) {
							processed = smirksProcessor.process(processed);
						}

						try {
							processed = standardprocessor.process(processed);

						} catch (Exception x) {
							logger_cli.log(Level.SEVERE, x.getMessage(), x);
							if (processed != null)
								processed.setProperty("ERROR.standardisation",
										x.getMessage());
						} finally {
							if (processed != null)
								processed.addProperties(mol.getProperties());
						}
						if (processed != null)
							try {
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

		int pagesize = 5000000;
		try {
			pagesize = (Integer) options.getParam(":pagesize");
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.toString());
		}

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
}

class Context extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1993541083813105854L;

}