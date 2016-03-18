package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.ICiteable;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.processors.structure.MoleculeReader;

/**
 * CSV parser implemented using {@link org.apache.commons.csv.CSVParser}
 * 
 * @author nina
 * 
 */
public class RawIteratingCSVReader extends DefaultIteratingChemObjectReader
		implements IRawReader<IStructureRecord>, ICiteable {
	static Logger logger = Logger.getLogger(RawIteratingCSVReader.class
			.getName());
	protected CSVParser parser;
	protected Iterator<CSVRecord> iterator;
	protected IStructureRecord structureRecord;
	protected CSVFormat format;
	protected ILiteratureEntry reference;
	protected MoleculeReader molReader = null;
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

	private enum special_header {
		SMILES {
			@Override
			public Property getproperty() {
				return Property.getSMILESInstance();
			}
		},
		INCHI {
			@Override
			public Property getproperty() {
				return Property.getInChIInstance();
			}
		},
		INCHIKEY {
			@Override
			public Property getproperty() {
				return Property.getInChIKeyInstance();
			}
		};
		public abstract Property getproperty();
	}

	public RawIteratingCSVReader(Reader in) throws CDKException {
		this(in, CSVFormat.EXCEL);
	}

	public RawIteratingCSVReader(InputStream in, DelimitedFileFormat format)
			throws CDKException {
		this(new InputStreamReader(in), format);
	}

	public RawIteratingCSVReader(Reader in, DelimitedFileFormat format)
			throws CDKException {
		super();
		this.format = CSVFormat.DEFAULT.withDelimiter(
				format.getFieldDelimiter().charAt(0)).withQuote(
				format.getTextDelimiter());
		setReader(in);
	}

	public RawIteratingCSVReader(InputStream in, CSVFormat format)
			throws CDKException {
		this(new InputStreamReader(in), format);
	}

	public RawIteratingCSVReader(Reader in, CSVFormat format)
			throws CDKException {
		super();
		this.format = format;
		setReader(in);
	}

	@Override
	public void setReader(Reader reader) throws CDKException {
		try {
			parser = format.withHeader().parse(reader);
			iterator = parser.iterator();
			structureRecord = null;
		} catch (IOException x) {
			throw new CDKException(x.getMessage());
		}
	}

	@Override
	public void setReader(InputStream in) throws CDKException {
		setReader(new InputStreamReader(in));
	}

	@Override
	public IResourceFormat getFormat() {
		return new DelimitedFileFormat(
				Character.toString(format.getDelimiter()),
				format.getQuoteCharacter());
	}

	@Override
	public void close() throws IOException {
		if (parser != null)
			parser.close();
	}

	@Override
	public boolean hasNext() {
		structureRecord = null;
		return iterator.hasNext();
	}

	@Override
	public Object next() {
		return nextRecord();
		/*
		 * if (structureRecord == null) structureRecord =
		 * transform(iterator.next()); if (molReader == null) molReader = new
		 * MoleculeReader(); try { IAtomContainer mol =
		 * molReader.process(structureRecord); if (mol == null) mol = new
		 * AtomContainer(); for (Property p :
		 * structureRecord.getRecordProperties()) { Object v =
		 * structureRecord.getRecordProperty(p); if (v != null)
		 * mol.setProperty(p, v); } return mol; } catch (AmbitException x) {
		 * x.printStackTrace(); return new AtomContainer(); }
		 */
	}

	@Override
	public void setReference(ILiteratureEntry reference) {
		this.reference = reference;
	}

	@Override
	public ILiteratureEntry getReference() {
		return reference;
	}

	@Override
	public IStructureRecord nextRecord() {
		if (structureRecord == null)
			structureRecord = transform(iterator.next());
		return structureRecord;
	}

	private IStructureRecord transform(CSVRecord record) {
		structureRecord = new StructureRecord();
		Property[] special_props = new Property[special_header.values().length];

		String[] ids = new String[special_header.values().length];

		Iterator<Entry<String, Integer>> header = parser.getHeaderMap()
				.entrySet().iterator();
		while (header.hasNext()) {
			Entry<String, Integer> entry = header.next();
			String value= null;
			try {
				value = record.get(entry.getValue());
			} catch (Exception x) {
				logger.log(Level.WARNING, String.format("Error reading column %s %s", entry.getKey(),x.getClass().getName(),x.getMessage()));
			}
			if (value==null) continue;
			try {
				special_header h = special_header.valueOf(entry.getKey()
						.replace("_", "").toUpperCase());
				ids[h.ordinal()] = value == null || "".equals(value.trim()) ? null
						: value.trim();
				special_props[h.ordinal()] = h.getproperty();
				special_props[h.ordinal()].setOrder(entry.getValue());
				continue;
			} catch (Exception x) {
				if (optionalSMILESHeader != null
						&& entry.getKey().toUpperCase()
								.equals(optionalSMILESHeader)) {
					special_header h = special_header.SMILES;
					ids[h.ordinal()] = value == null || "".equals(value.trim()) ? null
							: value.trim();
					special_props[h.ordinal()] = h.getproperty();
					special_props[h.ordinal()].setOrder(entry.getValue());
				}
			}
			Property p = Property.getInstance(entry.getKey(), getReference());
			p.setOrder(entry.getValue());
			structureRecord.setRecordProperty(p, value);
		}
		if (ids[special_header.SMILES.ordinal()] != null) {
			structureRecord.setContent(ids[special_header.SMILES.ordinal()]);
			structureRecord.setFormat(MOL_TYPE.INC.name());
		} else if (ids[special_header.INCHI.ordinal()] != null) {
			structureRecord.setContent(ids[special_header.INCHI.ordinal()]);
			structureRecord.setFormat(MOL_TYPE.INC.name());
		} else {
			structureRecord.setContent(null);
			structureRecord.setFormat(null);
		}
		structureRecord.setSmiles(ids[special_header.SMILES.ordinal()]);
		structureRecord.setInchi(ids[special_header.INCHI.ordinal()]);
		structureRecord.setInchiKey(ids[special_header.INCHIKEY.ordinal()]);

		if (structureRecord.getInchi() != null)
			structureRecord.setRecordProperty(
					special_props[special_header.INCHI.ordinal()],
					structureRecord.getInchi());
		if (structureRecord.getInchiKey() != null)
			structureRecord.setRecordProperty(
					special_props[special_header.INCHIKEY.ordinal()],
					structureRecord.getInchiKey());
		if (structureRecord.getSmiles() != null)
			structureRecord.setRecordProperty(
					special_props[special_header.SMILES.ordinal()],
					structureRecord.getSmiles());
		return structureRecord;
	}

}
