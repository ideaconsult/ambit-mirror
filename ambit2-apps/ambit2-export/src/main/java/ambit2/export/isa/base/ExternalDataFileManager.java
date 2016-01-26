package ambit2.export.isa.base;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import ambit2.export.isa.base.ISAConst.DataFileFormat;

public class ExternalDataFileManager 
{
	
	protected DataFileFormat dataFileFormat = DataFileFormat.TEXT_TAB;
	protected File outputDir = null;
	
	protected boolean FlagStoreDataInStringBuffer = false; 
	protected StringBuffer buffer = null;
	
	List<String> currentLine = new ArrayList<String>();
	
	
	public ExternalDataFileManager (File outputDir)
	{
		init();
	}
	
	public void saveBufferAsFile() throws Exception
	{
		if (outputDir == null)
			throw new Exception("OutputDir/File is null!");
	}
	
	void init ()
	{
		currentLine.clear();
	}
	
	public ExternalDataFileLocation storeData(Object obj)
	{
		//TODO
		return null;
	}
	
	void addLine()
	{
		//TODO
	}
	
	void addToLine(Object obj)
	{
		//TODO
	}
	
	public String getDataStringBuffer()
	{
		if (buffer != null)
			return buffer.toString();
		return null;
	}
	
	public DataFileFormat getDataFileFormat() {
		return dataFileFormat;
	}

	public void setDataFileFormat(DataFileFormat dataFileFormat) {
		this.dataFileFormat = dataFileFormat;
	}

}
