package ambit2.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;

import org.apache.commons.csv.CSVFormat;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.INChIReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.pdb.RawIteratingPDBReader;
import ambit2.core.io.sj.MalariaHTSDataDelimitedReader;

/**
 * Use {@link #getReader(InputStream, String)} to get a reader of the right
 * type.
 * 
 * @author Nina Jeliazkova nina@acad.bg <b>Modified</b> June 13, 2013
 */
public class FileInputState extends FileState implements IInputState {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4293026709920994430L;
	public String optionalSMILESHeader = null;
	public String optionalInChIKeyHeader = null;
	public String optionalInChIHeader = null;

	public String getOptionalInChIKeyHeader() {
		return optionalInChIKeyHeader;
	}

	public void setOptionalInChIKeyHeader(String optionalInChIKeyHeader) {
		this.optionalInChIKeyHeader = optionalInChIKeyHeader;
	}

	public String getOptionalInChIHeader() {
		return optionalInChIHeader;
	}

	public void setOptionalInChIHeader(String optionalInChIHeader) {
		this.optionalInChIHeader = optionalInChIHeader;
	}

	public String getOptionalSMILESHeader() {
		return optionalSMILESHeader;
	}

	public void setOptionalSMILESHeader(String optionalSMILESHeader) {
		this.optionalSMILESHeader = optionalSMILESHeader == null ? null
				: optionalSMILESHeader.toUpperCase();
	}

	public FileInputState() {
		super();
		setSupportedExtDescriptions(getExtensionDescriptions(true));
		setSupportedExtensions(getExtensions(true));
	}

	public FileInputState(String filename) {
		super(filename);
		setSupportedExtDescriptions(getExtensionDescriptions(true));
		setSupportedExtensions(getExtensions(true));
	}

	public FileInputState(File file) {
		super(file);
		setSupportedExtDescriptions(getExtensionDescriptions(true));
		setSupportedExtensions(getExtensions(true));
	}

	public IIteratingChemObjectReader getReader() throws AmbitIOException {
		try {
			String fname = filename.toLowerCase();
			return getReader(new FileInputStream(getFile()), fname, fileFormat);
		} catch (CDKException x) {
			throw new AmbitIOException(x);
		} catch (FileNotFoundException x) {
			throw new AmbitIOException(x);
		}
	}

	public static IIteratingChemObjectReader getReader(File file)
			throws FileNotFoundException, AmbitIOException, CDKException {
		return getReader(file, null);
	}

	public static IIteratingChemObjectReader getReader(InputStream stream,
			String ext) throws AmbitIOException, CDKException {
		return getReader(stream, ext, null);
	}

