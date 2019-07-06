package ambit2.groupcontribution.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import ambit2.groupcontribution.dataset.LocalProperties.Type;

public class LocalPropertiesIterator implements Iterator<LocalProperties> 
{
	protected File file = null;
	protected Type locPropType = null;
	protected CSVParser parser = null;
	protected Iterator<CSVRecord> iterator = null;
	protected CSVFormat format = null;
	protected LocalProperties curLocalProperties = null;
	protected CSVRecord curCSVRecord = null;
	protected String curMolId = null;
	
	protected String propertyNames[] = null;
	protected int numOfProperties = 1;
	protected int molIdColIndex = 0;
	protected int atomColIndices[] = null;
	protected int propertyColIndices[] = null;
	
	public LocalPropertiesIterator(File file) throws Exception
	{
		this.file = file;
		this.locPropType = Type.ATOM; 
		format = CSVFormat.DEFAULT.withDelimiter(',').withQuote('\"');
		init();
	}
	
	public LocalPropertiesIterator(File file, Type locPropType) throws Exception
	{
		this.file = file;
		this.locPropType = locPropType; 
		format = CSVFormat.DEFAULT.withDelimiter(',').withQuote('\"');
		init();
	}
	
	public LocalPropertiesIterator(File file, CSVFormat format, Type locPropType) throws Exception
	{
		this.file = file;
		this.locPropType = locPropType;
		this.format = format;
		init();
	}
	
	protected void init() throws Exception
	{	
		InputStream in = new FileInputStream(file);
		Reader reader = new InputStreamReader(in);
		parser = format.withHeader().parse(reader);
		iterator = parser.iterator();
		setDefaultColumnIndices();
	}
	
	public String[] getPropertyNames() {
		return propertyNames;		
	}
	
	public Type getLocalPropertyType() {
		return locPropType;
	}
	
	protected void setDefaultColumnIndices()
	{
		int nAtomColumns = 1;
		switch (locPropType){
		case BOND:
		case ATOM_PAIR:
			nAtomColumns = 2;
		}

		atomColIndices = new int[nAtomColumns];
		for (int i = 0; i < nAtomColumns; i++)
			atomColIndices[i] = i+1;
		
		int propFirstIndex = nAtomColumns + 1;
		propertyColIndices = new int[1];
		propertyColIndices[0] = propFirstIndex;
	}
	
	@Override
	public boolean hasNext() {
		curLocalProperties = null;
		return iterator.hasNext();
	}

	@Override
	public LocalProperties next() {
		//Start new local properties reading sequence of rows
		curLocalProperties = null;
		
		if (curCSVRecord == null)
		{
			//This is the first line after header
			if (iterator.hasNext())
				curCSVRecord = iterator.next();
		}
		
		
		if (curCSVRecord != null)
		{	
			curMolId = curCSVRecord.get(molIdColIndex);
			Map.Entry<Object, double[]> entry = getPropertyEntry(curCSVRecord);
			curLocalProperties = new LocalProperties();
			curLocalProperties.properties.put(entry.getKey(), entry.getValue());
		}
		else
			return null;
		
		//Reading all local properties for current molecule
		while (iterator.hasNext())
		{	
			curCSVRecord = iterator.next();
			String molId0 = curCSVRecord.get(molIdColIndex);
			if (!molId0.equals(curMolId))
				break;
			Map.Entry<Object, double[]> entry = getPropertyEntry(curCSVRecord);			
			curLocalProperties.properties.put(entry.getKey(), entry.getValue());
		}
		
		return curLocalProperties;
	}
	
	Map.Entry<Object, double[]> getPropertyEntry(CSVRecord csvRec)
	{
		//TODO
		return null;
	}
	
	public void close() throws Exception
	{
		if (parser != null)
			parser.close();
	}
	
}
