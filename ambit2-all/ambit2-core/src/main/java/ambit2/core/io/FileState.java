/*
 * Created on 2005-9-4
 *
 */
package ambit2.core.io;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.io.formats.IChemFormat;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.base.interfaces.IInputOutputState;



/**
 * This is to be able to verify if a file has changed since it has been last processed by FileBatchProcessing
 * @TODO cleanup
 * @author Nina Jeliazkova
 * <b>Modified</b> 2015-10-31
 */
public class FileState implements IInputOutputState {


	public enum _FILE_TYPE {
		SDF_INDEX,
		CSV_INDEX {
			@Override
			public String getDescription() {
				return "CSV files (Comma delimited) *.csv)";
			}
		},
		SMI_INDEX, TXT_INDEX {
			@Override
			public String getDescription() {
				return "Text files (Tab delimited) (*.txt)";
			}
		},
		TSV_INDEX {
			@Override
			public String getDescription() {
				return "Text files (Tab delimited) (*.tsv)";
			}
		},		
		MOL_INDEX, ICHI_INDEX {
			@Override
			public String getExtension() {
				return ".inchi";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		INCHI_INDEX {
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		CML_INDEX {
			@Override
			public String getDescription() {
				return "Chemical Markup Language files (*.cml)";
			}
		},
		HIN_INDEX, PDB_INDEX, 
		XLS_INDEX {
			@Override
			public String getDescription() {
				return "Microsoft Office Excel files (*.xls)";
			}
			
		}, 
		XLSX_INDEX {
			@Override
			public String getDescription() {
				return "Microsoft Office Excel files (*.xlsx)";
			}
		}, 
		EURAS_INDEX {
			@Override
			public String getDescription() {
				return "EURAS Excel file with BCF data (*.xls)";
			}

			@Override
			public String getExtension() {
				return ".xls";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		ECHAXML_INDEX {
			@Override
			public String getExtension() {
				return ".echaxml";
			}

			@Override
			public String getDescription() {
				return "ECHA preregistration list XML format (*.echaxml)";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		TOXML_INDEX {
			@Override
			public String getExtension() {
				return ".xml";
			}

			@Override
			public String getDescription() {
				return "Leadscope ToXML 3.08 (*.xml)";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		MALARIA_HTS_SHEETS {
			@Override
			public String getExtension() {
				return ".sht";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		ZIP_INDEX {
			@Override
			public String getDescription() {
				return "ZIP archive (*.zip)";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		GZ_INDEX {
			@Override
			public String getDescription() {
				return "GZ archive (*.gz)";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		I5D_INDEX {
			@Override
			public String getDescription() {
				return "IUCLID5 xml (*.i5d)";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		I5Z_INDEX {
			@Override
			public String getDescription() {
				return "IUCLID5 archive (*.i5z)";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		NANOCMLx_INDEX {
			@Override
			public String getExtension() {
				return ".nmx";
			}

			@Override
			public String getDescription() {
				return "Nano CML (*.nmx)";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}
		},
		NANOCMLd_INDEX {
			@Override
			public String getDescription() {
				return "Nano CML (*.nmd)";
			}

			@Override
			public String getExtension() {
				return ".nmd";
			}
			@Override
			public boolean supportsOutput() {
				return false;
			}

		},
		XYZ_INDEX {
			@Override
			public boolean supportsOutput() {
				return true;
			}
			@Override
			public boolean supportsInput() {
				return false;
			}
		},
		HTML_INDEX {
			@Override
			public boolean supportsOutput() {
				return true;
			}
			@Override
			public boolean supportsInput() {
				return false;
			}
			@Override
			public String getDescription() {
				return "HTML (*.html)";
			}
		},
		PDF_INDEX {
			@Override
			public boolean supportsOutput() {
				return true;
			}
			@Override
			public boolean supportsInput() {
				return false;
			}
			@Override
			public String getDescription() {
				return "Adobe PDF (*.pdf)";
			}
		},		
		SVG_INDEX {
			@Override
			public boolean supportsOutput() {
				return true;
			}
			@Override
			public boolean supportsInput() {
				return false;
			}
			@Override
			public String getDescription() {
				return "SVG (*.svg)";
			}
		},
		JPG_INDEX {
			@Override
			public boolean supportsOutput() {
				return true;
			}
			@Override
			public boolean supportsInput() {
				return false;
			}
			@Override
			public String getDescription() {
				return "JPEG image (*.jpg)";
			}
		},
		PNG_INDEX {
			@Override
			public boolean supportsOutput() {
				return true;
			}
			@Override
			public boolean supportsInput() {
				return false;
			}
			@Override
			public String getDescription() {
				return "PNG image (*.png)";
			}
		},
		RTF_INDEX {
			@Override
			public boolean supportsOutput() {
				return true;
			}
			@Override
			public boolean supportsInput() {
				return false;
			}
			@Override
			public String getDescription() {
				return "Rich Text Format (*.rtf)";
			}
		},
		VW_INDEX {
			@Override
			public boolean supportsOutput() {
				return true;
			}
			@Override
			public boolean supportsInput() {
				return false;
			}
			@Override
			public String getDescription() {
				return "vw";
			}
		}		
		;
		public String getExtension() {
			return "." + name().replace("_INDEX", "").toLowerCase();
		}

		public String getDescription() {
			return name().replace("_INDEX", "") + " files (." + getExtension()
					+ ")";
		}
		public boolean supportsInput() { return true;}
		public boolean supportsOutput() { return true;}
		public boolean hasExtension(String filename) {
			return (filename!=null) && filename.toLowerCase().endsWith(getExtension());
		}
		public boolean hasExtension(File file) {
			return file != null && hasExtension(file.getName());
		}
	}

	
	protected static transient String MSG_FILEDONOTEXISTS = "File do not exists!\t";
	protected static transient String MSG_CANTCREATEFILE = "Can't create file!\t";
	protected static transient String MSG_OPEN = "Opening input file\t";
	protected static transient String MSG_UNSUPPORTEDFORMAT = "UNSUPPORTED FORMAT\t";
	protected static transient String MSG_ERRORSAVE = "Error when writing file\t";
	protected String responseType = "text/plain";	
	protected String[] supportedExtensions = new String[] {"*.*"};
	protected String[] extensionDescriptions = new String[] {"All files"};
	
	private static final long serialVersionUID = -9098389880881547516L;
	protected String filename=null;
	protected long length=0;
	protected long lastModified=0;
	protected int hashCode=0;
	protected transient File file = null;
	protected long offset = 0;
	protected long currentRecord = 0;
	protected IChemFormat fileFormat;
	public IChemFormat getFileFormat() {
		return fileFormat;
	}
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
	public synchronized void setFile(File file) {
		setFile(file,null);
	}
	/**
	 * @param file The file to set.
	 */
	public synchronized void setFile(File file,IChemFormat format) {
		this.fileFormat = format;
		this.file = file;
		setFilename(file.getAbsolutePath());
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
    public String[] getSupportedExtensions() {
    	return supportedExtensions;
    }
    public String[] getSupportedExtDescriptions() {
    	return extensionDescriptions;
    }    
    public void setSupportedExtensions(String[] ext) {
    	supportedExtensions = ext;
    }
    public void setSupportedExtDescriptions(String[] descr) {
    	extensionDescriptions = descr;
    }

	protected static String[] getExtensionDescriptions(boolean input) {
		List<String> ext = new ArrayList<String>();
		for (_FILE_TYPE ft : _FILE_TYPE.values())
			if (input) {
				if (ft.supportsInput()) ext.add(ft.getDescription());
			} else 
				if (ft.supportsOutput()) ext.add(ft.getDescription());
		return ext.toArray(new String[ext.size()]);
	}

	protected static String[] getExtensions(boolean input) {
		List<String> ext = new ArrayList<String>();
		for (_FILE_TYPE ft : _FILE_TYPE.values())
			if (input) {
				if (ft.supportsInput()) ext.add(ft.getExtension());
			} else 
				if (ft.supportsOutput()) ext.add(ft.getExtension());
		return ext.toArray(new String[ext.size()]);
	}

}
