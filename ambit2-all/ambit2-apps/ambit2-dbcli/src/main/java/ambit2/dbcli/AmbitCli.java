package ambit2.dbcli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
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
	CliOptions options;
	
	public AmbitCli(CliOptions options) {
		this.options = options;
	}
	
	public long go(String command,String subcommand) throws Exception {
		long now = System.currentTimeMillis();
		RawIteratingSDFReader reader = null;
		Connection c = null;
		DBConnection dbc = null;
		try {
			File file = new File(options.input);
			dbc = getConnection(options.getSQLConfig());
			c = dbc.getConnection();
			reader = new RawIteratingSDFReader(new FileReader(file));
			reader.setReference(LiteratureEntry.getInstance(file.getName()));
			SourceDataset dataset = new SourceDataset(file.getName(),
					LiteratureEntry.getInstance("File", file.getName()));
			return write(reader, c, new NoneKey(), dataset, -1);
		} catch (Exception x) {
			throw x;
		} finally {
			System.out.print("Elapsed ");
			System.out.println(System.currentTimeMillis() - now);
			try {if (reader != null)	reader.close();	} catch (Exception x) {}
			try {if (c != null)	c.close();} catch (Exception x) {}
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

	public long write(IRawReader<IStructureRecord> reader,
			Connection connection, PropertyKey key, SourceDataset dataset,
			int maxrecords) throws Exception {
		RepositoryWriter writer = new RepositoryWriter();
		if (key != null)
			writer.setPropertyKey(key);
		writer.setDataset(dataset);
		writer.setConnection(connection);
		writer.open();
		long records = 0;
		long now = System.currentTimeMillis();
		long start = now;
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			writer.write(record);
			records++;
			
			if ((records % 1000)==0) {
				now = System.currentTimeMillis();
				logger.log(Level.INFO,String.format("Records read %d ; %f msec per record\t",records,((now-start)/1000.0)));
				start = now;		
			}			
			if (maxrecords <= 0 || (records <= maxrecords))
				continue;
			else
				break;

		}
		reader.close();
		writer.close();
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
