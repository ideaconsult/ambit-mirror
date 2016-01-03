package ambit2.ml.io;

import org.apache.commons.csv.CSVRecord;

public interface IMLOutput<V> {
	void open() throws Exception;

	void print(CSVRecord value,int columnid) throws Exception;

	void close() throws Exception;

	void setEnabled(boolean enabled);

	V transform(CSVRecord record,int columnid);
}
