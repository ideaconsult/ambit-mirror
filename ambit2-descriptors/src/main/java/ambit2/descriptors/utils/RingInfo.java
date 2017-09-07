package ambit2.descriptors.utils;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.SmartsParser;

public class RingInfo 
{
	public static final String moleculeProperty = "RingInfoMoleculeProperty"; 
	
	public static void setRingInfo(IAtomContainer mol)
	{
		if (mol.getProperty(moleculeProperty) == null)
		{	
			mol.setProperty(moleculeProperty, "Ring info is set");
			SmartsParser.setRingData(mol, true, true);
		}
	}
}
