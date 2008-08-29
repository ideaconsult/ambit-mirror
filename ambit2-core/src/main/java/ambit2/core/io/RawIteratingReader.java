package ambit2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.core.log.AmbitLogger;

public abstract class RawIteratingReader<T> extends DefaultIteratingChemObjectReader {
	protected static AmbitLogger logger = new AmbitLogger(RawIteratingReader.class);
	private BufferedReader input;
	protected StringBuffer recordBuffer = null;
	public RawIteratingReader(Reader in) {
		super();
		this.input = new BufferedReader(in);
	}
	public abstract IResourceFormat getFormat() ;

	public void close() throws IOException {
		if (input != null)
			input.close();

	}

	public boolean hasNext() {
		recordBuffer = new StringBuffer();
		try {
			while (input.ready()) {
				String line = input.readLine();
				recordBuffer.append(line);
				recordBuffer.append('\n');
				if (isEndOfRecord(line))
					return true;
			}
		} catch (Exception x) {
			logger.error(x);
		}
		return false;
	}

	public Object next() {
		return recordBuffer.toString();
	}
    public abstract T nextRecord();
	public abstract boolean isEndOfRecord(String line); 

}	
