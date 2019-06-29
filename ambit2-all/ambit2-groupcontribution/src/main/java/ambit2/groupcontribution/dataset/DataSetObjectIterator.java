package ambit2.groupcontribution.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

public class DataSetObjectIterator implements Iterator<DataSetObject> 
{
	File dataFile = null;
	File localPropertiesFile = null;
	
	IIteratingChemObjectReader<IAtomContainer> molReader = null;
	
	public DataSetObjectIterator(File dataFile, File localPropertiesFile) throws Exception
	{
		this.dataFile = dataFile;
		this.localPropertiesFile = localPropertiesFile;
		init();
	}
	
	protected void init() throws Exception
	{
		if (!dataFile.exists()) 
			throw new FileNotFoundException(dataFile.getAbsolutePath());
		
		//Setup molReader
		InputStream in = new FileInputStream(dataFile);
		molReader = DataSet.getReader(in,dataFile.getName());
	}
	
	
	@Override
	public boolean hasNext() {
		// TODO 
		return false;
	}

	@Override
	public DataSetObject next() {
		// TODO 
		return null;
	}
	
	public void close()
	{
		try {
			molReader.close(); 
		} catch (Exception x) {}
	}

}
