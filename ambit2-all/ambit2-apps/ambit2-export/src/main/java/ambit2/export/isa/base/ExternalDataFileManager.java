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
	protected StorageMode storageMode = StorageMode.DIRECT_FILE_STORAGE;
	
	protected List<List<Object>> arrayBuffer = new ArrayList<List<Object>>();
	protected StringBuffer strBuffer = null;
	protected FileWriter fileWriter = null;
	protected String splitter = "\t";
	protected String endLine = System.getProperty("line.separator");
	
	protected int currentRecordNum = 1;
	protected List<Object> currentRecord = new ArrayList<Object>();
	
	public ExternalDataFileManager (File outputDir, DataFileFormat dataFileFormat) throws Exception
	{		
		this.outputDir = outputDir;
		this.dataFileFormat = dataFileFormat;
		init();
	}
	
	public ExternalDataFileManager (File outputDir, 
								DataFileFormat dataFileFormat, 
								ExternalDataFileHeader fileHeader) throws Exception
	{		
		this.outputDir = outputDir;
		this.dataFileFormat = dataFileFormat;
		this.fileHeader = fileHeader;
		init();
	}
		
	void init () throws Exception
	{
		currentRecord.clear();
		
		if (outputDir == null)
			throw new Exception("Output file/dir is null!");
		
		fileWriter = createWriter(outputDir);
		
		initFileHeader();
	}
	
	void initFileHeader() throws Exception {
		//TODO
	}
	
	public File getOutputDir() {
		return outputDir;
	}

	public ExternalDataFileHeader getFileHeader() {
		return fileHeader;
	}
	
	public void setFileHeader(ExternalDataFileHeader fileHeader) {
		this.fileHeader = fileHeader;
	}
	
	public DataFileFormat getDataFileFormat() {
		return dataFileFormat;
	}
	
	public StorageMode getStorageMode() {
		return storageMode;
	}

	public void setStorageMode(StorageMode storageMode) {
		this.storageMode = storageMode;
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
	
	public void finalizeRecord() throws Exception
	{
		finalizeRecord(false);
	}
	
	public void finalizeRecord(boolean FinalizeIfEmpty) throws Exception
	{
		if (currentRecord.isEmpty())
			if (!FinalizeIfEmpty)
				return;
		
		switch (storageMode)
		{
		case DIRECT_FILE_STORAGE:
			finalizeRecordInFile();
			break;
			
		case STRING_BUFFER:
			for (int i = 0; i < currentRecord.size(); i++)
			{	
				strBuffer.append(currentRecord.get(i).toString());
				if (i < currentRecord.size()-1)
					strBuffer.append(splitter);
			}
			break;
		
		case ARRAY_BUFFER:
			arrayBuffer.add(currentRecord);
			break;
		}
		currentRecord.clear();
	}
	
	public void finalizeRecordInFile() throws Exception
	{
		if (fileWriter != null)
		{	
			StringBuffer s = new StringBuffer();
			for (int i = 0; i < currentRecord.size(); i++)
			{	
				s.append(currentRecord.get(i).toString());
				if (i < currentRecord.size()-1)
					s.append(splitter);
			}
			s.append(endLine);
			fileWriter.write(s.toString());
			fileWriter.flush();
		}
	}
	
	public String getDataStringBuffer()
	{
		if (strBuffer != null)
			return strBuffer.toString();
		return null;
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
