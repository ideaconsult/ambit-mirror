package ambit2.base.io.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import net.idea.modbcum.i.processors.IProcessor;

public abstract class MatrixMarketReaderAbstract<T, V> {

	public void readMatrixMarket(Path pathmatrix,int maxrecords, IProcessor<T, T> processor) throws Exception {

		long nrows = -1;
		long ncols = -1;
		long npairs = -1;
		long currentrow = -1;
		T doc = null;
		StringBuilder b = null;
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(pathmatrix, StandardCharsets.UTF_8);
			String line = null;
			int r = 0;

			boolean sparse = true;
			while ((line = reader.readLine()) != null) {
				r++;
				if (line.startsWith("%%")) {
					sparse = isSparse(line);
					if (!sparse)
						throw new Exception("Only coordinate format supported so far!");
				} else if (line.startsWith("%")) {
					// comment, skip
					continue;
				} else if (r == 3) {
					try {
						String[] tokens = line.split(" ");
						if (tokens.length < 3)
							throw new Exception("Invalid 3rd row");
						nrows = Long.parseLong(tokens[0]);
						ncols = Long.parseLong(tokens[1]);
						npairs = Long.parseLong(tokens[2]);
					} catch (Exception x) {
						throw x;
					}
				} else {
					String[] tokens = line.split(" ");
					if (tokens.length < 3)
						throw new Exception("Invalid line " + r + "\t" + line);

					int row = Integer.parseInt(tokens[0]);
					int col = Integer.parseInt(tokens[1]);
					// ignore value for now
					V value;
					try {
						value = parseValue(tokens[2]);
					} catch (Exception x) {
						value = null;
					}
					if (currentrow != row || doc == null) {
						if (doc != null) {
							recordReady(doc);
							processor.process(doc);
						}	

						b = new StringBuilder();
						currentrow = row;
						doc = createRecord(row);
					}
					addField(doc, row, col, value);
				}

				if ((maxrecords > 0) && (r > maxrecords))
					break;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			recordReady(doc);
			processor.process(doc);
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		System.out.println(String.format("%s\t%s\t%s", nrows, ncols, npairs));
		return;
	}

	protected boolean isSparse(String line) {
		return line.contains("coordinate");
	}

	abstract T createRecord(int id);

	abstract void addField(T doc, int row, int col, V value);

	abstract V parseValue(String token) throws Exception;

	abstract void recordReady(T record);

}
