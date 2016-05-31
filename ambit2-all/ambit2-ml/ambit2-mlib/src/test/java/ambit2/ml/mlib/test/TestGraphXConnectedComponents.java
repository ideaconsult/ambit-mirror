package ambit2.ml.mlib.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.lib.ConnectedComponents;
import org.apache.spark.mllib.clustering.StreamingKMeans;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.GroupedData;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.junit.Test;

import scala.reflect.ClassTag;

public class TestGraphXConnectedComponents extends TestSparkAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4476244628638514275L;

	@Test
	public void testConnectedComponents() throws Exception {
		String source = "/transposed/full";

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[*]");
		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);
		final int dim = 200000;
		// rows - fp, columns : cmps
		JavaRDD<Vector> data = jsc.textFile(dir + source).map(
				new Function<String, Vector>() {
					@Override
					public Vector call(String line) throws Exception {
						String[] tokens = line.split("\t");

						List<scala.Tuple2<Integer, Double>> items = new ArrayList<scala.Tuple2<Integer, Double>>();
						// skip ids
						for (int i = 1; i < tokens.length; i++) {
							int id = Integer.parseInt(tokens[i]);
							items.add(new scala.Tuple2<Integer, Double>(id, 1.0));
						}

						return Vectors.sparse(dim, items);
					}
				});
		RowMatrix rows = new RowMatrix(data.rdd());
		CoordinateMatrix simmatrix = rows.columnSimilarities(0.75);

		JavaRDD<Edge<Double>> similarities = simmatrix.entries().toJavaRDD()
				.map(new Function<MatrixEntry, Edge<Double>>() {
					public Edge<Double> call(MatrixEntry entry) {
						return new Edge<Double>(entry.i(), entry.j(), entry
								.value());
					}
				}).cache();
		ClassTag<String> vd = scala.reflect.ClassTag$.MODULE$
				.apply(String.class);
		ClassTag<Double> ed = scala.reflect.ClassTag$.MODULE$
				.apply(Double.class);
		Graph<String, Double> graph = Graph.fromEdges(similarities.rdd(),
				"node", StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(),
				vd, ed);
		Graph<Object, Double> cc = ConnectedComponents.run(graph, vd, ed);

		String path = String.format("%s/results%s/%s", dir, source,
				UUID.randomUUID());

		cc.vertices().saveAsTextFile(path);
		jsc.close();
	}

	public void testConnectedComponents_withpivot() throws Exception {
		// SLOW!
		String source = "/toy";

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[*]");

		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		JavaRDD<Row> data = jsc.textFile(dir + "/numbered" + source).flatMap(
				new MapperRow());

		SQLContext sqlContext = new SQLContext(jsc);
		DataFrame frame = sqlContext.createDataFrame(data.rdd(), schema);
		// frame.show();
		// records as columns
		GroupedData gd = frame.groupBy("fpnum");
		DataFrame df = gd.pivot("recordnum").count();
		// df.show();
		/**
		 * <pre>
		 * +-----------+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
		 * |      fpnum|  1|  2|  3|  4|  5|  6|  7|  8|  9| 10| 11| 12| 13| 14| 15| 16| 17| 18| 19| 20| 21| 22| 23| 24| 25| 26| 27| 28| 29| 30| 31| 32| 33| 34| 35| 36| 37| 38| 39| 40| 41| 42| 43| 44| 45| 46| 47| 48| 49| 50| 51| 52| 53| 54| 55| 56| 57| 58| 59| 60| 61| 62| 63| 64| 65| 66| 67| 68| 69| 70| 71| 72| 73| 74| 75| 76| 77| 78| 79| 80| 81| 82| 83| 84| 85| 86| 87| 88| 89| 90| 91| 92| 93| 94| 95| 96| 97| 98| 99|100|
		 * +-----------+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
		 * | 1597377831|  0|  1|  0|  0|  0|  0|  0|  0|  0|  1|  0|  0|  0|  0|  0|  0|  0|  0|  1|  0|  0|  1|  0|  0|  0|  0|  0|  1|  0|  0|  0|  0|  0|  0|  0|  1|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  1|  0|  0|  0|  1|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  1|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  1|  0|  0|  0|  0|  0|  0|  0|  1|  0|
		 * | -983709832|  0|  0|  0|  1|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|  0|
		 * 
		 * </pre>
		 */
		JavaRDD<Vector> fpasrows = df.rdd().toJavaRDD()
				.map(new Function<Row, Vector>() {
					public Vector call(Row entry) {
						List<scala.Tuple2<Integer, Double>> items = new ArrayList<scala.Tuple2<Integer, Double>>();
						for (int i = 1; i < entry.length(); i++) {
							Long c = entry.getLong(i);
							if (c > 0)
								items.add(new scala.Tuple2<Integer, Double>(
										i - 1, 1.0));
						}
						return Vectors.sparse(entry.length() - 1, items);
					}
				});
		double similarity = 0.75;

		RowMatrix rows = new RowMatrix(fpasrows.rdd());
		CoordinateMatrix simmatrix = rows.columnSimilarities(similarity);

		String path = String.format("%s/numbered/results%s/similarity/%s", dir,
				source, UUID.randomUUID());
		simmatrix.entries().saveAsTextFile(path);

		System.out.println(String.format("rows:%s\tcols%s",
				simmatrix.numRows(), simmatrix.numCols()));

		JavaRDD<Edge<Double>> similarities = simmatrix.entries().toJavaRDD()
				.map(new Function<MatrixEntry, Edge<Double>>() {
					public Edge<Double> call(MatrixEntry entry) {
						return new Edge<Double>(entry.i(), entry.j(), entry
								.value());
					}
				}).cache();
		ClassTag<String> vd = scala.reflect.ClassTag$.MODULE$
				.apply(String.class);
		ClassTag<Double> ed = scala.reflect.ClassTag$.MODULE$
				.apply(Double.class);
		Graph<String, Double> graph = Graph.fromEdges(similarities.rdd(),
				"node", StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(),
				vd, ed);
		Graph<Object, Double> cc = ConnectedComponents.run(graph, vd, ed);

		path = String.format("%s/numbered/results%s/graphx/%s", dir, source,
				UUID.randomUUID());
		cc.triplets().saveAsTextFile(path);

		/*
		 * String path = String.format("%s/numbered/results%s/graphx/%s", dir,
		 * source, UUID.randomUUID()); System.out.println(path);
		 * 
		 * data.saveAsTextFile(path);
		 */
		// RowMatrix rows = new RowMatrix(data.rdd());
		jsc.close();
	}

	class MapperRow implements FlatMapFunction<String, Row> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4483636401894466192L;

		@Override
		public Iterable<Row> call(String line) {
			List<Row> rows = new ArrayList<Row>();
			String[] tokens = line.split("\t");
			Long id = Long.parseLong(tokens[0].trim());
			for (int i = 3; i < tokens.length; i++) {
				rows.add(new GenericRowWithSchema(new Object[] {
						Long.parseLong(tokens[i].trim()), id }, schema));
			}

			return rows;
		}
	}

	@Override
	protected StructType getSchema() {
		StructField[] fields = new StructField[2];
		fields[0] = DataTypes.createStructField("fpnum", DataTypes.LongType,
				false);
		fields[1] = DataTypes.createStructField("recordnum",
				DataTypes.LongType, false);
		return new StructType(fields);

	}

}
