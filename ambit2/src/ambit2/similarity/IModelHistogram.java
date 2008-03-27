package ambit2.similarity;

import java.util.Enumeration;

import ambit2.stats.datastructures.Bin;
import ambit2.stats.datastructures.Histogram;

public interface IModelHistogram<U extends Bin> {
	public Histogram<U> getHistogram(Comparable dataset, int mode);
	public Enumeration<Comparable> getDatasets();
}
