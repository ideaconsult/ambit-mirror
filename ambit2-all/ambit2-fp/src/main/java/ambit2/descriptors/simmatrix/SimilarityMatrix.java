package ambit2.descriptors.simmatrix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tdunning.math.stats.TDigest;

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

	public TDigest createMatrix(String path, boolean dense, float threshold) throws Exception {
		return createMatrix(path, dense, threshold, 0, -1);
	}

	protected String getSimMatrixFormat() {
		return "%s\t%s\t%4.2f\n";
	}

	public TDigest createMatrix(String path, boolean dense, float threshold, int page, int pagesize) throws Exception {
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
		final TDigest tdigest = TDigest.createDigest(1000);

		for (File file : files) {
			String statsfile = String.format("%s/%s_%s_%d_%d_%s", path, file.getName(), threshold, (startRecord + 1),
					maxRecord, toString());
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
			try (BufferedWriter writer = new BufferedWriter(
					new FileWriter(new File(String.format("%s/%s_%s_%d_%d_%s.matrix", path, file.getName(), threshold,
							(startRecord + 1), maxRecord, toString()))))) {

				for (int i = startRecord; i < maxRecord; i++) {
					String id1 = b.get(i).getId();
					T bs1 = b.get(i).getValue();
					for (int j = i + 1; j < b.size(); j++) {
						L item2 = b.get(j);
						float t = distance.getDistance(bs1, item2.getValue());
						tdigest.add(t);

						if (accept(t, threshold)) {
							// if (t >= threshold) {
							writer.write(String.format(getSimMatrixFormat(), id1, item2.getId(), t));
						}
					}
					if ((i % 1000) == 0) {
						logger.log(Level.INFO, String.format("%d\t%s msec\tHistogram: %s", i,
								(System.currentTimeMillis() - now) / (i + 1 - startRecord), histogram2string(tdigest)));
					}
					if ((i % 10000) == 0) {
						histogram2file(tdigest, statsfile + ".quantiles", statsfile + ".histogram");
					}
					writer.flush();
				}
			} finally {
				logger.info(String.format(
						"Similarity matrix threshold %s %f for file %s generated in %s msec.\t%s\nQuantiles: %s",
						accept(threshold + 1, threshold) ? ">=" : "<=", threshold, file.getName(),
						System.currentTimeMillis() - now, getDistance().toString(), histogram2string(tdigest)));
			}

			histogram2file(tdigest, statsfile + ".quantiles", statsfile + ".histogram");
		}
		if (bitmap.size() > 0)
			logger.info(String.format("Number of fingerprints %s", bitmap.size()));

		return tdigest;
	}

	protected void histogram2file(TDigest tdigest, String file_quantiles, String file_histogram) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file_quantiles)))) {
			writer.write(histogram2string(tdigest, 100, false, false));
		} catch (IOException x) {
			logger.warning(x.getMessage());
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file_histogram)))) {
			writer.write(histogram2string(tdigest, 20, false, true));
		} catch (IOException x) {
			logger.warning(x.getMessage());
		}
	}

	protected abstract boolean accept(float t, float threshold);

	public static String histogram2string(TDigest tdigest) {
		return histogram2string(tdigest, 4, false, false);
	}

	/**
	 * 
	 * @param tdigest
	 * @param n
	 * @param bounds
	 *            print bounds
	 * @param histogram
	 *            true ; quantiles : false
	 * @return
	 */
	public static String histogram2string(TDigest tdigest, int n, boolean bounds, boolean histogram) {
		StringBuilder bb = new StringBuilder();
		if (bounds)
			bb.append(String.format("\n[Min,Max]:\t[%e , %e]\n", tdigest.getMin(), tdigest.getMax()));

		if (histogram) {
			bb.append("x\tfraction\n");
			double step = (tdigest.getMax() - tdigest.getMin()) / n;
			for (int i = 1; i <= n; i++) {
				double x1 = tdigest.getMin() + (i - 1) * step;
				double x2 = tdigest.getMin() + i * step;
				bb.append(String.format("%e\t%e\n", x2, tdigest.cdf(x2) - tdigest.cdf(x1)));
			}
		} else {
			bb.append("#\tquantile\tcdf\n");

			for (int i = 0; i <= n; i++) {
				double q = (double) i / (double) n;
				double q2 = tdigest.quantile(q);
				bb.append(String.format("%d.\t%e\t%e\n", i + 1, q, q2));

			}
		}

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
