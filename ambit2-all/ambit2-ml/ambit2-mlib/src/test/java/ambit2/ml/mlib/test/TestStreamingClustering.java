package ambit2.ml.mlib.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;
import java.util.logging.Level;

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
public class TestStreamingClustering extends TestSparkAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6978873485126084985L;

	@Test
	public void test() throws Exception {

		String source = "/train";
		UUID uuid = UUID.randomUUID();
		String path_method = String.format("%s/results%s/StreamingKMeans",
				dir_, source);

		String path = String.format("file:///%s/%s", path_method, uuid);
		System.out.println(path);
		File file = new File(path_method, uuid.toString() + ".log");
		new File(path_method).mkdirs();
		System.out.println(file);
		file.createNewFile();
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file)));

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[*]");

		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		JavaRDD<String> lines = jsc.textFile(dir + "/train").cache();
		JavaRDD<Vector> data = lines.map(new MyMapper());
		// streaming
		StreamingContext scx = new StreamingContext(ctx, Minutes.apply(1));
		JavaStreamingContext scc = new JavaStreamingContext(scx);

		// decay=1 use all data
		//int[] kk = new int[] {2048, 1024, 512,256,128,64,32,16,8,4,2 };
		//int[] kk = new int[] { 4096, 16384,32768,65536,8192,131072};
		int[] kk = new int[] {  86876 };

		int k = 0;
		for (int j = 0; j < kk.length; j++)
			try {
				k = kk[j];
				ConstantInputDStream cis = new ConstantInputDStream(scc.ssc(),
						data.rdd(), null);				
				StreamingKMeans model = new StreamingKMeans().setDecayFactor(1)
						.setK(k).setRandomCenters(1024, 0.5, 5L);

				model.trainOn(cis);
				double cost = model.latestModel().computeCost(data.rdd());

				w.write(String.format("%s\t%s\n", k, cost));
				w.flush();
				cis = new ConstantInputDStream(scc.ssc(),
						data.rdd(), null);	
				model.predictOn(cis).saveAsTextFiles(
						String.format("%s/%s", path_method, k), "txt");
						
			} catch (Exception x) {
				logger_cli.log(Level.WARNING,
						String.format("Error on k=%d", k), x);
			}
		scc.start();
		scc.awaitTermination();
		scc.close();
		
		w.close();

	}

	
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

		model.save(jsc.sc(), path);

		jsc.close();

	}
}