	public static IIteratingChemObjectReader getReader(File file,
			IChemFormat format) throws AmbitIOException, CDKException,
			FileNotFoundException {
		if (file.getName().endsWith(_FILE_TYPE.I5Z_INDEX.getExtension())) {
			return getI5ZReader(file);
		} else if (file.getName().endsWith(_FILE_TYPE.I5D_INDEX.getExtension())) {
			return getI5DReader(file);
		} else if (file.getName().endsWith(_FILE_TYPE.ZIP_INDEX.getExtension())) {
			return new ZipReader(file);
		} else if (file.getName().endsWith(_FILE_TYPE.GZ_INDEX.getExtension())) {
			return new ZipReader(file);
		}
		return getReader(new FileInputStream(file), file.getName(), format);
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws AmbitIOException
	 * @throws CDKException
	 */
	public static IIteratingChemObjectReader getI5ZReader(File file)
			throws FileNotFoundException, AmbitIOException, CDKException {
		try {
			Class clazz = Class.forName("net.idea.i5.io.I5ZReader");
			Constructor constructor = clazz.getConstructor(File.class);
			return (IIteratingChemObjectReader) constructor.newInstance(file);
		} catch (Exception x) {
			return new ZipReader(file);
		}
	}

	/**
	 * 
	 * @param in
	 * @return
	 * @throws FileNotFoundException
	 * @throws AmbitIOException
	 * @throws CDKException
	 */
	public static IIteratingChemObjectReader getI5DReader(InputStream in)
			throws AmbitIOException, CDKException {
		try {
			Class clazz = Class.forName("net.idea.i5.io.I5DReader");
			Constructor constructor = clazz.getConstructor(InputStream.class);
			return (IIteratingChemObjectReader) constructor.newInstance(in);
		} catch (Exception x) {
			return new I5ReaderSimple(new InputStreamReader(in));
		}
	}

	public static IIteratingChemObjectReader getI5DReader(File file)
			throws FileNotFoundException, AmbitIOException, CDKException {
		try {
			Class clazz = Class.forName("net.idea.i5.io.I5DReader");
			Constructor constructor = clazz.getConstructor(File.class);
			return (IIteratingChemObjectReader) constructor.newInstance(file);
		} catch (Exception x) {
			return new I5ReaderSimple(new FileReader(file));
		}
	}

	public static IRawReader<IStructureRecord> getRawReader(InputStream stream,
			String extension, IChemFormat format) throws CDKException,
			AmbitIOException {
		String ext = extension.toLowerCase();
		if (_FILE_TYPE.SDF_INDEX.hasExtension(ext)) {
			return new RawIteratingSDFReader(new InputStreamReader(stream));
		} else if (_FILE_TYPE.MOL_INDEX.hasExtension(ext)) {
			return new RawIteratingSDFReader(new InputStreamReader(stream));
		} else if (_FILE_TYPE.CSV_INDEX.hasExtension(ext)) {
			try {
				if ((format != null) && (format instanceof DelimitedFileFormat))
					return new RawIteratingCSVReader(stream,
							(DelimitedFileFormat) format);
				else
					return new RawIteratingCSVReader(stream, CSVFormat.EXCEL);
			} catch (CDKException x) {
				throw x;
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		} else if (_FILE_TYPE.TXT_INDEX.hasExtension(ext)
				|| _FILE_TYPE.TSV_INDEX.hasExtension(ext)) {
			try {
				if ((format != null) && (format instanceof DelimitedFileFormat))
					return new RawIteratingCSVReader(stream,
							(DelimitedFileFormat) format);
				else
					return new RawIteratingCSVReader(stream,
							CSVFormat.TDF.withCommentMarker('#'));
			} catch (CDKException x) {
				throw x;
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		} else
			throw new AmbitIOException(MSG_UNSUPPORTEDFORMAT + ext);
	}

	public static IIteratingChemObjectReader getReader(InputStream stream,
			String extension, IChemFormat format) throws AmbitIOException,
			CDKException {
		String ext = extension.toLowerCase();
		if (_FILE_TYPE.SDF_INDEX.hasExtension(ext)) {
			return new InteractiveIteratingMDLReader(stream,
					SilentChemObjectBuilder.getInstance(), true);
		} else if (_FILE_TYPE.SMI_INDEX.hasExtension(ext)) {
			return new IteratingSMILESReader(stream,
					SilentChemObjectBuilder.getInstance());
		} else if (_FILE_TYPE.CSV_INDEX.hasExtension(ext)) {
			try {
				IteratingDelimitedFileReader reader = null;
				if ((format != null) && (format instanceof DelimitedFileFormat))
					reader = new IteratingDelimitedFileReader(stream,
							(DelimitedFileFormat) format);
				else
					reader = new IteratingDelimitedFileReader(stream,
							new DelimitedFileFormat(",", '"'));
				return reader;
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		} else if (_FILE_TYPE.TXT_INDEX.hasExtension(ext)
				|| _FILE_TYPE.TSV_INDEX.hasExtension(ext)) {
			try {
				if ((format != null) && (format instanceof DelimitedFileFormat)) {
					DelimitedFileFormat df = (DelimitedFileFormat) format;
					if (df.fieldDelimiter.equals("\t")
							&& (df.textDelimiter == '"'))
						return new ToxcastAssayReader(stream);
					else
						return new IteratingDelimitedFileReader(stream,
								(DelimitedFileFormat) format);
				} else
					return new ToxcastAssayReader(stream);
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		} else if (ext.endsWith(_FILE_TYPE.MOL_INDEX.getExtension())) {
			// return new IteratingChemObjectReaderWrapper(new
			// FilteredMDLReader(stream));
			return new IteratingChemObjectReaderWrapper(new MDLReader(stream));

		} else if (_FILE_TYPE.INCHI_INDEX.hasExtension(ext)) {
			return new IteratingChemObjectReaderWrapper(new INChIReader(stream));
		} else if (_FILE_TYPE.CML_INDEX.hasExtension(ext)) {
			return new IteratingChemObjectReaderWrapper(new CMLReader(stream));
		} else if (_FILE_TYPE.HIN_INDEX.hasExtension(ext)) {
			return new IteratingChemObjectReaderWrapper(new HINReader(stream));
		} else if (_FILE_TYPE.PDB_INDEX.hasExtension(ext)) {
			return new RawIteratingPDBReader(new InputStreamReader(stream));
		} else if ((ext.toLowerCase().indexOf("euras") >= 0)
				&& (ext.endsWith(_FILE_TYPE.XLS_INDEX.getExtension()))) {
			try {
				return createReaderByReflection(
						"ambit2.core.io.bcf.EurasBCFReader", stream);
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		} else if (ext.endsWith(_FILE_TYPE.XLS_INDEX.getExtension())) {
			try {
				return createXLSXReaderByReflection(
						"ambit2.core.io.IteratingXLSReader", stream, 0, true);
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		} else if (ext.endsWith(_FILE_TYPE.XLSX_INDEX.getExtension())) {
			try {
				return createXLSXReaderByReflection(
						"ambit2.core.io.IteratingXLSReader", stream, 0, false);
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		} else if (ext.endsWith(_FILE_TYPE.ECHAXML_INDEX.getExtension())) {
			return new ECHAPreregistrationListReader(stream);
		} else if (ext.endsWith(_FILE_TYPE.TOXML_INDEX.getExtension())) {
			return new ToXMLReaderSimple(stream);
		} else if (ext.endsWith(_FILE_TYPE.MALARIA_HTS_SHEETS.getExtension())) {
			return new MalariaHTSDataDelimitedReader(stream);
		} else if (ext.endsWith(_FILE_TYPE.ZIP_INDEX.getExtension())) {
			return new ZipReader(stream);
		} else if (ext.endsWith(_FILE_TYPE.I5D_INDEX.getExtension())) {
			return getI5DReader(stream);
		} else if (ext.endsWith(_FILE_TYPE.I5D_INDEX.getExtension())) {
			return new ZipReader(stream);
		} else if (ext.endsWith(_FILE_TYPE.NANOCMLx_INDEX.getExtension())
				|| ext.endsWith(_FILE_TYPE.NANOCMLd_INDEX.getExtension()))
			try {
				return createReaderByReflection(
						"net.idea.ambit2.rest.nano.NanoCMLIteratingReader",
						stream);
			} catch (Exception x) {
				throw new AmbitIOException(x);
			}
		else
			throw new AmbitIOException(MSG_UNSUPPORTEDFORMAT + ext);
	}

	private static IIteratingChemObjectReader createXLSXReaderByReflection(
			String className, InputStream stream, int sheetIndex, boolean hssf)
			throws Exception {
		Class clazz = FileInputState.class.getClassLoader()
				.loadClass(className);
		Constructor<? extends Runnable> constructor = clazz
				.getConstructor(new Class[] { InputStream.class, Integer.class,
						Boolean.class });
		Object o = constructor.newInstance(new Object[] { stream, sheetIndex,
				hssf });
		return (DefaultIteratingChemObjectReader) o;
	}

	private static IIteratingChemObjectReader createReaderByReflection(
			String className, InputStream stream) throws Exception {
		Class clazz = FileInputState.class.getClassLoader()
				.loadClass(className);
		Constructor<? extends Runnable> constructor = clazz
				.getConstructor(InputStream.class);
		Object o = constructor.newInstance(stream);
		return (DefaultIteratingChemObjectReader) o;
	}
}
