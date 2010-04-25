package ambit2.core.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.openscience.cdk.exception.CDKException;

/**
 * ToxCast assay data are TXT tab delimited files.<br>
 * Lines starting with # are comments.<br>
 * The column SOURCE_NAME_SID contains DSSTOX_RID (DSSTOX record ID) , prependend with "DSSTOX_" string, 
 * e.g. DSSTOX_40307
 * @author nina
 *
 */
public class ToxcastAssayReader extends IteratingDelimitedFileReader {
	protected final static String prefix = "DSSTOX_";
	protected final static String  DSSTox_RID = "DSSTox_RID";
	protected final static String ToxCast_signature = "# EPA ToxCast Phase I Data";
	protected boolean isToxCastAssayFile = false;
	
	protected int DSSTOX_RID_index = -1;
	
	enum Assay_column {
		SOURCE_NAME_SID {
			@Override
			public String getDSSTOXName() {
				return DSSTox_RID;
			}
			@Override
			public String getValue(String value) {
				if (value.startsWith(prefix)) 
					return value.substring(7);
				else return value;
			}
		};
		public String getDSSTOXName() {
			return this.toString();
		}
		public String getValue(String value) {
			return value;
		}
	}
	
	public ToxcastAssayReader(InputStream in) throws UnsupportedEncodingException, CDKException {
		super(in, new DelimitedFileFormat("\t ",'"'));
	}	
	public ToxcastAssayReader(Reader in) throws CDKException {
		super(in, new DelimitedFileFormat("\t ",'"'));
	}

	@Override
	protected void processComment(String line) {
		if (line.startsWith(ToxCast_signature)) isToxCastAssayFile = true;
	}
	protected void addHeaderColumn(String name) {
		try {
			Assay_column column = Assay_column.valueOf(name);
			super.addHeaderColumn(column.getDSSTOXName());
		} catch (Exception x) {
			super.addHeaderColumn(name);
		}
	}
	@Override
	protected void processHeader(BufferedReader in) {
		super.processHeader(in);
		if (isToxCastAssayFile)
			for (int i=0; i < getNumberOfColumns(); i++)
				if (getHeaderColumn(i).toString().equals(DSSTox_RID)) {
					DSSTOX_RID_index = i; break;
				}		
	}
	
	@Override
	public void extractRowKeyAndData(String line) {
		super.extractRowKeyAndData(line);
		if (isToxCastAssayFile && (DSSTOX_RID_index>=0)) 
			values[DSSTOX_RID_index] = Assay_column.SOURCE_NAME_SID.getValue(values[DSSTOX_RID_index].toString());
	}
}
