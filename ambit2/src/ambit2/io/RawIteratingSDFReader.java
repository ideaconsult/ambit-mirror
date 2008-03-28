package ambit2.io;

import java.io.Reader;

public class RawIteratingSDFReader extends RawIteratingReader {
	public static final String delimiter = "$$$$"; 
	public RawIteratingSDFReader(Reader in) {
		super(in);
	}

	@Override
	public boolean isEndOfRecord(String line) {
		return line.contains(delimiter);
	}

}
