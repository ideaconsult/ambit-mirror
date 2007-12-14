package ambit.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.openscience.cdk.io.HINWriter;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.Mol2Writer;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.io.SVGWriter;
import org.openscience.cdk.io.XYZWriter;

import ambit.exceptions.AmbitIOException;

/**
 *  Use {@link #getWriter(OutputStream, String)} to get a writer of the right type.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class FileOutputState extends FileState implements IOutputState {
	public transient static final int SDF_INDEX = 0;
	public transient static final int CSV_INDEX = 1;
	public transient static final int SMI_INDEX = 2;
	public transient static final int TXT_INDEX = 3;
	public transient static final int CML_INDEX = 4;
	public transient static final int MOL_INDEX = 5;
	public transient static final int HIN_INDEX = 6;
	public transient static final int PDB_INDEX = 7;
	public transient static final int XYZ_INDEX = 8;
	public transient static final int XLS_INDEX = 9;
	
	public transient static final int HTML_INDEX = 10;
	public transient static final int PDF_INDEX = 11;
	public transient static final int SVG_INDEX = 12;
	public transient static final int JPG_INDEX = 13;
	public transient static final int PNG_INDEX = 14;
	
	public transient static final String[] extensions = {".sdf",".csv",".smi",".txt",".cml",
			".mol",".hin",".pdb",".xyz",".xls",
			".html",".pdf",".svg",".jpg",".png"};
	public transient static final String[] extensionDescription = 
		{"SDF files with chemical compounds (*.sdf)",
		"CSV files (Comma delimited) *.csv)",
		"SMILES files (*.smi)",
		"Text files (Tab delimited) (*.txt)",
		"Chemical Markup Language files (*.cml)",
		"MOL files (*.mol)",
		"HIN files (*.hin)",
		"PDB files (*.pdb)",
		"XYZ files (*.xyz)",
		"Microsoft Office Excel Workbook (*.xls)",
		
		"HTML (*.html)",
		"Adobe PDF (*.pdf)",
		"SVG (*.svg)",
		"JPEG image (*.jpg)",
		"PNG image (*.png)",

		};
	public FileOutputState() {
		super();
	}

	public FileOutputState(String filename) {
		super(filename);
	}

	public FileOutputState(File file) {
		super(file);
	}

	public IChemObjectWriter getWriter() throws AmbitIOException {
		IChemObjectWriter writer = null;
		try {
			writer = getWriter(new FileOutputStream(getFile(),currentRecord>0),filename.toLowerCase());
		} catch (Exception x) {
			throw new AmbitIOException(MSG_ERRORSAVE,x);
		}			
		return writer;
	}

	public static IChemObjectWriter getWriter(OutputStream stream, String ext) throws AmbitIOException {
		String fname = ext.toLowerCase();
		IChemObjectWriter writer = null;
		try {
			if (fname.endsWith(extensions[SDF_INDEX])) {
				writer = new MDLWriter(stream);
				((MDLWriter) writer).dontWriteAromatic();
			} else if (fname.endsWith(extensions[CSV_INDEX])) 
				writer = new DelimitedFileWriter(stream);
			else if ((fname.endsWith(extensions[TXT_INDEX]))) 
				writer = new DelimitedFileWriter(stream,
						new DelimitedFileFormat('\t','"'));
			else if ((fname.endsWith(extensions[SMI_INDEX]))) 
				writer = new SMILESWriter(stream);
			else if ((fname.endsWith(extensions[HTML_INDEX]))) 
				writer = new HTMLTableWriter(stream);
			else if ((fname.endsWith(extensions[JPG_INDEX]))) 			
				writer = new ImageWriter(stream);
			else if ((fname.endsWith(extensions[PNG_INDEX])))  
				writer = new ImageWriter(stream);
			else if ((fname.endsWith(extensions[PDF_INDEX]))) 
				writer = null;			
			else if ((fname.endsWith(extensions[XYZ_INDEX]))) 
				writer = new XYZWriter(stream);
			else if ((fname.endsWith(extensions[HIN_INDEX]))) 
				writer = new HINWriter(stream);
			else if ((fname.endsWith(extensions[MOL_INDEX]))) 
				writer = new MDLWriter(stream);			
			else if ((fname.endsWith(extensions[SVG_INDEX]))) 
				writer = new SVGWriter(stream);
			else if ((fname.endsWith(extensions[XLS_INDEX]))) 
				writer = new XLSFileWriter(stream);			
						
			else throw new AmbitIOException(MSG_UNSUPPORTEDFORMAT+ext);
		} catch (Exception x) {
			//logger.error(MSG_ERRORSAVE,filename);
			throw new AmbitIOException(MSG_ERRORSAVE,x);
		}			
		return writer;
	}
	public static String getResponseType(String ext) {
		if (ext.endsWith(extensions[SDF_INDEX])) {
			return "text/plain";
		} else if (ext.endsWith(extensions[SMI_INDEX])) { 
			return "text/plain";
		} else if (ext.endsWith(extensions[CSV_INDEX])) {
			return "application/vnd.ms-excel";
		} else if (ext.endsWith(extensions[TXT_INDEX])) {
			return "text/plain";	
		} else if (ext.endsWith(extensions[JPG_INDEX])) { 		
			return "image/jpeg";
		} else if (ext.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (ext.endsWith(extensions[PNG_INDEX])) {
			return "image/png";			
		} else	return "text/plain";	    
		
	}

	
}
