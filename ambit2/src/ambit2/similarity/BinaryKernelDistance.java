package ambit2.similarity;

import java.util.BitSet;

import sun.net.www.content.audio.x_aiff;

/**
 * Binary Kernel Discrimination method applied to fingerprints  (bitset).
 * <pre>
 * KL(i,j) ) ([L^(N-dij)]*[(1 - L)^dij])^(k/N)
 * where dij is the number of bits differing in fingerprint i and fingerprint j
 * </pre>
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public  class BinaryKernelDistance implements IDistanceFunction<BitSet> {
	protected double lambda = 0.9f;
	protected double k = 1;
	public BinaryKernelDistance() {
		super();
	}

	public float getDistance(BitSet object1, BitSet object2) throws Exception {
		return getNativeComparison(object1, object2);
	}

	public float getNativeComparison(BitSet object1, BitSet object2)
			throws Exception {
		
		BitSet difference = (BitSet)object1.clone();
		difference.xor(object2);
		float dij = difference.cardinality();
		float N = difference.size();
		return (float)Math.pow(
				Math.pow(lambda,N-dij)*Math.pow(1-lambda,dij),
				k/N);
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public double getK() {
		return k;
	}

	public void setK(float k) {
		this.k = k;
	}

	public void setLambda(float lambda) {
		this.lambda = lambda;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Binary kernel distance KL(i,j) ) ([L^(N-dij)]*[(1 - L)^dij])^(k/N) ; L="+lambda;
	}

}
