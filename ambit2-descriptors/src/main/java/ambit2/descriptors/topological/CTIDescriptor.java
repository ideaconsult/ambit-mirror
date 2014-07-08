package ambit2.descriptors.topological;

import java.util.List;

import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;


/**
 * Charge-related Topological Index (CTI)
 * TODO: reference
 *
 */
public class CTIDescriptor {
	public DescriptorSpecification getSpecification()
	{
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"CTIDescriptor"),
				this.getClass().getName(),
				"$Id: CTIDescriptor.java, v 0.1 2013 Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}

	public void setParameters(Object[] params) throws CDKException
	{	
	}

	public Object[] getParameters()
	{
		return null;
	}

	public String[] getDescriptorNames() 
	{
		return new String[] {"CTI"};
	}

	public DescriptorValue calculate(IAtomContainer container) throws Exception
	{
		//Get partial charger
		//GasteigerPEPEPartialCharges partCharges = new GasteigerPEPEPartialCharges();	
		GasteigerMarsiliPartialCharges partCharges = new GasteigerMarsiliPartialCharges();	
		//partCharges.assignGasteigerPiPartialCharges(container, true);
		partCharges.assignGasteigerMarsiliSigmaPartialCharges(container, true);
		
		//Get topological distance matrix		
		double[][] matr = ConnectionMatrix.getMatrix(container);        
        int[][] D = PathTools.computeFloydAPSP(matr);
		double CTI = 0.0;
		double L[] =  getLocalIndexes(container);
		
		int n = container.getAtomCount();
		for (int i = 0; i < n; i++)		
			for (int j = i+1; j < n; j++)
			{	
				if (D[i][j] > 0)
					CTI += L[i]*L[j] / D[i][j];
			}	
		
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				new DoubleResult(CTI), getDescriptorNames());
	}
	
	double [] getLocalIndexes(IAtomContainer container)
	{
		double L[] = new double [container.getAtomCount()];
		for (int i = 0; i < container.getAtomCount(); i++) 
		{
        	IAtom a = container.getAtom(i);
        	int implH = 0;
        	if (a.getImplicitHydrogenCount() != null)
        		implH = a.getImplicitHydrogenCount();
        	
        	int nH = implH + getExplicitHCount(container, a);
        	int val = 1;
        	if (a.getValency() != null)
        		val = a.getValency();
        	
			L[i] = val - nH + a.getCharge() ;
        }
		return L;
	}
	
	int getExplicitHCount(IAtomContainer container, IAtom atom)
	{
		int explH = 0;
		List<IAtom> list = container.getConnectedAtomsList(atom);
		for (IAtom a: list)
			if (a.getSymbol().equals("H"))
				explH++;
		return explH;
	}
	
	public IDescriptorResult getDescriptorResultType()
	{
		return new DoubleResult(0.0);
	}

	public String[] getParameterNames() 
	{
		return null;
	}

	public Object getParameterType(String name) 
	{
		return "";
	}
}
