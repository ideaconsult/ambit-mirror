package ambit2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.interfaces.ICiteable;

public abstract class RawIteratingReader<T> extends DefaultIteratingChemObjectReader implements IRawReader<T>, ICiteable {
	protected static Logger logger = Logger.getLogger(RawIteratingReader.class.getName());
	protected BufferedReader input;
	protected StringBuilder recordBuffer = null;
	protected ILiteratureEntry reference;
	
	public ILiteratureEntry getReference() {
		return reference;
	}

	public void setReference(ILiteratureEntry reference) {
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
			String line;
			while ((line=input.readLine()) != null) {
				if (recordBuffer==null)
					recordBuffer = new StringBuilder();
				recordBuffer.append(line);
				recordBuffer.append('\n');
				if (isEndOfRecord(line))
					return true;
			}
			//return recordBuffer!=null;
			return acceptLastRecord();
		} catch (Exception x) {
			logger.log(Level.SEVERE,x.getMessage(),x);
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
