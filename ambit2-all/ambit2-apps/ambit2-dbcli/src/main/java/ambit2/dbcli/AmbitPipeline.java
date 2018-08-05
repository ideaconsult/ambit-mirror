package ambit2.dbcli;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.FileOutputState;
import ambit2.core.io.FilesWithHeaderWriter;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.processors.structure.InchiProcessor;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.dbcli.processor.StdzBatchProcessor;
import ambit2.smarts.processors.SMIRKSProcessor;
import ambit2.tautomers.processor.StructureStandardizer;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.batch.IBatchStatistics.RECORDS_STATS;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

public abstract class AmbitPipeline {
	static Logger logger_cli = Logger.getLogger("ambitcli", "ambit2.dbcli.msg");
	static final String loggingProperties = "ambit2/dbcli/logging.properties";
	static final String log4jProperties = "ambit2/dbcli/log4j.properties";
	
	CliOptions options;
	public AmbitPipeline(CliOptions options) {
		this.options = options;
	}
	
	
	public abstract long go(String command, String subcommand) throws Exception ;


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

	protected String parseDelimiterParam() {
		return parseStringParam(":delimiter", ",");
	}

	protected boolean parseSparseParam() {
		return parseBooleanParam(":sparse", true);
	}

	protected boolean parseBitsetParam() {
		return parseBooleanParam(":bitset", true);
	}

