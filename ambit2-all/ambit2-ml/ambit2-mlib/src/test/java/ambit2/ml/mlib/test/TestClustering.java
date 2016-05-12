package ambit2.ml.mlib.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.StreamingKMeans;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.streaming.Minutes;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.dstream.ConstantInputDStream;
import org.junit.Test;

/**
 * 
 * @author nina
 * 
 */
public class TestClustering {

	String dir = "file:///f:/spark";
	String train = "test10000.hashed.csv";
	String results = "results.csv";

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
		//int k = 100;
		//int k= 86876;
		int k = 741;
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

		model.predictOn(teststream).saveAsTextFiles(dir + "/results/results20",
				"txt");

		scc.start();
		scc.awaitTermination();
		scc.close();

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
