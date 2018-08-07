package ambit2.groupcontribution.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.tools.LoggingTool;

import ambit2.groupcontribution.GCMParser;
import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.dataset.DataSetObject;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.fragmentation.Fragmentation;
import ambit2.groupcontribution.groups.IGroup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestFragmentation extends TestCase
{
	public LoggingTool logger;
	
	public TestFragmentation() 
	{   
		logger = new LoggingTool(this);		
	}	
	
	public static Test suite() {
		return new TestSuite(TestFragmentation.class);
	}
	
	GroupContributionModel createGCM (
			GroupContributionModel.Type gcmType,
			String localDescriptors
			) throws Exception
	{
		GroupContributionModel gcm = new GroupContributionModel();
		gcm.setTargetEndpoint("test_property");
		gcm.setModelType(gcmType);
		//System.out.println("GCM type : " + gcm.getModelType().toString());		
		GCMParser gcmParser = new GCMParser();
		
		List<ILocalDescriptor> locDescriptors = gcmParser.getLocalDescriptorsFromString(localDescriptors);
		if (!gcmParser.getErrors().isEmpty())
		{
			System.out.println("Errors:\n" + gcmParser.getAllErrorsAsString());
			return null;
		}
		else
			gcm.setLocalDescriptors(locDescriptors);
		
		return gcm;
	}
	
	void checkFragmentation(GroupContributionModel gcm, List<String> smiles, 
			String[] expectedFragmentsDesignations, List<int[]> expectedFragFrequences) throws Exception
	{
		
		DataSet trainDataSet = DataSet.makeDataSet(smiles, null, "test_property");
		Fragmentation.makeFragmentation(trainDataSet, gcm);
		
		Map<String,IGroup> groups = gcm.getGroups();
		int n = groups.keySet().size();
		String groupsArray[] = groups.keySet().toArray(new String[n]);
		Arrays.sort(groupsArray);
		
		assertEquals("Number of fragments: ", expectedFragmentsDesignations.length, groupsArray.length);
		
		//Check gcm groups
		for (int i = 0; i < groupsArray.length; i++)
		{	
			assertEquals("Fragment " + (i+1) + " designation: ", 
					expectedFragmentsDesignations[i], groupsArray[i]);			
		}
		
		//Check group frequencies
		for (int i = 0; i < smiles.size(); i++)
		{
			DataSetObject dso =  trainDataSet.dataObjects.get(i);
			int[] expFreqs = expectedFragFrequences.get(i);
			
			int freq = 0;
			for (int k = 0; k < groupsArray.length; k++)
			{	
				Integer iObj = dso.fragmentation.groupFrequencies.get(groupsArray[k]);
				if (iObj == null)
					freq = 0;
				else
					freq = iObj.intValue();
				
				assertEquals("Mol #" + (i+1) + " " + smiles.get(i) + 
						", frag #"  + (k+1) + " " + groupsArray[k] + ", frequency: ", 
						expFreqs[k], freq);					
			}			
		}		
	}
	
	public void test01() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,H");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C1","C2","C3"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CCC");
		expFragFreq.add(new int[] {0,1,2});
		smiles.add("CCCC");
		expFragFreq.add(new int[] {0,2,2});
		smiles.add("CCCCC");
		expFragFreq.add(new int[] {0,3,2});
		smiles.add("CC(C)C");
		expFragFreq.add(new int[] {1,0,3});
		
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
		
	
}
