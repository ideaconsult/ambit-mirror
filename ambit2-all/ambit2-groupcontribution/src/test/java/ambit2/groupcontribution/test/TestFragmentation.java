package ambit2.groupcontribution.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.tools.LoggingTool;

import ambit2.groupcontribution.GCMParser;
import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.dataset.DataSet;
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
		System.out.println("GCM type : " + gcm.getModelType().toString());		
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
		
		DataSet trainDataSet = DataSet.makeDataSet(smiles, null);
		Fragmentation.makeFragmentation(trainDataSet, gcm);
		
		Map<String,IGroup> groups = gcm.getGroups();
		int n = groups.keySet().size();
		String groupsArray[] = groups.keySet().toArray(new String[n]);
		for (String s: groupsArray) 
			System.out.println(s);
		assertEquals("Number of fragments: ", expectedFragmentsDesignations.length, groupsArray.length);
		
		//TODO
	}
	
	public void test01() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,H");
		List<String> smiles = new ArrayList<String>(); 
		smiles.add("CCC");
		
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C2","C3"};
		
		//checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);
		
	}
	
	
	
		
	
}
