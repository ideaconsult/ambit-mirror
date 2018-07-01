package ambit2.groupcontribution.test;

import java.util.List;

import org.openscience.cdk.tools.LoggingTool;

import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.fragmentation.Fragmentation;
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
	
	void checkFragmentation(GroupContributionModel gcm, List<String> smiles, 
			List<String> expectedFragmentsDesignations, List<int[]> expectedFragFrequences) throws Exception
	{
		
		DataSet trainDataSet = DataSet.makeDataSet(smiles, null);
		Fragmentation.makeFragmentation(trainDataSet, gcm);
		
		//TODO
	}
	
	
	
}
