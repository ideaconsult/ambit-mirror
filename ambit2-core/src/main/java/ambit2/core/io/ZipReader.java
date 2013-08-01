package ambit2.core.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import ambit2.base.exceptions.AmbitIOException;

public class ZipReader extends RawIteratingFolderReader {
	final static int BUFFER = 2048;
	static final int TOOBIG = 0x6400000; // 100MB
	
	public ZipReader(File zipfile) throws AmbitIOException {
		super(unzip(zipfile,getTempFolder()));
	}
   
	public ZipReader(InputStream zipstream) throws AmbitIOException {
		super(unzip(zipstream,getTempFolder()));
	}	
	public static File getTempFolder() throws AmbitIOException {
		try {
			File directory = new File(String.format("%s/Z_%s", System.getProperty("java.io.tmpdir"),UUID.randomUUID().toString()));
			directory.deleteOnExit();
			directory.mkdir();
			return directory;
		} catch (Exception x) {
			throw new AmbitIOException(x);
		}
	}
	public static File[] unzip(File zipfile, File directory) throws AmbitIOException {
		List<File> files = new ArrayList<File>();
		FileInputStream zipstream = null;
		try {
			ZipFile zipFile = new ZipFile(zipfile);
			Enumeration<? extends ZipEntry> zEntries = zipFile.entries();
			while (zEntries.hasMoreElements()) {
				ZipEntry zipEntry = zEntries.nextElement();
				if (zipEntry.isDirectory()) continue;
				File file = unzipEntry(zipEntry, zipFile.getInputStream(zipEntry), directory);
				if (file!=null) files.add(file);
			}
			return files==null?null:files.size()==0?null:files.toArray(new File[files.size()]);
		} catch (Exception x) {
			throw new AmbitIOException(x);
		} finally {
			try {zipstream.close(); } catch (Exception x) {}
		}
	}

	public static File[] unzip(InputStream zipstream, File directory) throws AmbitIOException {
		   List<File> files = new ArrayList<File>();
		   ZipInputStream zis = null;
	      try {
	         zis = new ZipInputStream(zipstream);
	         ZipEntry entry;
             while((entry = zis.getNextEntry())!=null) {
            	 System.out.println("Extracting: " + entry);
		         if (entry.isDirectory()) continue;
	        	 File file = unzipEntry(entry, zis, directory);
	        	 if (file!=null) files.add(file);
	         }
	      } catch(Exception e) {
	         throw new AmbitIOException(e);
	      } finally {
		     try {if (zis!=null) zis.close();} catch (Exception x) {}	    	  
	      }
	      return files==null?null:files.size()==0?null:files.toArray(new File[files.size()]);
   }
	
	protected static File unzipEntry(ZipEntry entry,InputStream zis, File directory) throws FileNotFoundException,IOException {
	     byte data[] = new byte[BUFFER];
         int total = 0;
         int count;
     	 File file = new File(directory,entry.getName());
     	 try { file.getParentFile().mkdirs(); } catch (Exception x) { x.printStackTrace();}
     	 file.deleteOnExit();
     	 FileOutputStream fos = new FileOutputStream(file);
     	 BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
     	 try {
	         while (total <= TOOBIG && (count = zis.read(data, 0, BUFFER)) != -1) {
	            dest.write(data, 0, count);
	            total += count;
	         }
	         dest.flush();
     	 } finally {
     		 try {dest.close();} catch (Exception x) {}
     	 }
         if (total > TOOBIG) {
             throw new IllegalStateException("File being unzipped is huge.");  }

         return file;
	}
}