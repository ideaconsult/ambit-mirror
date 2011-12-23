package ambit2.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipReader extends RawIteratingFolderReader {
	final static int BUFFER = 2048;
	public ZipReader(File zipfile) throws Exception {
		super(unzip(zipfile));
	}
   
	public static File[] unzip(File zipfile) throws Exception {
		File directory = new File(String.format("%s/%s", System.getProperty("java.io.tmpdir"),UUID.randomUUID().toString()));
		directory.deleteOnExit();
		directory.mkdir();
		return unzip(zipfile,directory);
	}
	public static File[] unzip(File zipfile, File directory) throws Exception {
		   List<File> files = new ArrayList<File>();
	      try {
    	  
	         BufferedOutputStream dest = null;
	         FileInputStream fis = new FileInputStream(zipfile);
	         ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	         ZipEntry entry;
	        
	         while((entry = zis.getNextEntry()) != null) {
	           
	            int count;
	            byte data[] = new byte[BUFFER];
	            // write the files to the disk
	    	
	        	File file = new File(directory,entry.getName());
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
	         throw e;
	      }
	      return files==null?null:files.size()==0?null:files.toArray(new File[files.size()]);
   }
	
}