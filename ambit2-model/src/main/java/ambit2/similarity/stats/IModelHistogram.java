package ambit2.similarity.stats;

import java.util.Enumeration;




public interface IModelHistogram<U extends Bin> {
	public Histogram<U> getHistogram(Comparable dataset, int mode);
	public Enumeration<Comparable> getDatasets();
}
