package ambit2.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class DownloadTool {
	public static void download(URL url, File file) throws IOException {
    	InputStream in = url.openStream();
    	try {
    		download(in, file);
    	} catch (IOException x) {
    		throw new IOException(x.getMessage());
    	} finally {
    		in.close();
    	}
    }
	public static void download(InputStream in, File file) throws IOException  {
		File dir = file.getParentFile();
		if (!dir.exists())	dir.mkdirs();		
    	FileOutputStream out = new FileOutputStream(file);		
    	try {
    		//logger.info("Downloading "+url + " to "+file.getAbsolutePath());
			download(in, out);
    	} catch (IOException x) {
    		throw new IOException(x.getMessage());
    	} finally {
			out.close();
    	}
    }
	public static void download(InputStream in, OutputStream out) throws IOException {
			byte[] bytes = new byte[512];
			int len;
			long count = 0;
			while ((len = in.read(bytes, 0, bytes.length)) != -1) {
				out.write(bytes, 0, len);
				count += len;
			}
			out.flush();
    }	
	public static void download(String resource, OutputStream out) throws IOException {
		InputStream in = DownloadTool.class.getClassLoader().getResourceAsStream(resource);
		try {
			download(in, out);
		} catch (IOException x) {
			throw new IOException(x.getMessage());
		} finally {
			in.close();			
		}

    }	
	public static void download(String resource, File file) throws IOException {
		InputStream in = DownloadTool.class.getClassLoader().getResourceAsStream(resource);
		if (in == null) throw new IOException(resource);
		try {
			download(in, file);
		} catch (IOException x) {
			throw new IOException(x.getMessage());
		} finally {
			in.close();			
		}

    }		

}
