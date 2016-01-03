package ambit2.ml.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class  OutputVW implements IMLOutput<CSVRecord> {
	CSVPrinter idWriter;
	CSVPrinter featureWriter;
	BufferedWriter vw;
	protected String folder;
	protected String file;
	Object[] id_record = new Object[] { null, null };
	Object[] feature_record = new Object[] { null, null };
	Map<String, Integer> columns = new Hashtable<String, Integer>();

	public OutputVW(String folder, String file, boolean enabled) {
		this.folder = folder;
		this.file = file;
		setEnabled(enabled);
	}

	@Override
	public CSVRecord transform(CSVRecord record,int columnid) {
		return record;
	}

	@Override
	public void open() throws Exception {
		if (enabled) {
			idWriter = new CSVPrinter(new PrintWriter(new File(folder,"id.csv")),
					CSVFormat.DEFAULT);
			idWriter.printRecord(new Object[] { "ID", "NO" });
			featureWriter = new CSVPrinter(new PrintWriter(new File(folder, "feature.csv")), CSVFormat.DEFAULT);
			featureWriter.printRecord(new Object[] { "ID", "NO" });
			vw = new BufferedWriter(new FileWriter(new File(folder,file + ".vw")));
		}
	}

	@Override
	public void print(CSVRecord record,int columnid) throws Exception {
		if (enabled) {
			id_record[0] = record.get(columnid);
			id_record[1] = record.getRecordNumber();
			idWriter.printRecord(id_record);

			for (int c = 0; c < record.size(); c++) {
				if (c==columnid) continue;
				String value = record.get(c);
				Integer index = columns.get(value);
				if (index == null) {
					index = columns.size() + 1;
					columns.put(value, index);
					vw.flush();
				}
				vw.write(String.format("1 |c %s |f %s\n", record.get(0), value));

			}
		}
	}

	@Override
	public void close() throws Exception {
		if (enabled) {
			Set<Entry<String, Integer>> entries = columns.entrySet();
			for (Entry<String, Integer> entry : entries) {
				feature_record[0] = entry.getKey();
				feature_record[1] = entry.getValue();
				featureWriter.printRecord(feature_record);
			}
			if (vw != null) {
				vw.close();
			}
			if (idWriter != null)
				idWriter.close();
			if (featureWriter != null)
				featureWriter.close();
		}
	}

	protected boolean enabled = false;

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

	}
}