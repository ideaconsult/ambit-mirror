package ambit2.ml.mlib.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.BisectingKMeans;
import org.apache.spark.mllib.clustering.BisectingKMeansModel;
import org.apache.spark.mllib.clustering.StreamingKMeans;
import org.apache.spark.mllib.linalg.Vector;
import org.junit.Test;

public class TestClustering extends TestSparkAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4494304392075642651L;

	@Test
	public void testBisectingKMeans() throws IOException {

		String source = "/train";
		UUID uuid = UUID.randomUUID();
		String path_method = String.format("%s/results%s/BisectingKMeans",
				dir_, source);

		String path = String.format("file:///%s/%s", path_method, uuid);

		File file = new File(path_method, uuid.toString() + ".log");
		System.out.println(file);
		file.createNewFile();
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file)));
		file = new File(path_method, uuid.toString() + "_cluster.txt");
		file.createNewFile();
		BufferedWriter clusters = new BufferedWriter(new FileWriter(file));
		clusters.write(String.format("%s\t%s\t%s\t%s\n", "Number of clusters",
				"Actual Number of clusters", "Center", "clusterCenter"));

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[*]");

		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		try {
			JavaRDD<String> lines = jsc.textFile(dir + source).cache();
			JavaRDD<Vector> data = lines.map(new MyMapper());
			int maxk = 30; // 86877;
			// int[] kk = new int[] { 5, 50, 200, 500, 1000, 2000, 5000, 10000,
			// 15000, 20000, 30000, 40000, 50000, 60000,70000, 80000, 90000,
			// 100000 };
			//int[] kk = new int[] { 10000, 100000 };
			int[] kk = new int[] { 50000 };
			// int[] kk = new int[] { 10000, 15000, 20000, 30000, 40000, 50000,
			// 60000,70000, 80000, 90000, 100000 };
			double cost;
			int k = 0;
			for (int j = 0; j < kk.length; j++)
				try {
					k = kk[j];

					BisectingKMeans bkm = new BisectingKMeans().setK(k);
					bkm.setMinDivisibleClusterSize(3);
					BisectingKMeansModel model = bkm.run(data);

					cost = model.computeCost(data);
					w.write(String.format("%s\t%s\n", k, cost));
					w.flush();
					Vector[] clusterCenters = model.clusterCenters();
					for (int i = 0; i < clusterCenters.length; i++) {
						Vector clusterCenter = clusterCenters[i];
						clusters.write(String.format("%d\t%d\t%d\t%s\n", k,
								clusterCenters.length, i, clusterCenter));
					}
					clusters.flush();
					Thread.yield();
					JavaRDD<Integer> results = model.predict(data);
					results.saveAsTextFile(String.format("%s/%d", path, k));
				} catch (Exception x) {
					logger_cli.log(Level.WARNING,
							String.format("Error on k=%d", k), x);
				}
		} catch (Exception x) {
			logger_cli.log(Level.WARNING, x.getMessage(), x);
		} finally {
			jsc.close();
			w.close();
			clusters.close();
		}

	}
}
