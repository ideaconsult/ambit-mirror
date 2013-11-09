package ambit2.descriptors.geometrical;


import java.util.List;

import javax.vecmath.Point3d;

import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;

/**
 * Charge-related Geometrical Index (CGI
 * TODO: reference
 *
 */

public class CGIDescriptor 
{
	public DescriptorSpecification getSpecification()
	{
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"CGIDescriptor"),
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
		int n = container.getAtomCount();
		double [][] R = new double [n][n];

		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				IAtom a = container.getAtom(i);
				IAtom a1 = container.getAtom(j);
				Point3d p = a.getPoint3d();
				Point3d p1 = a1.getPoint3d();
				R[i][j] = p.distance(p1);
			}
		}

		return R;
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

