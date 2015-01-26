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
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.batch.IBatchStatistics.RECORDS_STATS;
import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.MySQLSingleConnection;

import org.codehaus.jackson.JsonNode;
import org.restlet.Context;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IChemical;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.StructureNormalizer;
import ambit2.core.processors.structure.key.NoneKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.db.DbReader;
import ambit2.db.processors.AbstractRepositoryWriter.OP;
import ambit2.db.processors.AbstractUpdateProcessor;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.processors.QuickImportBatchProcessor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.structure.FingerprintsByStatus;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.db.search.structure.MissingInChIsQuery;
import ambit2.db.update.chemical.UpdateChemical;
import ambit2.db.update.qlabel.smarts.SMARTSAcceleratorWriter;
import ambit2.dbcli.CliOptions._preprocessingoptions;
import ambit2.descriptors.processors.BitSetGenerator;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.smarts.processors.SMARTSPropertiesGenerator;

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
		if ("import".equals(command)) {
			parseCommandImport();
		} else if ("preprocessing".equals(command)) {
			parseCommandPreprocessing();
		} else if ("dataset".equals(command)) {
			return parseCommandDataset(now);
		} else if ("split".equals(command)) {
			return parseCommandSplit(subcommand,now);
		}
		return -1;
	}
	
	public static void main(String[] args) {
		long now = System.currentTimeMillis();
		try {
			CliOptions options = new CliOptions();
			if (options.parse(args)) {
				AmbitCli navigator = new AmbitCli(options);
				logger.info(String.format("Running %s (%s)\nSubcommand:\t%s:%s",
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
			logger.log(Level.SEVERE,"SQL error",x);
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
	
	
    protected void disableIndices(Connection connection) throws SQLException {
    	Statement t = null;
    	try {
	        t = connection.createStatement();
	        t.addBatch("SET FOREIGN_KEY_CHECKS = 0;");
	        t.addBatch("SET UNIQUE_CHECKS = 0;");
	        t.addBatch("SET AUTOCOMMIT = 0;");
	        t.executeBatch();
	        logger.log(Level.INFO,"Indexes disabled.");
    	} catch (SQLException x) {
    		logger.log(Level.WARNING,"Error disabling indexes ...");

    		throw x;
    	} finally {
    		try {if (t!=null) t.close();} catch (Exception x) {}
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
	        logger.log(Level.INFO,"Indexes enabled.");
    	} catch (SQLException x) {
    		logger.log(Level.WARNING,"Error enabling indexes ...");
    		throw x;
    	} finally {
    		try {if (t!=null) t.close();} catch (Exception x) {}
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
			logger.info("Elapsed "+(System.currentTimeMillis() - now) + " msec.");
			try {if (reader != null)	reader.close();	} catch (Exception x) {}
			if (options.output!=null) {
				logger.log(Level.INFO,"Results written to "+options.output);
			}
		}
    }
    protected long parseCommandSplit(String subcommand,long now) throws Exception  {
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
    
    public void parseCommandImport() throws Exception {
    	File file = new File(options.input);
		
		final QuickImportBatchProcessor batch = new QuickImportBatchProcessor(file) {
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
						logger.log(Level.INFO,stats.toString());
						getConnection().commit();
					} catch (Exception x) {
						logger.log(Level.WARNING,x.getMessage());	
					}
			};
			@Override
			public void onError(IStructureRecord input, Object output,
					IBatchStatistics stats, Exception x) {
				super.onError(input, output, stats, x);
				logger.log(Level.SEVERE,x.getMessage());
			}
		};

		Connection c = null;
		DBConnection dbc = null;
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
			logger.log(Level.WARNING,x.getMessage(),x);
		} finally {
			try {batch.getConnection().commit();} catch (Exception x) { logger.warning(x.getMessage());}
			try {if (batch!=null) batch.close();} catch (Exception x) { logger.warning(x.getMessage());}
			if (stats!=null) 
				logger.log(Level.INFO,stats.toString());
		}    	
    }
    
    public void parseCommandPreprocessing() throws Exception {
		int pagesize= 5000000;
		try {
			pagesize = (Integer)options.getParam(":pagesize");
		} catch (Exception x) {
			logger.log(Level.WARNING,x.toString());
		}

		Set<FPTable> preprocessingOption = new TreeSet<FPTable>();
		/*
		try {
			if ((Boolean)options.getParam(":pubchemfp")) preprocessingOption.add(FPTable.pc1024);
		} catch (Exception x) {
			x.printStackTrace();
		}
		*/
		_preprocessingoptions[] po = _preprocessingoptions.values();
		for (_preprocessingoptions p  : po) 
			try {
				if ((Boolean)options.getParam(p.toString())) {
					FPTable[] to = p.getOption();
					for (FPTable t: to )
						 preprocessingOption.add(t);
				}
			} catch (Exception x) {
				logger.log(Level.WARNING,x.toString());
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
						logger.log(Level.INFO,stats.toString());
						getConnection().commit();
					} catch (Exception x) {
						logger.log(Level.WARNING,x.getMessage());	
					}
					
			};
			@Override
			public void onError(IStructureRecord input, Object output,
					IBatchStatistics stats, Exception x) {
				super.onError(input, output, stats, x);
				logger.log(Level.SEVERE,x.getMessage());
			}
		};
		batch.setProcessorChain(new ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor>());
		
		/*structure*/
		RetrieveStructure queryP = new RetrieveStructure(true);
		queryP.setFieldname(true);
		queryP.setPageSize(1);	queryP.setPage(0);

		MasterDetailsProcessor<IStructureRecord,IStructureRecord,IQueryCondition> strucReader = new MasterDetailsProcessor<IStructureRecord,IStructureRecord,IQueryCondition>(queryP) {
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
				//super.configureQuery(target, query);
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
		//preprocessing itself
		
		//query
		IQueryRetrieval<IStructureRecord> query = null;
		AbstractUpdate updateQuery = null;
		if (preprocessingOption.isEmpty()) preprocessingOption.add(FPTable.inchi);
		
		if (preprocessingOption.contains(FPTable.inchi)) {
			query = new MissingInChIsQuery("UNKNOWN");
			updateQuery = new UpdateChemical();
			batch.getProcessorChain().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
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
			batch.getProcessorChain().add(new AbstractUpdateProcessor<Object,IChemical>(OP.CREATE,updateQuery) {
				/**
			     * 
			     */
			    private static final long serialVersionUID = 9019409150445247686L;

				@Override
				protected IChemical execute(Object group,
						IQueryUpdate<Object, IChemical> query)
						throws SQLException, OperationNotSupportedException,
						AmbitException {
					if (group instanceof IChemical)
						query.setObject((IChemical)group);
					return super.execute(group, query);
				}
			});				
		} else {
			//add generators
			if (preprocessingOption.contains(FPTable.smarts_accelerator)) {
				query= new MissingFingerprintsQuery(FPTable.smarts_accelerator);
				batch.getProcessorChain().add(new SMARTSPropertiesGenerator());
			}
			if (preprocessingOption.contains(FPTable.fp1024)) {
				query =  new FingerprintsByStatus(FPTable.fp1024);
				//updateQuery = new CreateFingerprintChemical(FPTable.fp1024);
				batch.getProcessorChain().add(new BitSetGenerator(FPTable.fp1024));
			}
			if (preprocessingOption.contains(FPTable.sk1024)) {
				query =  new FingerprintsByStatus(FPTable.sk1024);
				batch.getProcessorChain().add(new BitSetGenerator(FPTable.sk1024));
			}
			
			//add writers
			if (preprocessingOption.contains(FPTable.smarts_accelerator)) {
				batch.getProcessorChain().add(new SMARTSAcceleratorWriter());					
			}
			if (preprocessingOption.contains(FPTable.fp1024)) {
				batch.getProcessorChain().add(new FP1024Writer(FPTable.fp1024));		
			}
			if (preprocessingOption.contains(FPTable.sk1024)) {
				batch.getProcessorChain().add(new FP1024Writer(FPTable.sk1024));			
			}
			
			/*
			if (preprocessingOption.contains(FPTable.pc1024)) {
				query =  new FingerprintsByStatus(FPTable.pc1024);
				batch.getProcessorChain().add(new BitSetGenerator(FPTable.pc1024));
				batch.getProcessorChain().add(new FP1024Writer(FPTable.pc1024));			
			}
			*/
		}
		

		batch.setHandlePrescreen(false);

		Connection c = null;
		DBConnection dbc = null;
		dbc = getConnection(options.getSQLConfig());
		c = dbc.getConnection();		
		c.setAutoCommit(false);
		batch.setCloseConnection(true);
		batch.setConnection(c);
		batch.open();
		
		IBatchStatistics stats = null;
		try {
			query.setPageSize(pagesize);
			logger.fine(query.getSQL());
			try {disableIndices(batch.getConnection());} catch (Exception x) { logger.warning(x.getMessage());}
			logger.info(String.format("Query submitted [pagesize %d]",pagesize));
			stats = batch.process(query);
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		} finally {
			try {batch.getConnection().commit();} catch (Exception x) { logger.warning(x.getMessage());}
			
			try {enableIndices(batch.getConnection());} catch (Exception x) { logger.warning(x.getMessage());}
			
			try {if (batch!=null) batch.close();} catch (Exception x) { logger.warning(x.getMessage());}
			if (stats!=null) 
				logger.log(Level.INFO,stats.toString());
		}

		    	
    }
}
