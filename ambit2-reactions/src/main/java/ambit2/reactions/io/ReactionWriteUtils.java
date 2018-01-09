package ambit2.reactions.io;

import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;

public class ReactionWriteUtils 
{
	public static FileWriter createWriter(String fname) throws Exception
	{
		FileWriter writer = null;
		try {
			writer = new FileWriter(fname);
			
		}catch (Exception x) {
			//in case smth's wrong with the writer file, close it and throw an error
			try {writer.close(); } catch (Exception xx) {}
			throw x;
		} finally { }
		
		return writer;
	}
	
	public static  void closeWriter(FileWriter writer)
	{
		try { 
			writer.close();
		} catch (Exception x) {}
	}
	
	public static RandomAccessFile createReader(File file) throws Exception
	{
		RandomAccessFile reader = null; 	
		
		try {
			reader = new RandomAccessFile(file,"r");
			
		}catch (Exception x) {
			//in case smth's wrong with the writer file, close it and throw an error
			try {reader.close(); } catch (Exception xx) {}
			throw x;
		} finally { }
		
		return reader;
	}
	
	public static  void closeReader(RandomAccessFile reader)
	{
		try { 
			reader.close();
		} catch (Exception x) {}
	}
	
}
