package ambit2.descriptors.topological;

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

public class EccentricBasedMadanIndicesDescriptor implements
		IMolecularDescriptor {
	public String[] getDescriptorNames() {
		return new String[] { "CSI", "EDS", "AEDS", "CEI" };
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
				" EccentricIndicesDescriptor.java",
				"University of Plovdiv, 2013");
	}

	public void setParameters(Object[] arg0) throws CDKException {
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
		int[] degree = TopologicalUtilities.getAtomTopDegrees(container);
		int[][] D = GraphMatrices.getTopDistanceMatrix(container);
		int[] Veec = GraphMatrices.getVertexEccentricity(D);
		int[] ddv = TopologicalUtilities.DistanceDegreeVector(container);
		int[][] AM = GraphMatrices.getAdjacencyMatrix(container);

		double CSI = 0.0; // this is the name by Dragon for Eccentric
							// connectivity index
		double EDC = 0.0; // Eccentric distance sum index
		double AEDS = 0.0; // Adjacency eccentric distance sum index
		double CEI = 0.0; // Connectivity eccentricity index
		double madanIndex = 1.0;

		for (int i = 0; i < container.getAtomCount(); i++) {
			double a = degree[i] * Veec[i];
			double b = Veec[i] * ddv[i];
			double c = 1.0 * (Veec[i] * ddv[i]) / degree[i];
			double d = 1.0 * degree[i] / Veec[i];

			CSI += a;
			EDC += b;
			AEDS += c;
			CEI += d;

			for (int j = 0; j < container.getAtomCount(); j++) {
				madanIndex *= Math.pow(degree[i], AM[i][j]);
			}
		}

		DoubleArrayResult result = new DoubleArrayResult();

		result.add(CSI);
		result.add(EDC);
		result.add(AEDS);
		result.add(CEI);

		return new DescriptorValue(getSpecification(), getParameterNames(),
				getParameters(), result, getDescriptorNames());
	}

	public IDescriptorResult getDescriptorResultType() {
		return new DoubleArrayResult(4);
	}

	@Override
	public void initialise(IChemObjectBuilder arg0) {

	}
}
