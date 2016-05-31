package ambit2.ml.mlib.test;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import scala.Tuple3;
import scala.util.MurmurHash;

public class TestSparkAbstract implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2144147103190243760L;
	static String dir_ = "f:/spark";
	static String dir = "file:///" + dir_;

	protected StructType schema = getSchema();

	protected static Logger logger_cli = Logger
			.getLogger(TestSparkAbstract.class.getName());
	static final String loggingProperties = "ambit2/ml/mlib/config/logging.properties";
	static final String log4jProperties = "ambit2/ml/mlib/config/log4j.properties";

	static {
		logger_cli = Logger.getLogger("ambitcli", "ambit2.ml.mlib.config.msg");
		Locale.setDefault(Locale.ENGLISH);
		String dOption = System.getProperty("java.util.logging.config.file");
		if (dOption == null || "".equals(dOption)) {
			InputStream in = null;
			try {
				in = TestSparkAbstract.class.getClassLoader()
						.getResourceAsStream(loggingProperties);
				LogManager.getLogManager().readConfiguration(in);

			} catch (Exception x) {
				logger_cli.log(Level.WARNING, x.getMessage());
			} finally {
				try {
					in.close();
				} catch (Exception x) {
				}
			}
		}
		// now log4j for those who use it
		InputStream in = null;
		try {
			in = TestSparkAbstract.class.getClassLoader().getResourceAsStream(
					log4jProperties);
			PropertyConfigurator.configure(in);

		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage());
		} finally {
			try {
				in.close();
			} catch (Exception x) {
			}
		}

	}

	class MyMapper implements Function<String, Vector> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4483636401894466192L;

		@Override
		public Vector call(String line) {
			String[] tokens = line.split(",");

			List<scala.Tuple2<Integer, Double>> items = new ArrayList<scala.Tuple2<Integer, Double>>();
			// skip ids
			int size = tokens.length - 2;
			for (int i = 2; i < tokens.length; ++i) {
				items.add(new scala.Tuple2<Integer, Double>(i - 2, Double
						.parseDouble(tokens[i])));
			}

			return Vectors.sparse(size, items);
		}
	}

	class MyMapperRow implements Function<String, Row> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4483636401894466192L;

		@Override
		public Row call(String line) {
			String[] tokens = line.split(",");
			Object[] values = new Object[tokens.length];
			values[0] = tokens[0];
			values[1] = tokens[1];
			for (int i = 2; i < tokens.length; ++i) {
				values[i] = Integer.parseInt(tokens[i]);
			}

			return new GenericRowWithSchema(values, schema);
		}
	}

	class MyMapperBitset implements
			Function<String, Tuple3<Long, BitSet, String>> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4483636401894466192L;

		@Override
		public Tuple3<Long, BitSet, String> call(String line) {

			String[] tokens = line.split(",");
			BitSet bitset = new BitSet(1024);
			for (int i = 2; i < tokens.length; ++i) {
				if (Integer.parseInt(tokens[i]) > 0)
					bitset.set(i - 2);
			}
			long id = MurmurHash.stringHash(tokens[1]);
			return new Tuple3<Long, BitSet, String>(id, bitset, tokens[1]);
		}
	}

	protected StructType getSchema() {
		StructField[] fields = new StructField[3];
		fields[0] = DataTypes.createStructField("inchi", DataTypes.StringType,
				false);
		fields[1] = DataTypes.createStructField("id", DataTypes.StringType,
				false);

		fields[2] = DataTypes.createStructField("bitset", DataTypes.StringType,
				false);
		return new StructType(fields);

	}

	protected static StructType getIntSchema() {
		StructField[] fields = new StructField[1026];
		fields[0] = DataTypes.createStructField("inchi", DataTypes.StringType,
				false);
		fields[1] = DataTypes.createStructField("id", DataTypes.StringType,
				false);
		for (int i = 0; i < 1024; i++)
			fields[2 + i] = DataTypes.createStructField(
					String.format("fp%d", i + 1), DataTypes.IntegerType, false);
		return new StructType(fields);

	}

	public static double tanimoto(BitSet bitset1, BitSet bitset2) {
		double a = bitset1.cardinality();
		double b = bitset2.cardinality();

		BitSet common = ((BitSet) bitset1.clone());
		common.and(bitset2);
		double c = common.cardinality();
		return c / (a + b - c);
	}
}
