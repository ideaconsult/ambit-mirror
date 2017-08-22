package ambit2.groupcontribution.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataSet 
{
	public List<DataSetObject> dataObjects = new ArrayList<DataSetObject>();
	File datafile = null;
	
	
	public DataSet(File f) throws Exception
	{
		datafile = f;
		loadDataFromFile(datafile);
		
	}
	
	public void loadDataFromFile(File file) throws Exception
	{
		//TODO use basic Ambit io utils
	}
	
	
	
}
