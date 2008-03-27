package ambit2.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.exceptions.AmbitIOException;

public class IteratingMolFolderReader extends DefaultIteratingChemObjectReader {
	protected File[] files;
	protected int index = -1;
	protected IIteratingChemObjectReader reader;
	
	public IteratingMolFolderReader() {
		this(null);
	}
	public IteratingMolFolderReader(File[] files) {
		super();
		setFiles(files);
	}	
	public IResourceFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}


	public void close() throws IOException {
		closeItemReader(reader);

	}


	protected IIteratingChemObjectReader getItemReader(int index) throws Exception {
		return FileInputState.getReader(new FileInputStream(files[index]), files[index].getName());		
	}
	protected void closeItemReader(IIteratingChemObjectReader itemReader) throws IOException {
		if (itemReader != null) {
			itemReader.close();
			itemReader = null;
		}
	}
	public boolean hasNext() {
		if ((files!= null) && (files.length>0) ) {
			
			if (reader == null) {
				try {
					reader = getItemReader(index);
					return reader.hasNext();
				} catch (Exception x) {
					System.err.println(x);
					return false;
				}
			} else {
				boolean next  = reader.hasNext();
				if (next) return next;
				else try {
					closeItemReader(reader);
					index++;
					if (index < files.length) {
						reader = getItemReader(index);
						return reader.hasNext();
					} else return false;
				} catch (Exception x) {
					System.err.println(x);
					return false;					
				}
			}
		} else return false;
	}

	public Object next() {
		if (reader != null) {
			Object o = reader.next();
			if (o instanceof IChemObject) {
				if (((IChemObject)o).getProperty(CDKConstants.NAMES)==null) 
					((IChemObject)o).setProperty(CDKConstants.NAMES,files[index].getName());
			}
			return o;
		} else return null;
	}

	public File[] getFiles() {
		return files;
	}
	public void setFiles(File[] files) {
		this.files = files;
		if ((files!= null) && (files.length>0) )
			index = 0;
		else index = -1;
		reader = null;
	}
	@Override
	public String toString() {
		return "Reads set of files";
	}
}
