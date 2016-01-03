package ambit2.ml.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.commons.csv.CSVRecord;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.db.reporters.ARFFReporter;

public class OutputARFF  implements IMLOutput<SubstanceRecord> {
	protected ARFFReporter arffWriter;
	protected BufferedWriter arff;
	protected boolean enabled = false;
	protected File file_arff;
	protected Template header;
	protected SubstanceRecord item = new SubstanceRecord();
	Property[] p = new Property[1026];

	public OutputARFF(String folder, String file, boolean enabled)
			throws Exception {
		this.file_arff = new File(folder, file + ".arff");
		this.header = new Template();
		setEnabled(enabled);
		Property key;
		for (int i = 0; i < p.length; i++) {
			key = Property.getInstance(Integer.toString(i), "file");
			key.setNominal(false);
			key.setClazz(Number.class);
			key.setAllowedValues(null);
			key.setId(i + 1);
			header.add(key);
			item.setRecordProperty(key, 0);
			p[i] = key;
		}
	}

	@Override
	public void open() throws Exception {
		if (enabled) {
			arff = new BufferedWriter(new FileWriter(file_arff));
			arffWriter = new ARFFReporter(header);
			arffWriter.setOutput(arff);
			arffWriter.header(arff, null);
		}
	}

	@Override
	public SubstanceRecord transform(CSVRecord record,int columnid) {
		for (int j = 0; j < p.length; j++)
			item.setRecordProperty(p[j], 0);
		item.setSubstanceUUID(record.get(columnid));

		for (int c = 0; c < record.size(); c++) {
			if (c==columnid) continue;
			String value = record.get(c);

			int x = Integer.parseInt(value);
			try {
				Property key = p[x];
				item.setRecordProperty(key, 1);
			} catch (Exception xx) {
				xx.printStackTrace();
			}
		}
		return item;

	}

	@Override
	public void print(CSVRecord record,int columnid) throws Exception {
		if (enabled) {
			arffWriter.processItem(transform(record,columnid));
			arff.flush();
		}
	}

	@Override
	public void close() throws Exception {
		if (enabled) {
			if (arffWriter != null) {
				arffWriter.footer(arff, null);
				arff.flush();
				arff.close();
				arffWriter.close();

			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}