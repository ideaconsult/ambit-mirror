package ambit2.ml.mlib.test;

import java.util.BitSet;
import java.util.UUID;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.PartitionStrategy;
import org.apache.spark.graphx.lib.ConnectedComponents;
import org.apache.spark.mllib.clustering.StreamingKMeans;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.storage.StorageLevel;
import org.junit.Test;

import scala.Option;
import scala.Tuple2;
import scala.Tuple3;
import scala.reflect.ClassTag;

public class TestGraphX extends TestSparkAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4476244628638514275L;

	@Test
	public void testConnectedComponents() throws Exception {
		String source = "/train";

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[*]");

		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		JavaRDD<Tuple3<Long, BitSet, String>> data = jsc.textFile(dir + source)
				.cache().map(new MyMapperBitset());

		JavaPairRDD<Tuple3<Long, BitSet, String>, Tuple3<Long, BitSet, String>> pairs = data
				.cartesian(data);

		final Double threshold = 0.9;
		JavaRDD<Edge<Double>> similarity = pairs
				.map(new Function<Tuple2<Tuple3<Long, BitSet, String>, Tuple3<Long, BitSet, String>>, Edge<Double>>() {
					@Override
					public Edge<Double> call(
							Tuple2<Tuple3<Long, BitSet, String>, Tuple3<Long, BitSet, String>> v1)
							throws Exception {
						BitSet b1 = v1._1._2();
						BitSet b2 = v1._2._2();
						double tanimoto = tanimoto(b1, b2);
						return new Edge<Double>(v1._1._1(), v1._2._1(),
								tanimoto);
					}
				}).filter(new Function<Edge<Double>, Boolean>() {
					@Override
					public Boolean call(Edge<Double> v1) throws Exception {
						return v1.attr() >= threshold;
					}
				});

		ClassTag<String> vd = scala.reflect.ClassTag$.MODULE$
				.apply(String.class);
		ClassTag<Double> ed = scala.reflect.ClassTag$.MODULE$
				.apply(Double.class);

		Graph<String, Double> graph = Graph.fromEdges(similarity.rdd(), "node",
				StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(), vd, ed);

		Graph<Object, Double> cc = ConnectedComponents.run(graph, vd, ed);

		String path = String.format("%s/results%s/graphx/%s", dir, source,
				UUID.randomUUID());
		cc.triplets().saveAsTextFile(path);
		jsc.close();
	}


	protected DataFrame getDataFrame(JavaSparkContext jsc, String source) {
		SQLContext sqlContext = new SQLContext(jsc);
		JavaRDD<Row> data = jsc.textFile(dir + source).cache()
				.map(new MyMapperRow());
		return sqlContext.createDataFrame(data.rdd(), schema);
	}

	protected JavaRDD<Vector> getRDD(JavaSparkContext jsc, String source) {
		return jsc.textFile(dir + source).cache().map(new MyMapper());
	}

	protected JavaRDD<Row> getRDDRow(JavaSparkContext jsc, String source) {
		return jsc.textFile(dir + source).cache().map(new MyMapperRow());
	}

	@Test
	public void testConnectedComponents_bad() throws Exception {

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[*]");

		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		String source = "/toy";
		JavaRDD<Vector> data = getRDD(jsc, source);

		double[] colMags = new double[1024];
		for (int i = 0; i < colMags.length; i++)
			colMags[i] = 1;
		RowMatrix rows = new RowMatrix(data.rdd());
		double similarity = 0.75;
		double gamma = 10 * Math.log(data.count()) / similarity;
		CoordinateMatrix simmatrix = rows.columnSimilaritiesDIMSUM(colMags,
				gamma);
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

		String path = String.format("%s/results%s/graphx/%s", dir, source,
				UUID.randomUUID());
		cc.triplets().saveAsTextFile(path);

		jsc.close();
		
	}
}
