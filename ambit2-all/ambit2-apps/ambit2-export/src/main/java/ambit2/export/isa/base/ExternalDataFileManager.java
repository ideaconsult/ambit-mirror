package ambit2.export.isa.base;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import ambit2.export.isa.base.ISAConst.DataFileFormat;

public class ExternalDataFileManager 
{
	
	protected DataFileFormat dataFileFormat = DataFileFormat.TEXT_TAB;
	protected File outputDir = null;
	
	protected boolean FlagStoreDataInStringBuffer = false; 
	protected StringBuffer buffer = null;
	FileWriter fileWriter = null;
	
	protected List<String> currentLine = new ArrayList<String>();
	
	
	public ExternalDataFileManager (File outputDir) throws Exception
	{
		init();
	}
	
	
	void init () throws Exception
	{
		currentLine.clear();
		if (outputDir != null)
		{
			//TODO
		}
		
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
	
	public void saveBufferAsFile(File file) throws Exception
	{
		if (file == null)
			throw new Exception("Target file is null!");
		
		FileWriter writer = createWriter(outputDir);
		//TODO
		
		closeWriter(writer);
	}
	
	
	protected FileWriter createWriter(File file) throws Exception
	{
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			
		}catch (Exception x) {
			//in case smth's wrong with the writer file, close it and throw an error
			try {writer.close(); } catch (Exception xx) {}
			throw x;
		} finally { }
		
		return writer;
	}
	
	public void closeWriter(FileWriter writer) throws Exception
	{
		writer.close();
	}

}
