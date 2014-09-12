package ambit2.dbcli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.MySQLSingleConnection;

import org.restlet.Context;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.key.NoneKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.db.processors.RepositoryWriter;

import com.mysql.jdbc.CommunicationsException;

public class AmbitCli {
	static Logger logger = Logger.getLogger(AmbitCli.class.getName());
	static final String loggingProperties = "config/logging.prop";
	
	CliOptions options;
	
	public AmbitCli(CliOptions options) {
		this.options = options;
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream(loggingProperties);
//			System.setProperty("java.util.logging.config.file", url.getFile());
			LogManager.getLogManager().readConfiguration(in);
			
			System.out.println(String.format("Logging configuration loaded from %s",loggingProperties));
			System.out.println(LogManager.getLogManager().getProperty("org.openscience.cdk"));
		} catch (Exception x) {
			System.err.println("logging configuration failed "+ x.getMessage());
		} finally {
			try { if (in!=null) in.close(); } catch (Exception x) {}
		}		
	}
	
	public long go(String command,String subcommand) throws Exception {
		long now = System.currentTimeMillis();
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
			System.out.print("Elapsed ");
			System.out.println(System.currentTimeMillis() - now);
			try {if (reader != null)	reader.close();	} catch (Exception x) {}
			if (options.output!=null) {
				logger.log(Level.INFO,"Results written to "+options.output);
			}
		}
	}
	
	public static void main(String[] args) {
		long now = System.currentTimeMillis();
		try {
			CliOptions options = new CliOptions();
			if (options.parse(args)) {
				AmbitCli navigator = new AmbitCli(options);
				logger.info(String.format("Running %s (%s)\tSubcommand:\t%s:%s",
						options.getCommand().get("name"),options.getCommand().get("connection"),
						options.getSubcommand(),options.getCommand().get(options.getSubcommand().name())));
				navigator.go(options.getCmd(),options.getSubcommand().name());
			} else 
				System.exit(0);
		} catch (ConnectException x) {
			logger.log(Level.SEVERE,"Connection refused. Please verify if the remote server is responding.");
			System.exit(-1);
		} catch (CommunicationsException x) {
			logger.log(Level.SEVERE,"Can't connect to MySQL server!");
			System.exit(-1);
		} catch (SQLException x) {
			logger.log(Level.SEVERE,"SQL error");
			System.exit(-1);		
		} catch (Exception x ) {
			logger.log(Level.WARNING,x.getMessage());
			System.exit(-1);
		} finally {
			logger.info(String.format("Completed in %s msec", (System.currentTimeMillis()-now)));
			logger.info("Done.");
		}
	}
	
	protected RepositoryWriter initWriter(RepositoryWriter writer, PropertyKey key,SourceDataset dataset) throws Exception  {

		try {
			if (writer!=null) {
				logger.log(Level.INFO,"Closing the connection ...");
				writer.close();
			}
			writer = null;
		} catch (Exception xx) { logger.log(Level.WARNING,xx.getMessage(),xx);}
		
		logger.log(Level.INFO,"Opening a new connection ...");		
		
		Connection c = null;
		DBConnection dbc = null;
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
		logger.log(Level.INFO,"Connection opened.");
		return writer;
	}

	public long write(IRawReader<IStructureRecord> reader,
			PropertyKey key, SourceDataset dataset,
			int maxrecords) throws Exception {
		
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
					if (writer==null) throw x;
				}
				records++;
				
				if ((System.currentTimeMillis()-connectionOpened)> options.connectionLifeTime) //restart the connection
					try {
						writer = initWriter(writer, key, dataset);
						connectionOpened = System.currentTimeMillis();
					} catch (Exception xx) {
						writer = null;
						throw xx;
					}
					
				if ((records % 1000)==0) {
					now = System.currentTimeMillis();
					System.out.println(String.format("Records read %d ; %f msec per record\t",records,((now-start)/1000.0)));
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
			try {reader.close();} catch (Exception x)  {}
			try {writer.close();} catch (Exception x) {}
		}

		return records;
	}

	protected DBConnection getConnection(String configFile)
			throws SQLException, AmbitException {
		try {
			Context context = initContext();
			String driver = context.getParameters().getFirstValue(
					Preferences.DRIVERNAME);

			if ((driver != null) && (driver.contains("mysql")))
				return new MySQLSingleConnection(context, configFile);
			else
				throw new AmbitException("Driver not supported");
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
}
