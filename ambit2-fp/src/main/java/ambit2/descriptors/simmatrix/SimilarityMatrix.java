package ambit2.descriptors.simmatrix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimilarityMatrix {
	protected String delimiter = ",";
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	protected Logger logger;

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public SimilarityMatrix() {
	}

	protected L parseLineDense(String line, long record) {
		String[] tokens = line.split(delimiter);
		L l = new L();
		l.setKey(tokens[0]);
		l.setId(Long.toString(record));
		BitSet bs = new BitSet();
		for (int i = 1; i < tokens.length; i++) {
			if ("1".equals(tokens[i]))
				bs.set(i - 1);
		}
		l.setBs(bs);
		return l;
	}

	protected L parseLineSparse(String line, long record) {
		String[] tokens = line.split(delimiter);
		L l = new L();
		l.setKey(tokens[0]);
		l.setId(Long.toString(record));
		BitSet bs = new BitSet();
		for (int i = 1; i < tokens.length; i++)
			try {
				bs.set(Integer.parseInt(tokens[i]));
			} catch (Exception x) {
				logger.log(Level.WARNING, x.getMessage());
			}
		l.setBs(bs);
		return l;
	}

	public long[] createMatrix(String path, boolean dense, double threshold)
			throws Exception {
		return createMatrix(path, dense, threshold, 0, -1);
	}

	public long[] createMatrix(String path, boolean dense, double threshold,
			int page, int pagesize) throws Exception {

		final int startRecord = pagesize > 0 ? (page * pagesize) : 0;
		int maxRecord = pagesize > 0 ? ((page + 1) * pagesize) : -1;

		if (logger == null)
			logger = Logger.getLogger(getClass().getName());
		List<L> b = new ArrayList<L>();
		long now = System.currentTimeMillis();

		File[] files;
		File folder = new File(path);
		if (folder.isDirectory())
			files = folder.listFiles();
		else {
			if (!folder.exists()) throw new Exception("File do not exists");
			files = new File[] { folder };
			path = folder.getParent();
			folder = folder.getParentFile();
		}

		double tt[] = new double[] { 0, 0.25, 0.5, 0.75, 0.8, 0.9, 0.95, 1 };
		long histogram[] = new long[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		for (File file : files) {
			if (!file.getPath().endsWith(".csv") && !file.getPath().endsWith(".txt") && !file.getPath().endsWith(".tsv") ) {
				logger.log(Level.WARNING, String.format("%s format not supported.", file.getName()));
				continue;
			}	
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				String line;
				int r = 0;
				while ((line = reader.readLine()) != null) {
					r++;
					b.add(dense ? parseLineDense(line,r) : parseLineSparse(line,
							r));
				}
			} finally {
				reader.close();
			}
			logger.info(String.format("File %s read in %s msec.",
					file.getName(), System.currentTimeMillis() - now));

			now = System.currentTimeMillis();

			if (startRecord >= b.size())
				throw new Exception(String.format(
						"Start record %d > number of records %d", startRecord,
						b.size()));
			if (maxRecord > b.size() || (maxRecord < 0)) {
				maxRecord = b.size();
				logger.info(String.format(
						"End record %d set to number of records %d", maxRecord,
						b.size()));
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					String.format("%s/%s_%s_%d_%d.matrix", path,
							file.getName(), threshold, (startRecord + 1),
							maxRecord))));
			try {

				for (int i = startRecord; i < maxRecord; i++) {
					String id1 = b.get(i).getId();
					BitSet bs1 = b.get(i).getBs();
					for (int j = i + 1; j < b.size(); j++) {
						L item2 = b.get(j);
						double t = tanimoto(bs1, item2.getBs());
						for (int k = 0; k < tt.length; k++)
							if (t <= tt[k]) {
								histogram[k]++;
								break;
							}
						if (t >= threshold) {
							writer.write(String.format("%s\t%s\t%4.2f\n",
									id1, item2.getId(), t));
						}
					}
					if ((i % 5000) == 0) {
						logger.log(Level.INFO, String.format(
								"%d\t%s msec\tHistogram: %s", i,
								(System.currentTimeMillis() - now) / (i + 1 - startRecord),
								histogram2string(histogram, tt)));
					}
					writer.flush();
				}
			} finally {
				writer.close();
				logger.info(String
						.format("Similarity matrix threshold>=%f for file %s generated in %s msec.\tHistogram: %s",
								threshold, file.getName(),
								System.currentTimeMillis() - now,
								histogram2string(histogram, tt)));
			}
		}
		return histogram;
	}

	protected String histogram2string(long histogram[], double tt[]) {
		StringBuilder bb = new StringBuilder();
		for (int k = 0; k < histogram.length; k++)
			bb.append(String.format("%s:%s\t", tt[k], histogram[k]));
		return bb.toString();
	}

	class L {
		String key;
		String id;
		BitSet bs;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public BitSet getBs() {
			return bs;
		}

		public void setBs(BitSet bs) {
			this.bs = bs;
		}
	}

	protected static double tanimoto(BitSet bitset1, BitSet bitset2) {
		double a = bitset1.cardinality();
		double b = bitset2.cardinality();

		BitSet common = ((BitSet) bitset1.clone());
		common.and(bitset2);
		double c = common.cardinality();
		return c / (a + b - c);
	}
}
