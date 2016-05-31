package ambit2.ml.mlib.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import junit.framework.Assert;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.lib.ConnectedComponents;
import org.apache.spark.mllib.clustering.PowerIterationClustering;
import org.apache.spark.mllib.clustering.PowerIterationClusteringModel;
import org.apache.spark.mllib.clustering.StreamingKMeans;
import org.apache.spark.storage.StorageLevel;
import org.junit.Test;

import scala.Tuple3;
import scala.reflect.ClassTag;

public class TestMatrix extends TestSparkAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 798662674656104974L;

	@Test
	public void clusterSimilarityMatrix() throws Exception {
		long now = System.currentTimeMillis();
		String source = "/matrix_0.25";

		SparkConf conf = new SparkConf().setAppName(
				ConnectedComponents.class.getName()).setMaster("local[*]");
		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		final double threshold = 0.95;
		JavaRDD<Edge<Double>> data = getMatrixAsRDD(jsc, source, threshold);

		ClassTag<String> vd = scala.reflect.ClassTag$.MODULE$
				.apply(String.class);
		ClassTag<Double> ed = scala.reflect.ClassTag$.MODULE$
				.apply(Double.class);
		Graph<String, Double> graph = Graph.fromEdges(data.rdd(), "node",
				StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(), vd, ed);
		Graph<Object, Double> cc = ConnectedComponents.run(graph, vd, ed);

		UUID uuid = UUID.randomUUID();
		String path = String.format("%s/results%s/%s-%s.triplets", dir, source,
				uuid, threshold);
		cc.triplets().saveAsTextFile(path);
		path = String.format("%s/results%s/%s-%s.vertices", dir, source, uuid,
				threshold);
		cc.vertices().saveAsTextFile(path);

		jsc.close();
		System.out.println(System.currentTimeMillis() - now);
	}

	protected JavaRDD<Edge<Double>> getMatrixAsRDD(JavaSparkContext jsc,
			String source, final double threshold) {
		JavaRDD<Edge<Double>> data = jsc.textFile(dir + source)
				.filter(new Function<String, Boolean>() {
					@Override
					public Boolean call(String line) throws Exception {
						String[] tokens = line.split("\t");
						Double attr = Double.parseDouble(tokens[2]);
						return attr >= threshold;
					}
				}).map(new Function<String, Edge<Double>>() {
					@Override
					public Edge<Double> call(String line) throws Exception {
						String[] tokens = line.split("\t");

						Double attr = Double.parseDouble(tokens[2]);
						long srcId;
						long dstId;
						if (tokens[0].indexOf("CHEMBL") >= 0)
							srcId = Integer.parseInt(tokens[0].replace(
									"CHEMBL", ""));
						else
							srcId = Long.parseLong(tokens[0]);
						if (tokens[1].indexOf("CHEMBL") >= 0)
							dstId = Integer.parseInt(tokens[1].replace(
									"CHEMBL", ""));
						else
							dstId = Long.parseLong(tokens[1]);
						Edge<Double> edge = new Edge<Double>(srcId, dstId, attr);
						return edge;
					}
				});
		return data;
	}

	@Test
	public void clusterByPIC() throws Exception {
		long now = System.currentTimeMillis();
		String source = "/matrix_0.25";

		SparkConf conf = new SparkConf().setAppName(
				StreamingKMeans.class.getName()).setMaster("local[*]");
		SparkContext ctx = SparkContext.getOrCreate(conf);
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(ctx);

		final double threshold = 0.95;
		JavaRDD<Tuple3<Long, Long, Double>> data = jsc.textFile(dir + source)
				.filter(new Function<String, Boolean>() {
					@Override
					public Boolean call(String line) throws Exception {
						String[] tokens = line.split("\t");
						Double attr = Double.parseDouble(tokens[2]);
						return attr >= threshold;
					}
				}).map(new Function<String, Tuple3<Long, Long, Double>>() {
					@Override
					public Tuple3<Long, Long, Double> call(String line)
							throws Exception {
						String[] tokens = line.split("\t");

						Double attr = Double.parseDouble(tokens[2]);
						long srcId = Integer.parseInt(tokens[0].replace(
								"CHEMBL", ""));
						long dstId = Integer.parseInt(tokens[1].replace(
								"CHEMBL", ""));
						Tuple3<Long, Long, Double> tuple = new Tuple3<Long, Long, Double>(
								srcId, dstId, attr);
						return tuple;
					}
				});

		int k = 10000;
		PowerIterationClustering pic = new PowerIterationClustering();
		pic.setK(k);
		pic.setInitializationMode("random"); // random or degree
		PowerIterationClusteringModel model = pic.run(data);

		// Save and load model

		UUID uuid = UUID.randomUUID();
		String path = String.format("%s/results%s/%s-%s-%s.pic", dir, source,
				uuid, threshold, k);
		model.assignments().saveAsTextFile(path);
		// model.save(ctx, path);

		jsc.close();
		System.out.println(System.currentTimeMillis() - now);
	}

	protected L parseLineDense(String line) {
		String[] tokens = line.split(",");
		Assert.assertEquals(1026, tokens.length);
		L l = new L();
		l.setKey(tokens[0]);
		l.setId(tokens[1]);
		BitSet bs = new BitSet();
		for (int i = 2; i < tokens.length; i++) {
			if ("1".equals(tokens[i]))
				bs.set(i - 2);
		}
		l.setBs(bs);
		return l;
	}

	protected L parseLineSparse(String line, long record) {
		String[] tokens = line.split(",");
		L l = new L();
		l.setKey(tokens[0]);
		l.setId(Long.toString(record));
		BitSet bs = new BitSet();
		for (int i = 1; i < tokens.length; i++)
			try {
				bs.set(Integer.parseInt(tokens[i]));
			} catch (Exception x) {
				logger_cli.log(Level.WARNING, x.getMessage());
			}
		l.setBs(bs);
		return l;
	}

	@Test
	public void testcreateMatrixSparse() throws Exception {
		String source = "/chembl";
		createMatrix(source, -1, false,0.25);
	}

	@Test
	public void testcreateMatrixDense() throws Exception {
		String source = "/train";
		createMatrix(source, 10, true,0.25);
	}

	public void createMatrix(String source, int max, boolean dense, double threshold)
			throws Exception {

		List<L> b = new ArrayList<L>();
		long now = System.currentTimeMillis();

		File folder = new File(dir_ + source);
		File[] files = folder.listFiles();

		for (File file : files) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				String line;
				int r = 0;
				while ((line = reader.readLine()) != null) {
					r++;
					b.add(dense ? parseLineDense(line) : parseLineSparse(line,r));
					if ((max > 0) && (r > max))
						break;
				}
			} finally {
				reader.close();
			}
			System.out.println(System.currentTimeMillis() - now);

			now = System.currentTimeMillis();

			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					String.format("%s%s/%s_%s.matrix", dir_, source,
							file.getName(), threshold))));

			for (int i = 0; i < b.size(); i++) {
				L item1 = b.get(i);
				for (int j = i + 1; j < b.size(); j++) {
					L item2 = b.get(j);
					double t = tanimoto(item1.getBs(), item2.getBs());
					if (t > threshold) {
						writer.write(String.format("%s\t%s\t%4.2f\n",
								item1.getId(), item2.getId(), t));
					}
				}
				if ((i % 1000) == 0) {
					System.out.println(i);
				}
				writer.flush();
			}
			writer.close();
			System.out.println(System.currentTimeMillis() - now);
		}
	}

	class L {
		String key;
		String id;
		BitSet bs;

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

		public BitSet getBs() {
			return bs;
		}

		public void setBs(BitSet bs) {
			this.bs = bs;
		}
	}
}
