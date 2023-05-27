package ambit2.dbsubstance;

import java.io.File;
import java.io.InputStream;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.mysql.jdbc.CommunicationsException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.lookup.DictionaryConfig;
import ambit2.base.data.lookup.SubstanceStudyValidator;
import ambit2.base.data.study.StructureRecordValidator;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.db.substance.processor.DBSubstanceWriter;
import net.enanomapper.expand.SubstanceRecordMapper;
import net.idea.modbcum.i.processors.IProcessor;

public class SubstanceImport extends DBSubstanceImport {
	

	protected DictionaryConfig config;
	
	public SubstanceImport(DictionaryConfig dict) {
		super();
		if (dict == null)
			config = new DictionaryConfig();
		else config = dict;
	}
	public SubstanceImport() {
		this(new DictionaryConfig());
	}
	

	@Override
	protected StructureRecordValidator createValidator(boolean xlsx) {
		boolean addcomposition = isAddDefaultComposition();
		SubstanceStudyValidator v =  new SubstanceStudyValidator(config,inputFile.getName(), true, getPrefix()) {
			public boolean isAddDefaultComposition() {
				return addcomposition;
			}
			@Override
			public IStructureRecord validate(SubstanceRecord record) throws Exception {
				record = StructureRecordValidator.basic_validation(record, inputFile.getName(), xlsx, getUpdated());
				return super.validate(record);
			}
		};
		return v;
	}
	@Override
	protected IRawReader<IStructureRecord> createParser(InputStream stream, boolean xlsx) throws Exception {
		_parsertype mode = getParserType();
		InputStream in = null;
		if (gzipped)
			in = new GZIPInputStream(stream);
		else
			in = stream;
		if (mode == null)
			throw new Exception("Unsupported parser type " + mode);

		return super.createParser(in, xlsx);

	}

	@Override
	protected void configure(DBSubstanceWriter writer, SubstanceRecord record) {
		_parsertype mode = getParserType();
		if ("coseu".equals(mode.name())) {
			if (isSplitRecord()) {
				logger_cli.log(Level.WARNING, record.getSubstanceName());

				writer.setImportedRecord(record);
			}
		}

	}




	
	@Override
	protected Option createParserTypeOption() {
		return OptionBuilder.hasArg().withLongOpt("parser").withArgName("type")
				.withDescription("File parser mode : xls|xlsx|modnanotox|nanowiki").create("p");
	}

	@Override
	protected IProcessor<IStructureRecord, IStructureRecord> createMapper(boolean xlsx) {
		try {
			File expandMap = getExpandMap();
			
			return expandMap==null?null:new SubstanceRecordMapper(getPrefix(), getExpandMap());
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		long now = System.currentTimeMillis();
		try {
			SubstanceImport object = new SubstanceImport();
			if (object.parse(args)) {
				object.importFile();
			} else
				System.exit(0);
		} catch (ConnectException x) {
			logger_cli.log(Level.SEVERE, "Connection refused. Please verify if the remote server is responding.");
			System.exit(-1);
		} catch (CommunicationsException x) {
			logger_cli.log(Level.SEVERE, "Can't connect to MySQL server!");
			System.exit(-1);
		} catch (SQLException x) {
			logger_cli.log(Level.SEVERE, "SQL error", x);
			System.exit(-1);
		} catch (Exception x) {

			logger_cli.log(Level.WARNING, x.getMessage(), x);
			System.exit(-1);
		} finally {
			logger_cli.info(String.format("Completed in %s msec", (System.currentTimeMillis() - now)));
			logger_cli.info("Done.");
		}
	}
}
