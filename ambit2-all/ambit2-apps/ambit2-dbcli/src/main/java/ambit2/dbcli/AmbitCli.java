package ambit2.dbcli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
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

/**
 * 
 * @author nina
 *
 */
public class AmbitCli {
	static Logger logger = Logger.getLogger(AmbitCli.class.getName());
	static final String loggingProperties = "config/logging.prop";
	
	CliOptions options;
	
	public AmbitCli(CliOptions options) {
		this.options = options;
		/*
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream(loggingProperties);
//			System.setProperty("java.util.logging.config.file", url.getFile());
			LogManager.getLogManager().readConfiguration(in);
			System.out.println(String.format("Logging configuration loaded from %s",loggingProperties));
		} catch (Exception x) {
			System.err.println("logging configuration failed "+ x.getMessage());
		} finally {
			try { if (in!=null) in.close(); } catch (Exception x) {}
		}
		*/		
	}
	
	public long go(String command,String subcommand) throws Exception {
		long now = System.currentTimeMillis();
		if ("dataset".equals(command)) {
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
				logger.info("Elapsed "+(System.currentTimeMillis() - now) + " msec.");
				try {if (reader != null)	reader.close();	} catch (Exception x) {}
				if (options.output!=null) {
					logger.log(Level.INFO,"Results written to "+options.output);
				}
			}
		} else if ("split".equals(command)) {
			RawIteratingSDFReader reader = null;
			Writer writer = null;
			long chunksize= 10000;
			
			JsonNode scmd = options.command.get(subcommand);
			try {
				JsonNode scommand = scmd.get("params");
				JsonNode chunkNode = scommand.get(":chunk");
				chunksize = Long.parseLong(chunkNode.get("value").getTextValue());
			} catch (Exception x) {
				logger.log(Level.WARNING,x.getMessage(),x);
			}

			int chunk = 1;
			long chunk_started=System.currentTimeMillis();
			try {
				File file = new File(options.input);
				File outdir = new File(options.output);
				logger.info(String.format("Splitting %s into files of size %d records into directory %s",
						file.getAbsoluteFile(),
						chunksize,
						outdir.getAbsolutePath()
				));	
				
				if (outdir.exists() && outdir.isDirectory()) {
					reader = new RawIteratingSDFReader(new FileReader(file));
					File outfile = new File(outdir,String.format("%d_%s",chunk,file.getName()));
					chunk_started=System.currentTimeMillis();
					logger.info(String.format("Writing chunk %d:\t%s ...",chunk,outfile.getAbsolutePath()));
					writer = new FileWriter(outfile);
					int records = 0;
					while (reader.hasNext()) {
						if (records >= chunksize) {
							System.out.println();
							try {if (writer != null)	writer.close();	} catch (Exception x) {}
							logger.info(String.format("Chunk %d written in %d msec.",chunk,(System.currentTimeMillis()-chunk_started)));
							chunk++;
							outfile = new File(outdir,String.format("%d_%s",chunk,file.getName()));
							writer = new FileWriter(outfile);
							records = 0;
							chunk_started=System.currentTimeMillis();

							logger.info(String.format("Writing chunk %d:\t%s ...",chunk,outfile.getAbsolutePath()));	
						}
						IStructureRecord record = reader.nextRecord();
						writer.write(record.getContent());
						if ((records % 10000)==0) {
							System.out.print('.');
							writer.flush();
						}									
						records++;
					}
					return chunk;
				} else throw new Exception(String.format("ERROR: %s is not an existing directory.",options.output));
			} catch (Exception x) {
				throw x;
			} finally {
				logger.info("Elapsed "+(System.currentTimeMillis() - now) + " msec.");
				try {if (reader != null)	reader.close();	} catch (Exception x) {}
				try {
					if (writer != null)	writer.close();
					logger.info(String.format("Chunk %d written in %d msec.",chunk,(System.currentTimeMillis()-chunk_started)));
				} catch (Exception x) {}
				if (options.output!=null) {
					logger.log(Level.INFO,"Results written to "+options.output);
				}
			}			
		}
		return -1;
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
			logger.log(Level.WARNING,x.getMessage(),x);
			System.exit(-1);
		} finally {
			logger.info(String.format("Completed in %s msec", (System.currentTimeMillis()-now)));
			logger.info("Done.");
		}
	}
	
	protected RepositoryWriter initWriter(RepositoryWriter writer, PropertyKey key,SourceDataset dataset) throws Exception  {

		try {
			if (writer!=null) {
				logger.log(Level.FINE,"Closing the connection ...");
				writer.close();
			}
			writer = null;
		} catch (Exception xx) { logger.log(Level.WARNING,xx.getMessage(),xx);}
		
		logger.log(Level.FINE,"Opening a new connection ...");		
		
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
		logger.log(Level.FINE,"Connection opened.");
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
					logger.info(String.format("Records read %d ; %f msec per record\t",records,((now-start)/1000.0)));
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
