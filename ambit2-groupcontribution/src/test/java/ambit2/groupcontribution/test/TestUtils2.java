package ambit2.groupcontribution.test;

import java.util.ArrayList;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.Calculator;
import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.descriptors.LDHNum;
import ambit2.smarts.SmartsHelper;


public class TestUtils2 
{
	public static void main(String[] args) throws Exception
	{
		//Set a model
		GroupContributionModel model = new GroupContributionModel();
		model.setModelType(GroupContributionModel.Type.ATOMIC);
		ArrayList<ILocalDescriptor> locDescr = new ArrayList<ILocalDescriptor>(); 
		locDescr.add(new LDAtomSymbol());
		locDescr.add(new LDHNum());
		model.setLocalDescriptors(locDescr);
		
		testGroupCount("CC(C)CNCN", model);
		
	}
	
	public static void testGroupCount(String smiles, GroupContributionModel model) throws Exception
	{
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles);		
		Map<String,Integer> groups = Calculator.getGroupsCount(mol, model);
		System.out.println("Group counts for " + smiles);
		for (Map.Entry<String, Integer> entry : groups.entrySet())		
			System.out.println(entry.getKey() + "   " + entry.getValue());
		
		
		System.out.println();
	}
}
