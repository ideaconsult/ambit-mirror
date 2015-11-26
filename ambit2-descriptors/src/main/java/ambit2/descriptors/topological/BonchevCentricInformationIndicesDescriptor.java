package ambit2.descriptors.topological;

import java.util.HashMap;
import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.descriptors.utils.GraphMatrices;
import ambit2.descriptors.utils.TopologicalUtilities;

public class BonchevCentricInformationIndicesDescriptor implements
		IMolecularDescriptor {
	public String[] getDescriptorNames() {
		return new String[] { "ICR", "DDCI", "DCCI", "CCI", "GRCII", "GDDCI",
				"GDCCI", "GCCI" };
	}

	public String[] getParameterNames() {
		return null;
	}

	public Object getParameterType(String arg0) {
		return null;
	}

	public Object[] getParameters() {
		return null;
	}

	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification("", this.getClass().getName(),
				" .java", "University of Plovdiv, 2013");
	}

	public void setParameters(Object[] arg0) throws CDKException {
	}

	@Override
	public void initialise(IChemObjectBuilder arg0) {

	}

	public DescriptorValue calculate(IAtomContainer container) {
		if (container == null) {
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), new DoubleResult(Double.NaN),
					getDescriptorNames(), new CDKException(
							"The supplied AtomContainer was NULL"));
		}

		if (container.getAtomCount() == 0) {
			return new DescriptorValue(
					getSpecification(),
					getParameterNames(),
					getParameters(),
					new DoubleResult(Double.NaN),
					getDescriptorNames(),
					new CDKException(
							"The supplied AtomContainer did not have any atoms"));
		}

		double ICR = 0.0; // radial centric information index
		double DDCI = 0.0; // distance degree centric index
		double DCCI = 0.0; // distance code centric index
		double CCI = 0.0; // copmlete centric index
		double GRCII = 0.0; // generalized radial centric information index
		double GDDCI = 0.0; // generalized distance degree centric index
		double GDCCI = 0.0; // generalized distance code centric index
		double GCCI = 0.0; // generalized complete centrix index

		int n = container.getAtomCount();
		int[][] D = GraphMatrices.getTopDistanceMatrix(container);
		int[] Veec = GraphMatrices.getVertexEccentricity(D);
		int[] DDV = TopologicalUtilities.DistanceDegreeVector(container);

		HashMap<Integer, Integer> vdeg = new HashMap<Integer, Integer>();

		for (int i = 0; i < n; i++) {
			Integer key = Veec[i];

			if (vdeg.containsKey(key)) {
				Integer value = vdeg.get(key);
				value = value + 1;
				vdeg.put(key, value);
			} else
				vdeg.put(key, new Integer(1));
			// System.out.println("vdeg1 = " +vdeg);

		}

		Iterator<Integer> keySetIterator = vdeg.keySet().iterator();

		while (keySetIterator.hasNext()) {
			Integer key = keySetIterator.next();

			double sum = vdeg.get(key);

			ICR -= (sum / n) * ((Math.log10(sum / n) / Math.log10(2)));
		}

		HashMap<Integer, Integer> vdeg1 = new HashMap<Integer, Integer>();

		for (int i = 0; i < n; i++) {
			Integer key = Veec[i];
			Integer key1 = DDV[i];

			if (vdeg1.containsKey(key1)) {
				{
					Integer value = vdeg1.get(key1);
					value = value + 1;
					vdeg1.put(key1, value);
				}
				if (vdeg1.containsKey(key)) {
					Integer value = vdeg1.get(key);
					value = value + 1;
					vdeg1.put(key, value);
				}
			} else
				vdeg1.put(key1, new Integer(1));
			System.out.println("vdeg1 = " + vdeg1);
		}

		Iterator<Integer> keySetIterator1 = vdeg1.keySet().iterator();

		while (keySetIterator1.hasNext()) {
			Integer key = keySetIterator1.next();

			double sum = vdeg1.get(key);

			DDCI -= (sum / n) * ((Math.log10(sum / n) / Math.log10(2)));
		}

		HashMap<Integer, Integer> vdeg2 = new HashMap<Integer, Integer>();

		for (int i = 0; i < n; i++) {
			Integer key = Veec[i];
			Integer key1 = DDV[i];

			if (vdeg2.containsKey(key1)) {
				{
					Integer value = vdeg2.get(key1);
					value = value + 1;
					vdeg2.put(key1, value);
				}
				if (vdeg2.containsKey(key)) {
					Integer value = vdeg2.get(key);
					value = value + 1;
					vdeg2.put(key, value);
				}
			} else
				vdeg1.put(key1, new Integer(1));
			// System.out.println("vdeg2 = " +vdeg2);
		}

		Iterator<Integer> keySetIterator2 = vdeg2.keySet().iterator();

		while (keySetIterator2.hasNext()) {
			Integer key = keySetIterator2.next();

			double sum = vdeg2.get(key);

			DCCI -= (sum / n) * ((Math.log10(sum / n) / Math.log10(2)));
		}

		DoubleArrayResult result = new DoubleArrayResult();
		result.add(ICR);
		result.add(DDCI);
		result.add(DCCI);

		return new DescriptorValue(getSpecification(), getParameterNames(),
				getParameters(), result, getDescriptorNames());
	}

	public IDescriptorResult getDescriptorResultType() {
		return new DoubleArrayResult(0);
	}

}
