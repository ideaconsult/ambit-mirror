package ambit2.dbsubstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import net.enanomapper.parser.GenericExcelParser;
import net.enanomapper.parser.InvalidCommand;
import net.idea.i5.io.I5Options;
import net.idea.i5.io.I5ZReader;
import net.idea.i5.io.QASettings;
import net.idea.loom.nm.nanowiki.ENanoMapperRDFReader;
import net.idea.loom.nm.nanowiki.NanoWikiRDFReader;
import net.idea.modbcum.c.DBConnectionConfigurable;
import net.idea.modbcum.c.MySQLSingleConnection;
import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.PropertyConfigurator;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.StructureRecordValidator;
import ambit2.base.data.substance.ParticleTypes;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.core.processors.structure.key.EINECSKey;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.InchiKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.substance.processor.DBSubstanceWriter;

import com.mysql.jdbc.CommunicationsException;

public class DBSubstanceImport {
	protected static Logger logger_cli = Logger
			.getLogger(DBSubstanceImport.class.getName());
	static final String loggingProperties = "ambit2/dbsubstance/logging.properties";
	static final String log4jProperties = "ambit2/dbsubstance/log4j.properties";

	static {
		logger_cli = Logger.getLogger("ambitcli", "ambit2.dbsubstance.msg");
		Locale.setDefault(Locale.ENGLISH);
		String dOption = System.getProperty("java.util.logging.config.file");
		if (dOption == null || "".equals(dOption)) {
			InputStream in = null;
			try {
				in = DBSubstanceImport.class.getClassLoader()
						.getResourceAsStream(loggingProperties);
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
			in = DBSubstanceImport.class.getClassLoader().getResourceAsStream(
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

	protected IStructureKey matchByKey = new ReferenceSubstanceUUID();
	protected String configFile;
	protected _parsertype parserType = _parsertype.xlsx;

	protected boolean isSplitRecord = false;
	protected boolean gzipped = false;

	public boolean isSplitRecord() {
		return isSplitRecord;
	}

	public void setSplitRecord(boolean isSplitRecord) {
		this.isSplitRecord = isSplitRecord;
	}

	protected boolean clearMeasurements = true;

	public boolean isClearMeasurements() {
		return clearMeasurements;
	}

	public void setClearMeasurements(boolean clearMeasurements) {
		this.clearMeasurements = clearMeasurements;
	}

	public boolean isClearComposition() {
		return clearComposition;
	}

	public void setClearComposition(boolean clearComposition) {
		this.clearComposition = clearComposition;
	}

	protected boolean clearComposition = true;

	public enum _parsertype {
		i5z, xlsx, xls, nanowiki, modnanotox, enmrdf
	}

	public _parsertype getParserType() {
		return parserType;
	}

	public void setParserType(_parsertype parserType) {
		this.parserType = parserType;
	}

	public String getConfigFile() {
		return configFile;
	}

	protected File inputFile;

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
		if (inputFile.toString().toLowerCase().endsWith(".gz"))
			gzipped = true;
		else
			gzipped = false;
	}

	protected File outputFile;
	protected File jsonConfig;

	protected static _parsertype getParserType(CommandLine line)
			throws Exception {
		if (line.hasOption('p'))
			try {
				return _parsertype.valueOf(line.getOptionValue('p'));
			} catch (Exception x) {
				throw x;
			}
		else
			return null;
	}

	protected static _mode_matchstructure getStructureMatchMode(CommandLine line)
			throws Exception {
		if (line.hasOption('x'))
			try {
				return _mode_matchstructure.valueOf(line.getOptionValue('x'));
			} catch (Exception x) {
				return null;
			}
		else
			return null;
	}

	protected static File getOutput(CommandLine line) {
		if (line.hasOption('o')) {
			return new File(line.getOptionValue('o'));
		} else
			return null;
	}

	protected static boolean isClearMeasurements(CommandLine line) {
		if (line.hasOption('m'))
			try {
				return Boolean.parseBoolean(line.getOptionValue('m'));
			} catch (Exception x) {
			}
		return true;
	}

	protected int maxRefSubstances = -1;

	protected static int getMaxRefSubstances(CommandLine line) {
		if (line.hasOption('r'))
			try {
				return Integer.parseInt(line.getOptionValue('r'));
			} catch (Exception x) {
			}
		return -1;
	}

	/**
	 * If splitrecord = true and record.getMeasurements() is not null, only
	 * measurements will be imported if record.getmeasurements() is null, only
	 * the substance record will be imported
	 * 
	 * @param line
	 * @return
	 */
	protected static boolean isSplitRecord(CommandLine line) {
		if (line.hasOption('s'))
			try {
				return Boolean.parseBoolean(line.getOptionValue('s'));
			} catch (Exception x) {
			}
		return false;
	}

	protected static boolean isClearComposition(CommandLine line) {
		if (line.hasOption('t'))
			try {
				return Boolean.parseBoolean(line.getOptionValue('t'));
			} catch (Exception x) {
			}
		return true;
	}

	protected static File getJSONConfig(CommandLine line)
			throws FileNotFoundException {
		if (line.hasOption('j')) {
			File file = new File(line.getOptionValue('j'));
			if (!file.exists())
				throw new FileNotFoundException(file.getName());
			else
				return file;
		} else
			return null;
	}

	protected static File getInput(CommandLine line)
			throws FileNotFoundException {
		if (line.hasOption('i')) {
			File file = new File(line.getOptionValue('i'));
			if (!file.exists())
				throw new FileNotFoundException(file.getName());
			else
				return file;
		} else
			return null;
	}

	protected static String getConfig(CommandLine line) {
		if (line.hasOption('c'))
			return line.getOptionValue('c');
		else
			return null;
	}

	protected void setConfig(String config) {
		if (config != null) {
			if (!new File(config).exists())
				config = null;
			else
				this.configFile = config;
		}
		if (config == null) {
			this.configFile = null;
			logger_cli.log(Level.WARNING,
					"Config file not specified or do not exist!");

		}
	}

	public static void main(String[] args) {
		logger_cli.log(Level.INFO, "MSG_INFO_VERSION");
		long now = System.currentTimeMillis();
		int code = 0;
		try {
			DBSubstanceImport object = new DBSubstanceImport();
			if (object.parse(args)) {
				object.importFile();
			} else
				code = -1;

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
		} catch (Exception x) {
			logger_cli.log(Level.SEVERE, "MSG_ERR", new Object[] { x });
			code = -1;
		} finally {
			if (code >= 0)
				logger_cli.log(Level.INFO, "MSG_INFO_COMPLETED",
						(System.currentTimeMillis() - now));
		}
	}

	private static enum _mode_matchstructure {
		uuid {
			@Override
			public IStructureKey getKey() {
				return new ReferenceSubstanceUUID();
			}
		},
		cas {
			@Override
			public IStructureKey getKey() {
				return new CASKey();
			}
		},
		einecs {
			@Override
			public IStructureKey getKey() {
				return new EINECSKey();
			}
		},
		smiles {
			@Override
			public IStructureKey getKey() {
				return new SmilesKey();
			}
		},
		inchi {
			@Override
			public IStructureKey getKey() {
				try {
					return new InchiKey();
				} catch (Exception x) {
					return null;
				}
			}
		};
		public abstract IStructureKey getKey();
	};

	protected Options createOptions() {
		Options options = new Options();
		Option input = OptionBuilder.hasArg().withLongOpt("input")
				.withArgName("file").withDescription("Input file or folder")
				.create("i");

		Option jsonConfig = OptionBuilder.hasArg().withLongOpt("json")
				.withArgName("file").withDescription("JSON config file")
				.create("j");

		Option output = OptionBuilder.hasArg().withLongOpt("output")
				.withArgName("file").withDescription("Output file").create("o");

		Option config = OptionBuilder.hasArg().withLongOpt("config")
				.withArgName("file")
				.withDescription("Config file (DB connection parameters)")
				.create("c");

		Option clearComposition = OptionBuilder.hasArg()
				.withLongOpt("clearComposition").withArgName("value")
				.withDescription("true|false").create("t");

		Option clearMeasurement = OptionBuilder.hasArg()
				.withLongOpt("clearMeasurements").withArgName("value")
				.withDescription("true|false").create("m");

		Option matchStructure = OptionBuilder
				.hasArg()
				.withLongOpt("structureMatch")
				.withArgName("value")
				.withDescription(
						"Match structure by uuid|cas|einecs|smiles|inchi")
				.create("x");

		Option maxRefSubstances = OptionBuilder
				.hasArg()
				.withLongOpt("maxReferenceSubstances")
				.withArgName("value")
				.withDescription(
						"Maximum reference substances in *.i5z archive")
				.create("r");

		Option isSplitRecord = OptionBuilder.hasArg()
				.withLongOpt("isSplitRecord").withArgName("value")
				.withDescription("true|false").create("s");

		/*
		 * Option gzip = OptionBuilder.hasArg().withLongOpt("gzipped")
		 * .withDescription("Gzipped file").create("z");
		 */
		Option help = OptionBuilder.withLongOpt("help")
				.withDescription("This help").create("h");

		options.addOption(input);
		options.addOption(jsonConfig);
		options.addOption(output);
		options.addOption(config);
		options.addOption(createParserTypeOption());
		options.addOption(clearComposition);
		options.addOption(clearMeasurement);
		options.addOption(matchStructure);
		options.addOption(isSplitRecord);
		options.addOption(maxRefSubstances);

		options.addOption(help);

		return options;
	}

	protected Option createParserTypeOption() {
		StringBuilder b = new StringBuilder();
		String d = "";
		for (_parsertype p : _parsertype.values()) {
			b.append(d);
			b.append(p.name());
			d = "|";
		}
		;
		return OptionBuilder.hasArg().withLongOpt("parser").withArgName("type")
				.withDescription("File parser mode : " + b.toString())
				.create("p");
	}

	public boolean parse(String[] args) throws Exception {
		final Options options = createOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse(options, args, false);
			setParserType(getParserType(line));
			try {
				matchByKey = getStructureMatchMode(line).getKey();
			} catch (Exception x) {
				matchByKey = new ReferenceSubstanceUUID();
			}
			setInputFile(getInput(line));

			if (inputFile == null)
				throw new Exception("Missing input file");
			jsonConfig = getJSONConfig(line);

			if ("xslx".equals(getParserType()))
				if (jsonConfig == null)
					throw new Exception(
							"Missing JSON config file, mandatory for importing XLSX!");

			outputFile = getOutput(line);

			String config = getConfig(line);
			if (config == null || !(new File(config)).exists())
				throw new Exception("Missing database connection config file");
			setConfig(config);

			setClearComposition(isClearComposition(line));
			setClearMeasurements(isClearMeasurements(line));
			setSplitRecord(isSplitRecord(line));
			maxRefSubstances = getMaxRefSubstances(line);
			if (line.hasOption("h")) {
				printHelp(options, null);
				return false;
			}
			return true;
		} catch (Exception x) {
			printHelp(options, x.getMessage());
			throw x;
		} finally {

		}
	}

	protected IRawReader<IStructureRecord> createParser(InputStream stream,
			boolean xlsx) throws Exception {
		_parsertype mode = getParserType();
		InputStream in = null;
		if (gzipped)
			in = new GZIPInputStream(stream);
		else
			in = stream;
		if (mode == null) {
			logger_cli.log(Level.INFO, "MSG_IMPORT", new Object[] {
					"parser type not specified, assuming",
					xlsx ? "xlsx" : "xls" });
			return new GenericExcelParser(in, jsonConfig, xlsx);
		} else
			switch (mode) {
			case enmrdf:
				return new ENanoMapperRDFReader(new InputStreamReader(in),
						"ENM3");
			case nanowiki:
				return new NanoWikiRDFReader(new InputStreamReader(in));
			default:
				if (jsonConfig == null)
					throw new FileNotFoundException(
							"JSON config file not specified!");
				return new GenericExcelParser(in, jsonConfig, xlsx);
			}
	}

	protected int importFile() throws Exception {
		if (inputFile.isDirectory()) {
			logger_cli.log(Level.INFO, "MSG_IMPORT", new Object[] { "folder",
					inputFile.getAbsolutePath() });
			File[] allFiles = inputFile.listFiles();
			long started = System.currentTimeMillis();
			int allrecords = 0;
			for (int i = 0; i < allFiles.length; i++) {
				File file = allFiles[i];
				setInputFile(file);
				try {
					allrecords += importFile();
				} catch (Exception x) {
					logger_cli.log(Level.INFO, "MSG_ERR",
							new Object[] { x.getMessage() });
				} finally {
					long now = System.currentTimeMillis();
					logger_cli.log(Level.INFO, "MSG_INFO_RECORDS",
							new Object[] {
									(i + 1),
									(double) (now - started)
											/ ((double) (i + 1)),
									allrecords,
									(double) (now - started)
											/ ((double) allrecords) });
				}
			}
			return allrecords;
		} else {
			String ext = inputFile.toString().toLowerCase();
			String unzippedext = ext;
			if (ext.endsWith(".gz")) {
				int p = ext.length();
				unzippedext = ext.substring(1,p-3);
			}
			if (ext.endsWith(".i5z"))
				return importI5Z(matchByKey);
			else if (unzippedext.endsWith(".ttl") || unzippedext.endsWith(".rdf"))
				return importFile(false,false,true);			
			else
				return importFile(isSplitRecord(), unzippedext.endsWith(".xlsx"),false);

		}
	}

	protected int importI5Z(IStructureKey keytomatch) throws Exception {
		logger_cli.log(Level.INFO, "MSG_IMPORT", new Object[] { "i5z",
				inputFile.getAbsolutePath() });
		I5ZReader reader = null;
		Connection c = null;
		try {

			DBConnectionConfigurable<Context> dbc = null;
			dbc = getConnection(getConfigFile());
			c = dbc.getConnection();
			c.setAutoCommit(true);
			I5Options options = new I5Options();

			options.setMaxReferenceStructures(maxRefSubstances);
			options.setExceptionOnMaxReferenceStructures(false);
			options.setAllowMultipleSubstances(false);
			reader = new I5ZReader<>(inputFile, options);
			QASettings qa = new QASettings(false);
			qa.setAll();
			reader.setQASettings(qa);

			matchByKey = keytomatch == null ? new CASKey() : keytomatch;
			return write(reader, c, matchByKey, true, clearMeasurements,
					clearComposition, null, true,false);
		} catch (Exception x) {
			throw x;
		} finally {
			if (reader != null)
				reader.close();
			try {
				if (c != null)
					c.close();
			} catch (Exception x) {
			}
		}
	}

	protected int importFile(boolean splitRecord, boolean xlsx, boolean importBundles)
			throws Exception {
		IRawReader<IStructureRecord> parser = null;
		Connection c = null;
		try {
			FileInputStream fin = new FileInputStream(inputFile);

			parser = createParser(fin, xlsx);
			logger_cli.log(Level.INFO, "MSG_IMPORT", new Object[] {
					parser.getClass().getName(), inputFile.getAbsolutePath() });

			StructureRecordValidator validator = new StructureRecordValidator(
					inputFile.getName(), true) {
				@Override
				public IStructureRecord validate(SubstanceRecord record)
						throws Exception {
					if (record.getRelatedStructures() != null
							&& !record.getRelatedStructures().isEmpty()) {

						for (int i = record.getRelatedStructures().size() - 1; i >= 0; i--) {
							CompositionRelation rel = record
									.getRelatedStructures().get(i);
							int props = 0;
							for (Property p : rel.getSecondStructure()
									.getRecordProperties()) {
								Object val = rel.getSecondStructure()
										.getRecordProperty(p);
								if (val != null && !"".equals(val.toString()))
									props++;
							}
							if ((rel.getContent() == null || "".equals(rel
									.getContent())) && (props == 0))
								record.getRelatedStructures().remove(i);

						}

					}
					return super.validate(record);
				}
			};

			DBConnectionConfigurable<Context> dbc = null;
			dbc = getConnection(getConfigFile());
			c = dbc.getConnection();
			c.setAutoCommit(true);

			return write(parser, c, matchByKey, splitRecord, clearMeasurements,
					clearComposition, validator, false,importBundles);
		} catch (Exception x) {
			throw x;
		} finally {
			if (parser != null)
				parser.close();
			try {
				c.close();
			} catch (Exception x) {
			}
		}
	}

	protected DBConnectionConfigurable<Context> getConnection(String configFile)
			throws SQLException, AmbitException {
		try {
			Context context = initContext();
			String driver = context.get(Preferences.DRIVERNAME);

			if ((driver != null) && (driver.contains("mysql")))
				return new MySQLSingleConnection(context, configFile);
			else
				throw new AmbitException("Driver not supported");
		} catch (Exception x) {
			x.printStackTrace();
			throw new AmbitException(x);
		}
	}

	protected synchronized Context initContext() throws Exception {
		if (getConfigFile() == null)
			return new Context();

		InputStream in = null;
		try {
			Properties properties = new Properties();
			in = new FileInputStream(getConfigFile());

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

	public int write(IRawReader<IStructureRecord> reader,
			Connection connection, PropertyKey key, boolean splitRecord,
			StructureRecordValidator validator, boolean i5mode, boolean importBundles)
			throws Exception {
		return write(reader, connection, key, splitRecord, true, true,
				validator, i5mode,importBundles);
	}

	public int write(IRawReader<IStructureRecord> reader,
			Connection connection, IStructureKey key, boolean splitRecord,
			boolean clearMeasurements, boolean clearComposition,
			StructureRecordValidator validator, boolean i5mode, boolean importBundles)
			throws Exception {

		DBSubstanceWriter writer = new DBSubstanceWriter(
				DBSubstanceWriter.datasetMeta(), new SubstanceRecord(),
				clearMeasurements, clearComposition, key);
		writer.setImportBundles(importBundles);
		writer.setI5mode(i5mode);
		writer.setSplitRecord(splitRecord);
		writer.setConnection(connection);
		writer.setClearComposition(clearComposition);
		writer.setClearMeasurements(clearMeasurements);
		writer.open();
		int records = 0;
		try {
			while (reader.hasNext()) {
				Object record = reader.next();
				if (record == null)
					continue;
				if (record instanceof SubstanceRecord)
					configure(writer, (SubstanceRecord) record);
				validate(validator, (IStructureRecord) record);
				writer.process((IStructureRecord) record);
				records++;
			}
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			writer.close();
			logger_cli
					.log(Level.INFO, "MSG_IMPORTED", new Object[] { records });
		}
		return records;
	}

	protected void configure(DBSubstanceWriter writer, SubstanceRecord record) {

	}

	protected void validate(StructureRecordValidator validator,
			IStructureRecord record) throws Exception {
		if (validator != null) {
			if (record instanceof SubstanceRecord) {
				_parsertype mode = getParserType();
				SubstanceRecord srecord = (SubstanceRecord) record;
				if (mode == null) {
					cleanReferenceStructure(srecord);
					List<ProtocolApplication> m = srecord.getMeasurements();
					cleanupEmptyRecords(srecord, m);
					validator.process((IStructureRecord) record);
				} else
					switch (mode) {
					case i5z: {
						break;
					}
					case xlsx: {
						cleanReferenceStructure(srecord);
						List<ProtocolApplication> m = srecord.getMeasurements();
						cleanupEmptyRecords(srecord, m);
						validator.process((IStructureRecord) record);
						break;
					}
					case xls: {
						cleanReferenceStructure(srecord);
						List<ProtocolApplication> m = srecord.getMeasurements();
						cleanupEmptyRecords(srecord, m);
						validator.process((IStructureRecord) record);
						break;
					}
					case nanowiki: {
						validator.process((IStructureRecord) record);
						break;
					}
					}
			}

		}
	}

	protected static void printHelp(Options options, String message) {
		if (message != null)
			logger_cli.log(Level.WARNING, message);

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ambitsi-{version}", options);
		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().exit(0);
	}

	protected void cleanReferenceStructure(SubstanceRecord srecord) {
		if (srecord.getReferenceSubstanceUUID() == null) {
			logger_cli.log(Level.WARNING, "Missing Reference Substance UUID");

			if (srecord.getReferenceSubstanceUUID() == null)
				try {
					ParticleTypes stype = ParticleTypes.valueOf(srecord
							.getSubstancetype());
					logger_cli.log(Level.WARNING,
							"Missing Reference Substance UUID");
					String ref_uuid = stype.getReferenceUUID();
					if (ref_uuid != null)
						srecord.setReferenceSubstanceUUID(ref_uuid);

				} catch (Exception x) {
				}
		}

		List<ProtocolApplication> m = srecord.getMeasurements();
		cleanupEmptyRecords(srecord, m);
	}

	protected void cleanupEmptyRecords(SubstanceRecord srecord,
			List<ProtocolApplication> m) {
		if (m == null)
			return;
		for (int i = m.size() - 1; i >= 0; i--) {
			ProtocolApplication<Protocol, IParams, String, IParams, String> papp = m
					.get(i);
			boolean empty = true;
			if (papp.getEffects() != null) {
				for (EffectRecord<String, IParams, String> effect : papp
						.getEffects()) {
					if (effect.getLoValue() != null
							|| effect.getUpValue() != null)
						effect.setTextValue(null);
				}

				List<EffectRecord<String, IParams, String>> effects = papp
						.getEffects();
				for (int j = effects.size() - 1; j >= 0; j--) {
					if (!effects.get(j).isEmpty()) {
						empty = false;
					} else {
						logger_cli
								.log(Level.WARNING,
										String.format(
												"Remove empty effect record\t%s\t%s\t%s\t%s\t%s [%s]",
												srecord.getSubstanceName(),
												srecord.getPublicName(), papp
														.getProtocol()
														.getTopCategory(), papp
														.getProtocol()
														.getCategory(), papp
														.getProtocol()
														.getEndpoint(), papp
														.getEffects().get(j)
														.getErrQualifier()));
						papp.getEffects().remove(j);
					}
				}

				if (empty
						&& ((papp.getInterpretationResult() == null) || ""
								.equals(papp.getInterpretationResult()))) {
					m.remove(i);
					logger_cli
							.log(Level.WARNING,
									String.format(
											"Remove empty protocol application\t%s\t%s\t%s\t%s",
											srecord.getSubstanceName(), srecord
													.getPublicName(), papp
													.getProtocol()
													.getTopCategory(), papp
													.getProtocol()
													.getCategory(), papp
													.getProtocol()
													.getEndpoint()));
				}
			}
		}
	}

}

class Context extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1993541083813105854L;

}