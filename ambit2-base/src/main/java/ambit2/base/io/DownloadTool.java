package ambit2.base.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

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
		if ((dir!=null) && !dir.exists())	dir.mkdirs();		
    	FileOutputStream out = new FileOutputStream(file);		
    	try {
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
	   /**
	    * Channel copy method 1.  This method copies data from the src
	    * channel and writes it to the dest channel until EOF on src.
	    * This implementation makes use of compact() on the temp buffer
	    * to pack down the data if the buffer wasn't fully drained.  This
	    * may result in data copying, but minimizes system calls.  It also
	    * requires a cleanup loop to make sure all the data gets sent.
	    */
	   private static void channelCopy1 (ReadableByteChannel src,
	      WritableByteChannel dest)
	      throws IOException
	   {
	      ByteBuffer buffer = ByteBuffer.allocateDirect (16 * 1024);

	      while (src.read (buffer) != -1) {
	         // prepare the buffer to be drained
	         buffer.flip();

	         // write to the channel, may block
	         dest.write (buffer);

	         // If partial transfer, shift remainder down
	         // If buffer is empty, same as doing clear()
	         buffer.compact();
	      }

	      // EOF will leave buffer in fill state
	      buffer.flip();

	      // make sure the buffer is fully drained.
	      while (buffer.hasRemaining()) {
	         dest.write (buffer);
	      }
	   }	
	  /**
	    * Channel copy method 2.  This method performs the same copy, but
	    * assures the temp buffer is empty before reading more data.  This
	    * never requires data copying but may result in more systems calls.
	    * No post-loop cleanup is needed because the buffer will be empty
	    * when the loop is exited.
	    */
	 public static void channelCopy2(ReadableByteChannel src,
	      WritableByteChannel dest)
	      throws IOException
	   {
	      ByteBuffer buffer = ByteBuffer.allocateDirect (16 * 1024);

	      while (src.read (buffer) != -1) {
	         // prepare the buffer to be drained
	         buffer.flip();

	         // make sure the buffer was fully drained.
	         while (buffer.hasRemaining()) {
	            dest.write (buffer);
	         }

	         // make the buffer empty, ready for filling
	         buffer.clear();
	      }
	 }	

}
