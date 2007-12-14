package ambit.similarity;

import java.util.Enumeration;

import ambit.stats.datastructures.Bin;
import ambit.stats.datastructures.Histogram;

public interface IModelHistogram<U extends Bin> {
	public Histogram<U> getHistogram(Comparable dataset, int mode);
	public Enumeration<Comparable> getDatasets();
}
