package ambit2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.log.AmbitLogger;

public abstract class RawIteratingReader<T> extends DefaultIteratingChemObjectReader implements IRawReader<T>{
	protected static AmbitLogger logger = new AmbitLogger(RawIteratingReader.class);
	private BufferedReader input;
	protected StringBuffer recordBuffer = null;
	protected LiteratureEntry reference;
	public LiteratureEntry getReference() {
		return reference;
	}
	public void setReference(LiteratureEntry reference) {
		this.reference = reference;
	}
	public RawIteratingReader(Reader in) throws CDKException {
		super();
		setReader(in);
		
	}
	public void setReader(InputStream in) throws CDKException {
		setReader(new InputStreamReader(in));
		
	}
	public void setReader(Reader in) throws CDKException {
		this.input = new BufferedReader(in);
		
	}
	public abstract IResourceFormat getFormat() ;

	public void close() throws IOException {
		if (input != null)
			input.close();

	}

	public boolean hasNext() {
		recordBuffer = null;
		try {
			while (input.ready()) {
				if (recordBuffer==null)
					recordBuffer = new StringBuffer();
				String line = input.readLine();
				recordBuffer.append(line);
				recordBuffer.append('\n');
				if (isEndOfRecord(line))
					return true;
			}
			//return recordBuffer!=null;
			return acceptLastRecord();
		} catch (Exception x) {
			logger.error(x);
		}
		return false;
	}
	protected boolean acceptLastRecord() {
		return false;
	}
	public Object next() {
		return recordBuffer.toString();
	}
    public abstract T nextRecord();
	public abstract boolean isEndOfRecord(String line); 

}	
