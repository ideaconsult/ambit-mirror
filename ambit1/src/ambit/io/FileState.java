/*
 * Created on 2005-9-4
 *
 */
package ambit.io;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.openscience.cdk.io.formats.IChemFormat;

import ambit.exceptions.AmbitIOException;



/**
 * This is to be able to verify if a file has changed since it has been last processed by FileBatchProcessing
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-4
 */
public class FileState implements IInputOutputState {

	protected static transient String MSG_FILEDONOTEXISTS = "File do not exists!\t";
	protected static transient String MSG_CANTCREATEFILE = "Can't create file!\t";
	protected static transient String MSG_OPEN = "Opening input file\t";
	protected static transient String MSG_UNSUPPORTEDFORMAT = "UNSUPPORTED FORMAT\t";
	protected static transient String MSG_ERRORSAVE = "Error when writing file\t";
	protected String responseType = "text/plain";	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -9098389880881547516L;
	protected String filename=null;
	protected long length=0;
	protected long lastModified=0;
	protected int hashCode=0;
	protected transient File file = null;
	protected long offset = 0;
	protected long currentRecord = 0;
	protected IChemFormat fileFormat = null;
	/**
	 * 
	 */
	public FileState() {
		super();
	}
	public FileState(String filename) {
		this(new File(filename));
	}	
	public FileState(File file) {
		this();
		setFile(file);
		
	}	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		FileState fs = (FileState) obj;
		return filename.equals(fs.getFilename()) &&
			   (length == fs.length) &&
			   (lastModified == fs.lastModified) &&
			   (hashCode == fs.hashCode) &&
			   (currentRecord == fs.currentRecord) &&
			   (offset == fs.offset)
			   ;
		
	}

	/**
	 * @return Returns the filename.
	 */
	public synchronized String getFilename() {
		return filename;
	}
	/**
	 * @param filename The filename to set.
	 */
	public synchronized void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return Returns the hashCode.
	 */
	public synchronized int getHashCode() {
		return hashCode;
	}
	/**
	 * @param hashCode The hashCode to set.
	 */
	public synchronized void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	/**
	 * @return Returns the lastModified.
	 */
	public synchronized long getLastModified() {
		return lastModified;
	}
	/**
	 * @param lastModified The lastModified to set.
	 */
	public synchronized void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	/**
	 * @return Returns the length.
	 */
	public synchronized long getLength() {
		return length;
	}
	/**
	 * @param length The length to set.
	 */
	public synchronized void setLength(long length) {
		this.length = length;
	}
/*
	private void writeObject(java.io.ObjectOutputStream out)
	     throws IOException {
		
	}
	 private void readObject(java.io.ObjectInputStream in)
	     throws IOException, ClassNotFoundException {
	 	
	 }
	 */
	 	
	/**
	 * @return Returns the file.
	 */
	public synchronized File getFile() {
		return file;
	}
	/**
	 * @param file The file to set.
	 */
	public synchronized void setFile(File file) {
		this.file = file;
		filename = file.getAbsolutePath();
		length = file.length();
		lastModified = file.lastModified();
		hashCode = file.hashCode();
		this.file = file;
		currentRecord = 0;
		offset = 0;
	}
	/**
	 * @return Returns the currentRecord.
	 */
	public long getCurrentRecord() {
		return currentRecord;
	}
	/**
	 * @param currentRecord The currentRecord to set.
	 */
	public void setCurrentRecord(long currentRecord) {
		this.currentRecord = currentRecord;
	}
	/**
	 * @return Returns the offset.
	 */
	public synchronized long getOffset() {
		return offset;
	}
	/**
	 * @param offset The offset to set.
	 */
	public synchronized void setOffset(long offset) {
		this.offset = offset;
	}
	public boolean match() throws AmbitIOException {
		return match(createFile());
	}
	public boolean match(File file) {
		
		return filename.equals(file.getAbsolutePath()) &&
		   (length == file.length()) &&
		   (lastModified == file.lastModified()) 
		   //&&   (hashCode == file.hashCode())
		   ;
		
	}
	public File createFile()  throws AmbitIOException {
		try {
			return new File(filename);
		} catch (Exception x) {
			throw new AmbitIOException(x);
		}
	}
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(" [");
		b.append((currentRecord+1));
		b.append(" ] ");
		b.append(filename);
		return b.toString();
	}
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(serialVersionUID);
		out.writeObject(filename);
		out.writeLong(length);
		out.writeLong(lastModified);
		out.writeInt(hashCode);
		out.writeLong(offset);
		out.writeLong(currentRecord);
	}


	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		if (in.readLong() != serialVersionUID) throw new ClassNotFoundException();
		filename =in.readObject().toString();
		length =in.readLong();
		lastModified = in.readLong();
		hashCode = in.readInt();
		offset = in.readLong();
		currentRecord = in.readLong();
		file = new File(filename);
	}
    public String getResponseType() {
    	return responseType;
    }
    public void setResponseType(String responseType) {
    	this.responseType = responseType;
    }
}
