package ambit2.dbsubstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.enanomapper.parser.GenericExcelParser;
import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.MySQLSingleConnection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.restlet.Context;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.StructureRecordValidator;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.substance.processor.DBSubstanceWriter;

import com.mysql.jdbc.CommunicationsException;

public class DBSubstanceImport {
	protected static Logger logger = Logger.getLogger(DBSubstanceImport.class
			.getName());
	static final String loggingProperties = "config/logging.prop";
	protected String configFile;
	protected String parserType = null;
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

	public String getParserType() {
		return parserType;
	}

	public void setParserType(String parserType) {
		this.parserType = parserType;
	}

	public String getConfigFile() {
		return configFile;
	}

	protected File inputFile;
	protected File outputFile;
	protected File jsonConfig;

	protected static String getParserType(CommandLine line) {
		if (line.hasOption('p'))
			return line.getOptionValue('p');
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
		if (line.hasOption('m')) try {
			return Boolean.parseBoolean(line.getOptionValue('m'));
		} catch (Exception x) {}
		return true;
	}

	protected static boolean isClearComposition(CommandLine line) {
		if (line.hasOption('t')) try {
			return Boolean.parseBoolean(line.getOptionValue('t'));
		} catch (Exception x) {}
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
			System.err
					.println("Config file not specified or do not exist, using default "
							+ getClass().getClassLoader().getResource(
									"config/ambit.properties"));
		}
	}

	public static void main(String[] args) {
		long now = System.currentTimeMillis();
		try {
			DBSubstanceImport object = new DBSubstanceImport();
			if (object.parse(args)) {
				object.importFile();
			} else
				System.exit(0);
		} catch (ConnectException x) {
			logger.log(Level.SEVERE,
					"Connection refused. Please verify if the remote server is responding.");
			System.exit(-1);
		} catch (CommunicationsException x) {
			logger.log(Level.SEVERE, "Can't connect to MySQL server!");
			System.exit(-1);
		} catch (SQLException x) {
			logger.log(Level.SEVERE, "SQL error", x);
			System.exit(-1);
		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage(), x);
			System.exit(-1);
		} finally {
			logger.info(String.format("Completed in %s msec",
					(System.currentTimeMillis() - now)));
			logger.info("Done.");
		}
	}

	protected Options createOptions() {
		Options options = new Options();
		Option input = OptionBuilder.hasArg().withLongOpt("input")
				.withArgName("file").withDescription("Input file").create("i");

		Option jsonConfig = OptionBuilder.hasArg().withLongOpt("json")
				.withArgName("file").withDescription("JSON config file")
				.create("j");

		Option output = OptionBuilder.hasArg().withLongOpt("output")
				.withArgName("file").withDescription("Output file").create("o");

		Option config = OptionBuilder.hasArg().withLongOpt("config")
				.withArgName("file")
				.withDescription("Config file (DB connection parameters)")
				.create("c");
		
		Option clearComposition = OptionBuilder.hasArg().withLongOpt("clearComposotion")
				.withArgName("value").withDescription("true|false")
				.create("t");		
		
		Option clearMeasurement = OptionBuilder.hasArg().withLongOpt("clearMeasurements")
				.withArgName("value").withDescription("true|false")
				.create("m");			

		Option help = OptionBuilder.withLongOpt("help")
				.withDescription("This help").create("h");

		options.addOption(input);
		options.addOption(jsonConfig);
		options.addOption(output);
		options.addOption(config);
		options.addOption(createParserTypeOption());
		options.addOption(clearComposition);
		options.addOption(clearMeasurement);
		options.addOption(help);

		return options;
	}

	protected Option createParserTypeOption() {
		return OptionBuilder.hasArg().withLongOpt("parser").withArgName("type")
				.withDescription("File parser mode : xlsx").create("p");
	}

	public boolean parse(String[] args) throws Exception {
		final Options options = createOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse(options, args, false);
			inputFile = getInput(line);
			if (inputFile == null)
				throw new Exception("Missing input file");
			jsonConfig = getJSONConfig(line);
			if (jsonConfig == null)
				throw new Exception("Missing JSON config file");

			outputFile = getOutput(line);

			setConfig(getConfig(line));

			setParserType(getParserType(line));
			
			setClearComposition(isClearComposition(line));
			setClearMeasurements(isClearMeasurements(line));

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

	protected IRawReader<IStructureRecord> createParser(InputStream in)
			throws Exception {
		return new GenericExcelParser(in, jsonConfig, true);
	}

	protected void importFile() throws Exception {
		IRawReader<IStructureRecord> parser = null;
		Connection c = null;
		try {
			FileInputStream fin = new FileInputStream(inputFile);

			DBConnection dbc = null;
			dbc = getConnection(getConfigFile());
			c = dbc.getConnection();
			c.setAutoCommit(true);

			parser = createParser(fin);
			StructureRecordValidator validator = new StructureRecordValidator(
					inputFile.getName(), true);
			write(parser, c, new ReferenceSubstanceUUID(), false,  clearMeasurements,  clearComposition,validator);
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

	protected DBConnection getConnection(String configFile)
			throws SQLException, AmbitException {
		try {
			Context context = initContext();
			String driver = context.getParameters().getFirstValue(
					Preferences.DRIVERNAME);

			if ((driver != null) && (driver.contains("mysql")))
				return new MySQLSingleConnection(context,
						"config/ambit.properties");
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
			context.getParameters().add(Preferences.DATABASE,
					properties.get(Preferences.DATABASE).toString());
			context.getParameters().add(Preferences.USER,
					properties.get(Preferences.USER).toString());
			context.getParameters().add(Preferences.PASSWORD,
					properties.get(Preferences.PASSWORD).toString());
			context.getParameters().add(Preferences.HOST,
					properties.get(Preferences.HOST).toString());

			context.getParameters().add(Preferences.PORT,
					properties.get(Preferences.PORT).toString());
			context.getParameters().add(Preferences.DRIVERNAME,
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
			StructureRecordValidator validator) throws Exception {
		return write(reader,connection,key,splitRecord,true,true,validator);
	}

	public int write(IRawReader<IStructureRecord> reader,
			Connection connection, PropertyKey key, boolean splitRecord,
			boolean clearMeasurements, boolean clearComposition,
			StructureRecordValidator validator) throws Exception {

		DBSubstanceWriter writer = new DBSubstanceWriter(
				DBSubstanceWriter.datasetMeta(), new SubstanceRecord(), clearMeasurements,
				clearComposition);
		writer.setSplitRecord(splitRecord);
		writer.setConnection(connection);
		writer.setClearComposition(clearComposition);
		writer.setClearMeasurements(clearMeasurements);
		writer.open();
		int records = 0;
		while (reader.hasNext()) {
			Object record = reader.next();
			if (record == null)
				continue;
			validate(validator, (IStructureRecord) record);
			writer.process((IStructureRecord) record);
			records++;
		}
		writer.close();
		return records;
	}

	protected void validate(StructureRecordValidator validator,
			IStructureRecord record) throws Exception {
		validator.process((IStructureRecord) record);
	}

	protected static void printHelp(Options options, String message) {
		if (message != null)
			System.out.println(message);

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(DBSubstanceImport.class.getName(), options);
		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().exit(0);
	}
}
