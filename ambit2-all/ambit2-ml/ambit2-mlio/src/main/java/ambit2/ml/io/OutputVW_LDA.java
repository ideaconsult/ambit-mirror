package ambit2.ml.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.commons.csv.CSVRecord;

import ambit2.base.data.SubstanceRecord;

public class OutputVW_LDA  implements IMLOutput<SubstanceRecord> {
	protected boolean enabled = false;
	BufferedWriter writer;
	protected File file;

	public OutputVW_LDA(String folder, String file, boolean enabled) {
		this.file = new File(folder,file + "_lda.vw");
		setEnabled(enabled);
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void open() throws Exception {
		if (enabled) {
			writer = new BufferedWriter(new FileWriter(file));
		}
	}

	/**
	 * <pre>
	 * | word_id:word_ct word_id:word_ct word_id:word_ct word_id:word_ct
	 * </pre>
	 */
	@Override
	public void print(CSVRecord record,int columnid) throws Exception {
		if (enabled) {
			writer.write("|");
			for (int c = 0; c < record.size(); c++) {
				if (c==columnid) continue;
				String value = record.get(c);
				writer.write(" ");
				writer.write(value);
			}
			writer.write("\n");
		}
	}

	@Override
	public void close() throws Exception {
		if (enabled) {
			if (writer != null)
				writer.close();
		}
	}

	@Override
	public SubstanceRecord transform(CSVRecord record,int columnid) {
		return null;
	}

}