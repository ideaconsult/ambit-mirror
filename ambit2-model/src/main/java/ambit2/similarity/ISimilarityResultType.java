package ambit2.similarity;

public interface ISimilarityResultType extends Comparable<ISimilarityResultType>{
	double getAverageSimilarity();
	double getPairwiseSimilarity();
	void setAverageSimilarity(double value);
	void setPairwiseSimilarity(double value);
}
