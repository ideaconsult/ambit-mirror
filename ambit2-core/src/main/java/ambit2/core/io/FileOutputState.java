package ambit2.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import org.openscience.cdk.io.HINWriter;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.io.XYZWriter;

import ambit2.base.exceptions.AmbitIOException;

/**
 *  Use {@link #getWriter(OutputStream, String)} to get a writer of the right type.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class FileOutputState extends FileState implements IOutputState {


		public FileOutputState() {
		super();
		setSupportedExtDescriptions(getExtensionDescriptions(false));
		setSupportedExtensions(getExtensions(false));		
	}

	public FileOutputState(String filename) {
		super(filename);
		setSupportedExtDescriptions(getExtensionDescriptions(false));
		setSupportedExtensions(getExtensions(false));		
	}

	public FileOutputState(File file) {
		super(file);
		setSupportedExtDescriptions(getExtensionDescriptions(false));
		setSupportedExtensions(getExtensions(false));		
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
			if (fname.endsWith(_FILE_TYPE.SDF_INDEX.getExtension())) {
				writer = new SDFWriter(stream);
			} else if (fname.endsWith(_FILE_TYPE.CSV_INDEX.getExtension())) 
				writer = new DelimitedFileWriter(stream);
			else if ((fname.endsWith(_FILE_TYPE.TXT_INDEX.getExtension()))) 
				writer = new DelimitedFileWriter(stream,
						new DelimitedFileFormat("\t",'"'));
			else if ((fname.endsWith(_FILE_TYPE.SMI_INDEX.getExtension()))) 
				writer = new SMILESWriter(stream);
			/*
			else if ((fname.endsWith(extensions[HTML_INDEX]))) 
				writer = new HTMLTableWriter(stream);
			else if ((fname.endsWith(extensions[JPG_INDEX]))) 			
				writer = new ImageWriter(stream);
			else if ((fname.endsWith(extensions[PNG_INDEX])))  
				writer = new ImageWriter(stream);
				*/
			else if ((fname.endsWith(_FILE_TYPE.PDF_INDEX.getExtension()))) {
				try {
					writer = createWriterByReflection(
							"ambit2.core.io.PDFWriter",
							stream);
				} catch (Exception x) {
					throw new AmbitIOException(x);
				}	
			} else if ((fname.endsWith(_FILE_TYPE.XYZ_INDEX.getExtension()))) 
				writer = new XYZWriter(stream);
			else if ((fname.endsWith(_FILE_TYPE.HIN_INDEX.getExtension()))) 
				writer = new HINWriter(stream);
			else if ((fname.endsWith(_FILE_TYPE.MOL_INDEX.getExtension()))) 
				writer = new SDFWriter(stream);
			else if ((fname.endsWith(_FILE_TYPE.VW_INDEX.getExtension()))) {
				writer = new DelimitedFileWriter(stream,
						new DelimitedFileFormat("|",'"'));
				((DelimitedFileWriter)writer).setAddSMILEScolumn(false);
			}	
						
			/*
			else if ((fname.endsWith(extensions[SVG_INDEX]))) 
				writer = new SVGWriter(stream);
				*/
			else if ((fname.endsWith(_FILE_TYPE.XLS_INDEX.getExtension()))) 
				try {
					writer = createXLSXWriterByReflection(
							"ambit2.core.io.XLSFileWriter",
							stream,true);
				} catch (Exception x) {
					throw new AmbitIOException(x);
				}	
			else if ((fname.endsWith(_FILE_TYPE.XLSX_INDEX.getExtension()))) 
				try {
					writer = createXLSXWriterByReflection(
							"ambit2.core.io.XLSFileWriter",
							stream,false);
				} catch (Exception x) {
					throw new AmbitIOException(x);
				}
					
			else throw new AmbitIOException(MSG_UNSUPPORTEDFORMAT+ext);
		} catch (Exception x) {
			//logger.error(MSG_ERRORSAVE,filename);
			throw new AmbitIOException(MSG_ERRORSAVE,x);
		}			
		return writer;
	}
	
	private static IChemObjectWriter createWriterByReflection(
			String className, OutputStream stream) throws Exception {
		Class clazz = FileInputState.class.getClassLoader()
				.loadClass(className);
		Constructor<? extends Runnable> constructor = clazz
				.getConstructor(new Class[] {OutputStream.class, Boolean.class});
		Object o = constructor.newInstance(stream);
		return (IChemObjectWriter) o;
	}

	private static IChemObjectWriter createXLSXWriterByReflection(
			String className, OutputStream stream,  boolean hssf) throws Exception {
		Class clazz = FileInputState.class.getClassLoader()
				.loadClass(className);
		Constructor<? extends Runnable> constructor = clazz
				.getConstructor(new Class[] {OutputStream.class, Boolean.class});
		Object o = constructor.newInstance(new Object[] {stream,hssf});
		return (IChemObjectWriter) o;
	}
	/*
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
*/
	
}
