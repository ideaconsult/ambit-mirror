package ambit2.descriptors.simmatrix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ambit2.similarity.measure.IDistanceFunction;

public abstract class SimilarityMatrix<T> {
	protected Map<Integer, Integer> bitmap = new HashMap<Integer, Integer>();
	protected IDistanceFunction<T> distance;

	public IDistanceFunction<T> getDistance() {
		return distance;
	}

	public void setDistance(IDistanceFunction<T> distance) {
		this.distance = distance;
	}

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
		super();
	}

	protected abstract L parseLineSparse(String line, long record) throws Exception;

	protected abstract L parseLineDense(String line, long record) throws Exception;

	public long[] createMatrix(String path, boolean dense, float threshold) throws Exception {
		return createMatrix(path, dense, threshold, 0, -1);
	}

	protected String getSimMatrixFormat() {
		return "%s\t%s\t%4.2f\n";
	}

	public long[] createMatrix(String path, boolean dense, float threshold, int page, int pagesize) throws Exception {
		if (distance == null)
			throw new Exception("Distance not defined");
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
			if (!folder.exists())
				throw new Exception("File do not exists");
			files = new File[] { folder };
			path = folder.getParent();
			folder = folder.getParentFile();
		}
		double max = Double.NEGATIVE_INFINITY;
		double tt[] = new double[] { 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1, Double.POSITIVE_INFINITY };
		long histogram[] = new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (File file : files) {
			if (!file.getPath().endsWith(".csv") && !file.getPath().endsWith(".txt")
					&& !file.getPath().endsWith(".tsv")) {
				logger.log(Level.WARNING, String.format("%s format not supported.", file.getName()));
				continue;
			}
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				String line;
				int r = 0;
				while ((line = reader.readLine()) != null) {
					r++;
					L value = dense ? parseLineDense(line, r) : parseLineSparse(line, r);

					b.add(value);
					if ((r % 10000) == 0) {
						logger.log(Level.FINE, String.format("%s\t%s", r, bitmap.size()));
					}
				}
			} finally {
				reader.close();
			}
			logger.info(String.format("File %s read in %s msec.", file.getName(), System.currentTimeMillis() - now));

			now = System.currentTimeMillis();

			if (startRecord >= b.size())
				throw new Exception(String.format("Start record %d > number of records %d", startRecord, b.size()));
			if (maxRecord > b.size() || (maxRecord < 0)) {
				maxRecord = b.size();
				logger.info(String.format("End record %d set to number of records %d", maxRecord, b.size()));
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(String.format("%s/%s_%s_%d_%d.matrix",
					path, file.getName(), threshold, (startRecord + 1), maxRecord))));
			try {

				for (int i = startRecord; i < maxRecord; i++) {
					String id1 = b.get(i).getId();
					T bs1 = b.get(i).getValue();
					for (int j = i + 1; j < b.size(); j++) {
						L item2 = b.get(j);
						float t = distance.getDistance(bs1, item2.getValue());
						if (t > max)
							max = t;
						for (int k = 0; k < tt.length; k++)
							if (t <= tt[k]) {
								histogram[k]++;
								break;
							}
						if (accept(t, threshold)) {
							// if (t >= threshold) {
							writer.write(String.format(getSimMatrixFormat(), id1, item2.getId(), t));
						}
					}
					if ((i % 5000) == 0) {
						logger.log(Level.INFO,
								String.format("%d\t%s msec\tHistogram: %s", i,
										(System.currentTimeMillis() - now) / (i + 1 - startRecord),
										histogram2string(histogram, tt)));
					}
					writer.flush();
				}
			} finally {
				writer.close();
				logger.info(String.format(
						"Similarity matrix threshold %s %f for file %s generated in %s msec.\t%s\nmax=%e\tHistogram: %s",
						accept(threshold+1,threshold)?">=":"<=",threshold, file.getName(), System.currentTimeMillis() - now, getDistance().toString(),max,
						histogram2string(histogram, tt)));
			}
		}
		logger.info(String.format("Number of fingerprints %s", bitmap.size()));
		return histogram;
	}

	protected abstract boolean accept(float t, float threshold);

	public static String histogram2string(long histogram[], double tt[]) {
		StringBuilder bb = new StringBuilder();
		for (int i = 0; i < histogram.length - 1; i++)
			bb.append(String.format("%3.1f:%s\t", i * 0.1, histogram[i]));
		bb.append(String.format(">1:%s\t", histogram[histogram.length - 1]));

		return bb.toString();
	}

	class L {
		String key;
		String id;
		T value;

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

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

	}

}
