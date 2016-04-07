package ambit2.core.io.fp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.fingerprint.ICountFingerprint;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;

import ambit2.base.data.Property;

public class MultiFingerprintsWriter extends DefaultChemObjectWriter {
	protected String id;
	private HashMap<String, CSVPrinter> writers = new HashMap<String, CSVPrinter>();
	protected File prefix;
	protected CSVFormat CSVFORMAT_SPACE = CSVFormat.newFormat(' ')
			.withRecordSeparator("\r\n");
	protected String[] tags_to_keep;
	protected Object[] tags;

	public MultiFingerprintsWriter(File prefix, List<IFingerprinter> fps,
			String[] tags_to_keep) {
		super();
		this.tags_to_keep = tags_to_keep == null ? new String[] { Property.opentox_InChIKey }
				: tags_to_keep;
		tags = new Object[tags_to_keep.length];
		this.prefix = prefix;
		String[] suffix = new String[] { "", ".hashed", ".count", ".raw" };
		for (IFingerprinter fp : fps) {
			for (String s : suffix) {
				String property = String.format("%s%s",
						fp.getClass().getName(), s);
				writers.put(property, null); // will create writers as needed
			}
		}
	}

	/*
	 * public MultiFileWriter(File dir, String id, List<String> properties) {
	 * super(); for (String property : properties) try { writers.put(property,
	 * new CSVPrinter(new PrintWriter(new File( dir, property + ".csv")),
	 * CSVFormat.DEFAULT)); } catch (Exception x) { x.printStackTrace(); } }
	 */
	protected CSVPrinter getWriter(String property) {
		CSVPrinter csvprinter = writers.get(property);
		if (csvprinter == null) {

			boolean vw = property.endsWith("count");

			File file = new File(String.format("%s%s%s.%s",
					prefix.getAbsolutePath(), prefix.isDirectory() ? "/" : "",
					property, vw ? "vw" : "csv"));
			try {
				csvprinter = new CSVPrinter(new PrintWriter(file),
						property.endsWith("raw") ? CSVFormat.TDF : property
								.endsWith("hashed") ? CSVFormat.DEFAULT
								: vw ? CSVFORMAT_SPACE : CSVFormat.DEFAULT);
				writers.put(property, csvprinter);
				return csvprinter;
			} catch (IOException x) {
				Logger.getGlobal().log(Level.WARNING, x.getMessage());
			}
		}
		return csvprinter;
	}

	@Override
	public void write(IChemObject mol) throws CDKException {
		for (int k = 0; k < tags.length; k++)
			tags[k] = null;
		Iterator<Entry<Object, Object>> i = mol.getProperties().entrySet()
				.iterator();
		while (i.hasNext()) {
			Entry<Object, Object> entry = i.next();
			String p = entry.getKey() instanceof Property ? ((Property) entry
					.getKey()).getName() : entry.getKey().toString();
			int index = Arrays.binarySearch(tags_to_keep, p);
			if (index >= 0)
				tags[index] = entry.getValue();
		}

		i = mol.getProperties().entrySet().iterator();
		while (i.hasNext()) {
			Entry<Object, Object> entry = i.next();
			try {
				Object[] record = mol2record(tags, entry.getKey().toString(),
						entry.getValue());

				if (record != null) {
					CSVPrinter writer = getWriter(entry.getKey().toString());
					if (writer != null)
						writer.printRecord(record);
				}
			} catch (Exception x) {
				Logger.getGlobal().log(Level.WARNING, x.getMessage());
			}
		}
	}

	protected Object[] mol2record(Object[] tags, String propertyname,
			Object propertyvalue) {
		int offset = tags.length;
		Object[] record = null;
		boolean writecounts = propertyname.endsWith(".count");
		if (propertyvalue instanceof IBitFingerprint) {
			BitSet bitset = ((IBitFingerprint) propertyvalue).asBitSet();

 
			/*//not sparse!
			 * record = new Object[bitset.size() + offset]; for (int i = 0; i <
			 * bitset.size(); i++) { record[i + offset] = bitset.get(i) ? 1 : 0;
			 * }
			 */
			//this is sparse output
			record = new Object[bitset.cardinality()+offset];
			int i = 0;
			for (int b = bitset.nextSetBit(0); b >= 0; b = bitset
					.nextSetBit(b + 1)) {
				record[i + offset] = b;
				i++;
			}

		} else if (propertyvalue instanceof ICountFingerprint) {
			ICountFingerprint cfp = (ICountFingerprint) propertyvalue;
			int numBins = cfp.numOfPopulatedbins();
			record = new Object[numBins + offset];
			// StringBuilder b_hashed = new StringBuilder();
			// StringiBulder b_count = new StringBuilder();
			// StringBuilder b_fp = new StringBuilder();
			for (int i = 0; i < numBins; i++) {
				if (writecounts)
					record[i + offset] = String.format("%d:%d", cfp.getHash(i),
							cfp.getCount(i));
				else
					record[i + offset] = cfp.getHash(i);
			}
		} else if (propertyvalue instanceof Map) { // rawfingerprint
			Map map = ((Map) propertyvalue);
			record = new Object[map.size() + offset];
			Iterator<Entry<Object, Object>> entries = ((Map) propertyvalue)
					.entrySet().iterator();
			int i = 0;
			while (entries.hasNext()) {
				Entry<Object, Object> entry = entries.next();
				record[i + offset] = String.format("%s:%s", entry.getKey(),
						entry.getValue());
				i++;
			}
		} else
			return null;
		if (record != null)
			if (propertyname.endsWith("count"))
				for (int k = 0; k < tags.length; k++)
					record[k] = tags[k] + "|";
			else
				for (int k = 0; k < tags.length; k++)
					record[k] = tags[k];

		return record;
	}

	@Override
	public void setWriter(Writer writer) throws CDKException {

	}

	@Override
	public void setWriter(OutputStream writer) throws CDKException {

	}

	@Override
	public IResourceFormat getFormat() {
		return null;
	}

	@Override
	public boolean accepts(Class<? extends IChemObject> classObject) {
		return false;
	}

	@Override
	public void close() throws IOException {
		Iterator<Entry<String, CSVPrinter>> i = writers.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, CSVPrinter> entry = i.next();
			if (entry.getValue() != null)
				try {
					entry.getValue().close();
				} catch (Exception x) {

				}
		}
	}

}
