package ambit2.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ambit2.base.exceptions.AmbitIOException;

public class ZipReader extends RawIteratingFolderReader {
	final static int BUFFER = 2048;
	public ZipReader(File zipfile) throws AmbitIOException {
		super(unzip(zipfile,getTempFolder()));
	}
   
	public ZipReader(InputStream zipstream) throws AmbitIOException {
		super(unzip(zipstream,getTempFolder()));
	}	
	public static File getTempFolder() throws AmbitIOException {
		try {
			File directory = new File(String.format("%s/%s", System.getProperty("java.io.tmpdir"),UUID.randomUUID().toString()));
			directory.deleteOnExit();
			directory.mkdir();
			return directory;
		} catch (Exception x) {
			throw new AmbitIOException(x);
		}
	}
	public static File[] unzip(File zipfile, File directory) throws AmbitIOException {
		FileInputStream zipstream = null;
		try {
			zipstream = new FileInputStream(zipfile);
			return unzip(zipstream, directory);
		} catch (Exception x) {
			throw new AmbitIOException(x);
		} finally {
			try {zipstream.close(); } catch (Exception x) {}
		}
	}
	public static File[] unzip(InputStream zipstream, File directory) throws AmbitIOException {
		   List<File> files = new ArrayList<File>();
	      try {
    	  
	         BufferedOutputStream dest = null;
	         ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipstream));
	         ZipEntry entry;
	        
	         while((entry = zis.getNextEntry()) != null) {
	 	    	
		        if (entry.isDirectory()) continue;
	            int count;
	            byte data[] = new byte[BUFFER];
	            // write the files to the disk

	            //int ext = entry.getName().lastIndexOf(".");
	            
	        	File file = new File(directory,entry.getName());
	        	try { file.getParentFile().mkdirs(); } catch (Exception x) {}
	        			//UUID.randomUUID().toString(),entry.getName().substring(ext)));
	        	file.deleteOnExit();
	        	files.add(file);
	        	// System.out.println("Extracting: " +entry);
	        	// System.out.println("Writing: " +file.getAbsolutePath());
	            FileOutputStream fos = new FileOutputStream(file);
	            dest = new BufferedOutputStream(fos, BUFFER);
	            while ((count = zis.read(data, 0, BUFFER)) != -1) {
	               dest.write(data, 0, count);
	            }
	            dest.flush();
	            dest.close();
	         }
	         zis.close();
	      } catch(Exception e) {
	         throw new AmbitIOException(e);
	      }
	      return files==null?null:files.size()==0?null:files.toArray(new File[files.size()]);
   }
	
}