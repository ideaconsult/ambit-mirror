package ambit2.ml.mlib.test;

import java.util.UUID;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.PowerIterationClustering;
import org.apache.spark.mllib.clustering.PowerIterationClusteringModel;
import org.apache.spark.mllib.clustering.StreamingKMeans;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.apache.spark.streaming.Minutes;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.dstream.ConstantInputDStream;
import org.junit.Test;

import scala.Tuple3;

/**
 * 
 * @author nina
 * 
 */
public class TestClustering extends TestSparkAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6978873485126084985L;
	@Test
	public void test() {

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[2]");

		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		JavaRDD<String> lines = jsc.textFile(dir + "/train").cache();
		JavaRDD<Vector> data = lines.map(new MyMapper());
		// streaming
		StreamingContext scx = new StreamingContext(ctx, Minutes.apply(1));
		JavaStreamingContext scc = new JavaStreamingContext(scx);

		ConstantInputDStream cis = new ConstantInputDStream(scc.ssc(),
				data.rdd(), null);
		// decay=1 use all data
		// int k = 100;
		int k = 86876;
		// int k = 741;
		StreamingKMeans model = new StreamingKMeans().setDecayFactor(1).setK(k)
				.setRandomCenters(1024, 0.5, 5L);

		model.trainOn(cis);
		double cost = model.latestModel().computeCost(data.rdd());
		System.out.println(cost);
		System.out.println(model.latestModel().k());

		JavaRDD<String> test = jsc.textFile(dir + "/train").cache();
		JavaRDD<Vector> datatest = test.map(new MyMapper());
		ConstantInputDStream teststream = new ConstantInputDStream(scc.ssc(),
				datatest.rdd(), null);

		model.predictOn(teststream).saveAsTextFiles(
				dir + String.format("/results/results%s", k), "txt");

		scc.start();
		scc.awaitTermination();
		scc.close();

	}

	@Test
	public void testPairs() {

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[2]");

		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		String source = "/train";
		JavaRDD<String> lines = jsc.textFile(dir + source).cache();
		JavaRDD<Vector> data = lines.map(new MyMapper());

		double[] colMags = new double[1024];
		for (int i = 0; i < colMags.length; i++)
			colMags[i] = 1;
		RowMatrix rows = new RowMatrix(data.rdd());
		double similarity = 0.5;
		double gamma = 10 * Math.log(data.count()) / similarity;
		CoordinateMatrix simmatrix = rows.columnSimilaritiesDIMSUM(colMags,
				gamma);
		System.out.println(simmatrix.numRows());
		JavaRDD<Tuple3<Long, Long, Double>> similarities = simmatrix.entries()
				.toJavaRDD()
				.map(new Function<MatrixEntry, Tuple3<Long, Long, Double>>() {
					public Tuple3<Long, Long, Double> call(MatrixEntry entry) {
						return new Tuple3(entry.i(), entry.j(), entry.value());
					}
				}).cache();

		PowerIterationClustering pic = new PowerIterationClustering();
		int k = 86877;
		pic.setK(k);
		pic.setInitializationMode("random"); // random or degree
		PowerIterationClusteringModel model = pic.run(similarities);

		String path = String.format("%s/results%s/pic%d/%s", dir, source, k,
				UUID.randomUUID());
		model.assignments().saveAsTextFile(path);
		/*
		 * for (PowerIterationClustering.Assignment a : model.assignments()
		 * .toJavaRDD().collect()) { System.out.println(a.id() + "->" +
		 * a.cluster()); }
		 */
		model.save(jsc.sc(), path);

		jsc.close();

	}
}

