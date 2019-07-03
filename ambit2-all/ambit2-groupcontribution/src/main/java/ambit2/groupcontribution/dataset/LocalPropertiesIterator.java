package ambit2.groupcontribution.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class LocalPropertiesIterator implements Iterator<LocalProperties> 
{
	protected File file = null;
	protected CSVParser parser = null;
	protected Iterator<CSVRecord> iterator = null;
	protected CSVFormat format = null;
	
	public LocalPropertiesIterator(File file) throws Exception
	{
		this.file = file;
		format = CSVFormat.DEFAULT.withDelimiter(',').withQuote('\"');
		init();
	}
	
	public LocalPropertiesIterator(File file, CSVFormat format) throws Exception
	{
		this.file = file;
		this.format = format;
		init();
	}
	
	protected void init() throws Exception
	{	
		InputStream in = new FileInputStream(file);
		Reader reader = new InputStreamReader(in);
		parser = format.withHeader().parse(reader);
		iterator = parser.iterator();
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LocalProperties next() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void close() throws Exception
	{
		if (parser != null)
			parser.close();
	}
	
}
