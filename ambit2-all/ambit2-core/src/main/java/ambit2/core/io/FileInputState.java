package ambit2.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.INChIReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.bcf.EurasBCFReader;
import ambit2.core.io.sj.MalariaHTSDataDelimitedReader;

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
	public transient static final int EURAS_INDEX = 11;
	public transient static final int ECHAXML_INDEX = 12;
	public transient static final int TOXML_INDEX = 13;
	public transient static final int MALARIA_HTS_SHEETS = 14;
	public transient static final int ZIP_INDEX = 15;
	
	//TODO support for .xlsx 
	public transient static final String[] extensions = {
		".sdf",".csv",".smi",".txt",".mol",".ichi",".inchi",
		".cml",".hin",".pdb",".xls",".xls",".echaxml",".xml",
		".sht",".zip"};
	public transient static final String[] extensionDescription = 
		{"SDF files with chemical compounds (*.sdf)",
		"CSV files (Comma delimited) *.csv)",
		"SMILES files (*.smi)",
		"Text files (Tab delimited) (*.txt)",
		"MDL MOL files (*.mol)",
		"ICHI files (*.ichi)",
		"InChI files (*.inchi)",
		"Chemical Markup Language files (*.cml)",
		"HIN files (*.hin)",
		"PDB files (*.pdb)",
		"Microsoft Office Excel file (*.xls)",
		"EURAS Excel file with BCF data (*.xls)",
		"ECHA preregistration list XML format (*.echaxml)",
		"Leadscope ToXML 3.08 (*.xml)",
		"Malaria HTS data sheets (*.sht)",
		"ZIP archive (*.zip)",
		};	
	public FileInputState() {
		super();
		setSupportedExtDescriptions(extensionDescription);
		setSupportedExtensions(extensions);		
	}

	public FileInputState(String filename) {
		super(filename);
		setSupportedExtDescriptions(extensionDescription);
		setSupportedExtensions(extensions);		
	}

	public FileInputState(File file) {
		super(file);
		setSupportedExtDescriptions(extensionDescription);
		setSupportedExtensions(extensions);
	}

	public IIteratingChemObjectReader getReader()
			throws AmbitIOException {
		try {
			String fname = filename.toLowerCase();
			return getReader(new FileInputStream(getFile()),fname,fileFormat);
		} catch (CDKException x) {
			throw new AmbitIOException(x);
		} catch (FileNotFoundException x) {
			throw new AmbitIOException(x);
		}
	}
	public static IIteratingChemObjectReader getReader(InputStream stream, String ext) throws AmbitIOException, CDKException {
		return getReader(stream, ext,null);
	}
	public static IIteratingChemObjectReader getReader(InputStream stream, String ext, IChemFormat format) throws AmbitIOException, CDKException {
		if (ext.endsWith(extensions[SDF_INDEX])) {
			return new InteractiveIteratingMDLReader(stream,SilentChemObjectBuilder.getInstance());
		} else if (ext.endsWith(extensions[SMI_INDEX])) { 
			return new IteratingSMILESReader(stream);
		} else if (ext.endsWith(extensions[CSV_INDEX])) {
			try {
				if ((format != null) && (format instanceof DelimitedFileFormat))
				return new IteratingDelimitedFileReader(stream,(DelimitedFileFormat)format);
				else
					return new IteratingDelimitedFileReader(stream,new DelimitedFileFormat(",",'"'));
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		} else if (ext.endsWith(extensions[TXT_INDEX])) {
			try {
				if ((format != null) && (format instanceof DelimitedFileFormat)) {
					DelimitedFileFormat df = (DelimitedFileFormat) format;
					if (df.fieldDelimiter.equals("\t") && (df.textDelimiter=='"'))
						return new ToxcastAssayReader(stream);
					else return new IteratingDelimitedFileReader(stream,(DelimitedFileFormat)format);
				} else
					return new ToxcastAssayReader(stream);		
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
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
		} else if ((ext.toLowerCase().indexOf("euras")>=0) && (ext.endsWith(extensions[XLS_INDEX]))) {
			return new EurasBCFReader(stream,0);			
		} else if (ext.endsWith(extensions[XLS_INDEX])) {
			return new IteratingXLSReader(stream,0);
		} else if (ext.endsWith(extensions[ECHAXML_INDEX])) {
			return new ECHAPreregistrationListReader(stream);
		} else if (ext.endsWith(extensions[TOXML_INDEX])) {
			return new ToXMLReaderSimple(stream);	
		} else if (ext.endsWith(extensions[MALARIA_HTS_SHEETS])) {
			return new MalariaHTSDataDelimitedReader(stream);	
		} else if (ext.endsWith(extensions[ZIP_INDEX])) {
			return new ZipReader(stream);					
		} else throw new AmbitIOException(MSG_UNSUPPORTEDFORMAT+ext);	    
	}
	
}
