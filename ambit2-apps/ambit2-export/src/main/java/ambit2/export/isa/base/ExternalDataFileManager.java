package ambit2.export.isa.base;

import java.io.File;

import ambit2.export.isa.base.ISAConst.DataFileFormat;

public class ExternalDataFileManager 
{
	
	protected DataFileFormat dataFileFormat = DataFileFormat.TEXT_TAB;
	protected File outputDir = null;
	
	protected boolean FlagStoreDataInStringBuffer = false; 
	protected StringBuffer buffer = null;
	
	
	public ExternalDataFileManager (File outputDir)
	{
		init();
	}
	
	void init ()
	{
		//TODO
	}
	
	public void addLine()
	{
		//TODO
	}
	
	public void addToLine(Object obj)
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