	protected float parseThresholdParam() {
		try {
			Object n = options.getParam(":threshold");
			return ((Double) n).floatValue();
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.toString());
			return 0.75f;
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
			return Boolean.parseBoolean(options.getParam(":write_count").toString());
		} catch (Exception x) {
			return false;
		}
	}

	protected boolean parseWriteRawParam() {
		try {
			return Boolean.parseBoolean(options.getParam(":write_raw").toString());
		} catch (Exception x) {
			return false;
		}
	}

	protected File getInputFile() throws Exception {
		if (options.input == null)
			throw new FileNotFoundException("Input file not specified! Please use -i {file}");
		return new File(options.input);
	}

	protected String[] parsetags_to_keep() {
		String[] fields_to_keep = null;
		try {
			Object tag_to_keep = options.getParam(":tag_tokeep");
			if (tag_to_keep != null && !"".equals(tag_to_keep.toString().trim()))
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

	public void parseCommandStandardize(String subcommand, long now) throws Exception {
		int page = parsePageParam();
		int pagesize = parsePageSizeParam();
		Object tmpTag = parseSdfTitleParam();

		String smiles_header = parseInputTag_Param("smiles", IteratingDelimitedFileReader.defaultSMILESHeader);
		String inchi_header = parseInputTag_Param("inchi", "InChI");
		String inchikey_header = parseInputTag_Param("inchikey", "InChIKey");

		final String sdf_title = tmpTag == null ? null : tmpTag.toString().toLowerCase();

		final StructureStandardizer standardprocessor = new StructureStandardizer(logger_cli);
		standardprocessor.setGenerate2D(parseBooleanParam(":generate2D", false));
		standardprocessor.setGenerateTautomers(parseBooleanParam(":tautomers", false));

		SMIRKSProcessor tmp = null;
		try {
			Object o = options.getParam(":smirks");
			if (o != null) {
				File smirksConfig = new File(o.toString());
				if (smirksConfig.exists()) {
					tmp = new SMIRKSProcessor(smirksConfig, logger_cli);
					tmp.setEnabled(true);
				} else
					logger_cli.log(Level.WARNING, "SMIRKS transformation file not found");
			}
		} catch (Exception x) {
			logger_cli.log(Level.SEVERE, x.getMessage());
			tmp = null;
		}
		final SMIRKSProcessor smirksProcessor = tmp;

		standardprocessor.setSplitFragments(parseBooleanParam(":splitfragments", false));
		standardprocessor.setImplicitHydrogens(parseBooleanParam(":implicith", false));
		standardprocessor.setNeutralise(parseBooleanParam(":neutralise", false));

		final String[] tags_to_keep = parsetags_to_keep();

		standardprocessor.setRankTag(parseStringParam(":tag_rank", "RANK"));
		standardprocessor.setInchiTag(parseStringParam(":tag_inchi", "InChI"));
		standardprocessor.setInchiKeyTag(parseStringParam(":tag_inchikey", "InChIKey"));
		standardprocessor.setSMILESTag(parseStringParam(":tag_smiles", "SMILES"));

		standardprocessor.setGenerateInChI(parseBooleanParam(":inchi", true));
		standardprocessor.setGenerateSMILES(parseBooleanParam(":smiles", true));
		standardprocessor.setGenerateSMILES_Canonical(parseBooleanParam(":smilescanonical", false));
		standardprocessor.setGenerateSMILES_Aromatic(parseBooleanParam(":smilesaromatic", false));
		standardprocessor.setGenerateStereofrom2D(parseBooleanParam(":generatestereofrom2d", false));
		standardprocessor.setClearIsotopes(parseBooleanParam(":setClearIsotopes", false));

		final boolean debugatomtypes = parseBooleanParam(":debugatomtypes", false);
		final int startRecord = pagesize > 0 ? (page * pagesize + 1) : 1;
		final int maxRecord = pagesize > 0 ? ((page + 1) * pagesize + 1) : pagesize;

		final File file = getInputFile();
		FileInputState in = new FileInputState(file);
		in.setOptionalInChIHeader(inchi_header);
		in.setOptionalInChIKeyHeader(inchikey_header);
		in.setOptionalSMILESHeader(smiles_header);

		if (options.output == null)
			throw new FileNotFoundException("Output file not specified. Please use -o {file}");
		final File outfile = new File(options.output);

		logger_cli.log(Level.INFO, "MSG_INFO_READINGWRITING",
				new Object[] { file.getAbsoluteFile(), outfile.getAbsolutePath() });
		FileOutputState out = new FileOutputState(outfile);
		final IChemObjectWriter writer = out.getWriter();
		if (writer instanceof FilesWithHeaderWriter)
			((FilesWithHeaderWriter) writer).setAddSMILEScolumn(false);
		
		final BatchDBProcessor<IStructureRecord> batch = new BatchDBProcessor<IStructureRecord>() {

			@Override
			public void onItemRead(IStructureRecord input, IBatchStatistics stats) {
				super.onItemRead(input, stats);
				if ((maxRecord > 0) && stats.getRecords(RECORDS_STATS.RECORDS_READ) >= (maxRecord))
					cancel();
			};

			@Override
			public boolean skip(IStructureRecord input, IBatchStatistics stats) {
				return (stats.getRecords(RECORDS_STATS.RECORDS_READ) < startRecord)
						|| ((maxRecord > 0) && (stats.getRecords(RECORDS_STATS.RECORDS_READ) >= maxRecord));
			}

			@Override
			public void onItemSkipped(IStructureRecord input, IBatchStatistics stats) {
				super.onItemSkipped(input, stats);
				if (stats.isTimeToPrint(getSilentInterval() * 2))
					propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS, null, stats);
			}

			@Override
			public void onItemProcessing(IStructureRecord input, Object output, IBatchStatistics stats) {
			}

			@Override
			public void onError(IStructureRecord input, Object output, IBatchStatistics stats, Exception x) {
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
		batch.getProcessorChain().add(new StdzBatchProcessor(standardprocessor, smirksProcessor, tags_to_keep,logger_cli,writer,sdf_title,debugatomtypes));
		batch.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (AbstractBatchProcessor.PROPERTY_BATCHSTATS.equals(evt.getPropertyName()))
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
			logger_cli.log(Level.WARNING, "MSG_ERR", new Object[] { x.getMessage() });
			logger_cli.log(Level.FINE, "MSG_ERR_DEBUG", new Object[] { x.getMessage(), w.toString() });
		} finally {
			try {
				if (batch != null)
					batch.close();
			} catch (Exception x) {
				logger_cli.log(Level.WARNING, "MSG_ERR", new Object[] { x.getMessage() });
			}
			if (stats != null)
				logger_cli.log(Level.INFO, "MSG_INFO", new Object[] { stats.toString() });
		}
	}
	
	protected static void inchi_warmup(int retry) throws Exception {
		Exception err = null;
		long now = System.currentTimeMillis();
		for (int i = 0; i < retry; i++)
			try {
				InchiProcessor p = new InchiProcessor();
				p.process(MoleculeFactory.makeCyclohexane());
				p.close();
				p = null;

				logger_cli.log(Level.INFO, "MSG_INCHI",
						new Object[] { String.format("loaded in %d msec", System.currentTimeMillis() - now), "" });
				return;
			} catch (Exception x) {
				logger_cli.log(Level.WARNING, "MSG_INCHI", new Object[] { "load failed at retry ", (retry + 1) });
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
