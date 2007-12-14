package ambit.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.INChIReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;

import ambit.exceptions.AmbitIOException;

/**
 * Use {@link #getReader(InputStream, String)} to get a reader of the right type.
 * 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class FileInputState extends FileState implements IInputState {
	public transient static final int SDF_INDEX = 0;
	public transient static final int CSV_INDEX = 1;
	public transient static final int SMI_INDEX = 2;
	public transient static final int TXT_INDEX = 3;
	public transient static final int MOL_INDEX = 4;
	public transient static final int ICHI_INDEX = 5;
	public transient static final int INCHI_INDEX = 6;	
	public transient static final int CML_INDEX = 7;
	public transient static final int HIN_INDEX = 8;
	public transient static final int PDB_INDEX = 9;
	public transient static final int XLS_INDEX = 10;	
	
	public transient static final String[] extensions = {".sdf",".csv",".smi",".txt",".mol",".ichi",".inchi",".cml",".hin",".pdb",".xls"};
	public transient static final String[] extensionDescription = 
		{"SDF files with chemical compounds (*.sdf)",
		"CSV files (Comma delimited) *.csv)",
		"SMILES files (*.smi)",
		"Text files (Tab delimited) (*.txt)",
		"MDL MOL files (*.mol)",
		"ICHI files (*.ichi)",
		"InCHi files (*.inchi)",
		"Chemical Markup Language files (*.cml)",
		"HIN files (*.hin)",
		"PDB files (*.pdb)",
		"Microsoft Office Excel file (*.xls)"};	
	public FileInputState() {
		super();
	}

	public FileInputState(String filename) {
		super(filename);
	}

	public FileInputState(File file) {
		super(file);
	}

	public IIteratingChemObjectReader getReader()
			throws AmbitIOException {
		try {
			String fname = filename.toLowerCase();
			return getReader(new FileInputStream(getFile()),fname,fileFormat);
		} catch (FileNotFoundException x) {
			throw new AmbitIOException(x);
		}
	}
	public static IIteratingChemObjectReader getReader(InputStream stream, String ext) throws AmbitIOException {
		return getReader(stream, ext,null);
	}
	public static IIteratingChemObjectReader getReader(InputStream stream, String ext, IChemFormat format) throws AmbitIOException {
		if (ext.endsWith(extensions[SDF_INDEX])) {
			return new InteractiveIteratingMDLReader(stream,DefaultChemObjectBuilder.getInstance());
		} else if (ext.endsWith(extensions[SMI_INDEX])) { 
			return new IteratingSMILESReader(stream);
		} else if (ext.endsWith(extensions[CSV_INDEX])) {
			if ((format != null) && (format instanceof DelimitedFileFormat))
			return new IteratingDelimitedFileReader(stream,(DelimitedFileFormat)format);
			else
				return new IteratingDelimitedFileReader(stream,new DelimitedFileFormat(',','"'));
		} else if (ext.endsWith(extensions[TXT_INDEX])) {
			if ((format != null) && (format instanceof DelimitedFileFormat))
				return new IteratingDelimitedFileReader(stream,(DelimitedFileFormat)format);
				else
					return new IteratingDelimitedFileReader(stream,new DelimitedFileFormat('\t','"'));			
					//new DelimitedFileFormat('\t','"'));		
		} else if (ext.endsWith(extensions[MOL_INDEX])) {
			//return new IteratingChemObjectReaderWrapper(new FilteredMDLReader(stream));
			return new IteratingChemObjectReaderWrapper(new MDLReader(stream));
		
		} else if (ext.endsWith(extensions[INCHI_INDEX])) {
			return new IteratingChemObjectReaderWrapper(new INChIReader(stream));
		} else if (ext.endsWith(extensions[CML_INDEX])) {
			return new IteratingChemObjectReaderWrapper(new CMLReader(stream));			
		} else if (ext.endsWith(extensions[HIN_INDEX])) {
			return new IteratingChemObjectReaderWrapper(new HINReader(stream));
		} else if (ext.endsWith(extensions[PDB_INDEX])) {
			return new IteratingChemObjectReaderWrapper(new PDBReader(stream));
		} else if (ext.endsWith(extensions[XLS_INDEX])) {
			return new IteratingXLSReader(stream,0);
		} else throw new AmbitIOException(MSG_UNSUPPORTEDFORMAT+ext);	    
	}
}
