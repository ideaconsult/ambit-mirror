package ambit2.descriptors.geometrical;


import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;

import java.util.List;



public class CGIDescriptor 
{
	public DescriptorSpecification getSpecification()
	{
		return new DescriptorSpecification(
				"Charge-related Geometrical Index (CGI)",
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
		return new String[] {"CGI"};
	}

	public DescriptorValue calculate(IAtomContainer container) throws Exception
	{
		//Get partial charger			
		GasteigerMarsiliPartialCharges partCharges = new GasteigerMarsiliPartialCharges();		
		partCharges.assignGasteigerMarsiliSigmaPartialCharges(container, true);
		
		//Get geometric distance matrix		
		double[][] R = getGeometricDistanceMatrix(container);
		double CTI = 0.0;
		double L[] =  getLocalIndexes(container);
		
		int n = container.getAtomCount();
		for (int i = 0; i < n; i++)		
			for (int j = i+1; j < n; j++)
			{	
				if (R[i][j] > 0)
					CTI += L[i]*L[j] / R[i][j];
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
	
	double [][] getGeometricDistanceMatrix(IAtomContainer container)
	{
		//TODO
		return null;
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

