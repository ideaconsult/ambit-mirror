package ambit2.groupcontribution.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

public class DataSetObjectIterator implements Iterator<DataSetObject> 
{
	File dataFile = null;
	File localPropertiesFile = null;
	int targetLocalPropertyIndex = 0;
	
	IIteratingChemObjectReader<IAtomContainer> molReader = null;
	LocalPropertiesIterator locPropIterator = null;
	
	public DataSetObjectIterator(File dataFile, File localPropertiesFile, int targetLocalPropertyIndex) throws Exception
	{
		this(dataFile, localPropertiesFile);
		this.targetLocalPropertyIndex = targetLocalPropertyIndex; 
	}
	
	public DataSetObjectIterator(File dataFile, File localPropertiesFile) throws Exception
	{
		this.dataFile = dataFile;
		this.localPropertiesFile = localPropertiesFile;
		init();
	}
	
	protected void init() throws Exception
	{
		//Setup molReader
		if (!dataFile.exists()) 
			throw new FileNotFoundException(dataFile.getAbsolutePath());
		
		InputStream in = new FileInputStream(dataFile);
		molReader = DataSet.getReader(in,dataFile.getName());
		
		if (localPropertiesFile == null)
			return; //local properties are not handled
		
		//Setup local properties iterator
		if (!localPropertiesFile.exists()) 
			throw new FileNotFoundException(localPropertiesFile.getAbsolutePath());
		
		locPropIterator = new LocalPropertiesIterator(localPropertiesFile);
	}
	
	
	@Override
	public boolean hasNext() {
		boolean molReaderHasNext = molReader.hasNext();
		if (molReaderHasNext)
		{
			if (locPropIterator == null)
				return true;
			else
				return locPropIterator.hasNext(); 
		}
		else
			return false;
	} 

	@Override
	public DataSetObject next() 
	{
		IAtomContainer molecule  = molReader.next();
		DataSetObject dso = new DataSetObject();		
		
		if (molecule==null) {
			dso.molecule = null;
			dso.error = "Unable to read chemical object";			
		}
		else
		{
			if (molecule.getAtomCount() == 0)
			{	
				dso.molecule = null;
				dso.error = "Empty chemical object. Problem on object creation.";
			}
			else
			{	
				try {
					DataSet.processMolecule(molecule);
				}
				catch (Exception e) {}
				dso.molecule = molecule;				
			}
		}
		
		if ((dso.error == null) && (locPropIterator != null))
		{
			LocalProperties locProp = locPropIterator.next();
			if (locProp == null)
				dso.error = "Unable to read local properties";
			else
			{
				dso.externalLocalProperties = locProp.properties; //all properties are taken as external
				//dso.targetLocalProperty = new HashMap<Object, Double>();
				//TODO
			}
		}
			
		
		return dso;
	}
	
	public void close()
	{
		try {
			molReader.close(); 
		} catch (Exception x) {}
	}

}
