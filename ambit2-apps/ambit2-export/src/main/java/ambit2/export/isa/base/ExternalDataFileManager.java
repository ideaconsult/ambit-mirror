package ambit2.export.isa.base;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import ambit2.export.isa.base.ISAConst.DataFileFormat;

public class ExternalDataFileManager 
{	
	public static enum StorageMode {
		DIRECT_FILE_STORAGE, STRING_BUFFER, ARRAY_BUFFER
	}
	
	protected DataFileFormat dataFileFormat = DataFileFormat.TEXT_TAB;
	protected File outputDir = null;
	protected ExternalDataFileHeader fileHeader = null;
	protected StorageMode FlagStorageMode = StorageMode.DIRECT_FILE_STORAGE;
	
	protected List<List<Object>> array_buffer = new ArrayList<List<Object>>();
	protected StringBuffer buffer = null;
	protected FileWriter fileWriter = null;
	
	protected int currentRecordNum = 1;
	protected List<Object> currentRecord = new ArrayList<Object>();
	
	public ExternalDataFileManager (File outputDir) throws Exception
	{		
		this.outputDir = outputDir;
		init();
	}	
	
	void init () throws Exception
	{
		currentRecord.clear();
		if (outputDir != null)
		{
			fileWriter = createWriter(outputDir);
			//TODO set file header
		}
	}
	
	
	
	public void close() throws Exception
	{
		finalizeRecord();
		if (fileWriter != null)
			closeWriter(fileWriter);
	}
	
	public ExternalDataFileLocation storeData(Object obj)
	{
		//TODO
		return null;
	}
	
	public void finalizeRecord()
	{
		finalizeRecord(false);
	}
	
	public void finalizeRecord(boolean FinalizeIfEmpty)
	{
		if (currentRecord.isEmpty())
			if (!FinalizeIfEmpty)
				return;
		
		switch (FlagStorageMode)
		{
		case DIRECT_FILE_STORAGE:
			break;
			
		case STRING_BUFFER:
			break;
		
		case ARRAY_BUFFER:
			break;
			
			//TODO
		}
		
		if (fileWriter != null)
		{
			//TODO
		}
		
		currentRecord.clear();
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
