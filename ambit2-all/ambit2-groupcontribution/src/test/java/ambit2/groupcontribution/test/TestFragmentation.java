package ambit2.groupcontribution.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

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
	public ILoggingTool logger;
	
	public TestFragmentation() 
	{   
		logger = LoggingToolFactory.createLoggingTool(TestFragmentation.class);		
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
		gcm.setTargetProperty("test_property");
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
	
	public void test00() throws Exception 
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
	
	public void test01() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C","N","O","S"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CCN");
		expFragFreq.add(new int[] {2,1,0,0});
		smiles.add("CCO");
		expFragFreq.add(new int[] {2,0,1,0});
		smiles.add("CCS");
		expFragFreq.add(new int[] {2,0,0,1});
		smiles.add("C=CCN");
		expFragFreq.add(new int[] {3,1,0,0});
		smiles.add("C=CCO");
		expFragFreq.add(new int[] {3,0,1,0});
		smiles.add("CCCCS");
		expFragFreq.add(new int[] {4,0,0,1});
		smiles.add("CC(N)C");
		expFragFreq.add(new int[] {3,1,0,0});
		smiles.add("C1C(N)C1");
		expFragFreq.add(new int[] {3,1,0,0});
		smiles.add("CC(N)C(N)CCN");
		expFragFreq.add(new int[] {5,3,0,0});
		smiles.add("C1C(N)CCC(N)C1");
		expFragFreq.add(new int[] {6,2,0,0});
		
		smiles.add("CC(C)C");
		expFragFreq.add(new int[] {4,0,0,0});
		smiles.add("CC(=C)C");
		expFragFreq.add(new int[] {4,0,0,0});
		smiles.add("CC=CCC");
		expFragFreq.add(new int[] {5,0,0,0});
		smiles.add("CC(C)CC(C)C");
		expFragFreq.add(new int[] {7,0,0,0});
		smiles.add("C1CCCC1");
		expFragFreq.add(new int[] {5,0,0,0});
		smiles.add("C1CC=CC1");
		expFragFreq.add(new int[] {5,0,0,0});
		smiles.add("C1CC(C)CC1");
		expFragFreq.add(new int[] {6,0,0,0});
		smiles.add("C1CC(=C)CC1");
		expFragFreq.add(new int[] {6,0,0,0});
		smiles.add("C1C=C(C)CC1");
		expFragFreq.add(new int[] {6,0,0,0});
		smiles.add("CCCC1CC(C)CC1");
		expFragFreq.add(new int[] {9,0,0,0});
		smiles.add("CC(C)CC1CC(C)CC1");
		expFragFreq.add(new int[] {10,0,0,0});
		smiles.add("CC#C");
		expFragFreq.add(new int[] {3,0,0,0});
		smiles.add("CC(C)CC#C");
		expFragFreq.add(new int[] {6,0,0,0});
		smiles.add("C1CC1CC#C");
		expFragFreq.add(new int[] {6,0,0,0});
		
		smiles.add("CC(O)C");
		expFragFreq.add(new int[] {3,0,1,0});
		smiles.add("CC(=O)C");
		expFragFreq.add(new int[] {3,0,1,0});
		smiles.add("CC=CCO");
		expFragFreq.add(new int[] {4,0,1,0});
		smiles.add("CC(O)CC(C)C");
		expFragFreq.add(new int[] {6,0,1,0});
		smiles.add("C1COCC1");
		expFragFreq.add(new int[] {4,0,1,0});
		smiles.add("C1OC=CC1");
		expFragFreq.add(new int[] {4,0,1,0});
		smiles.add("C1CC(O)CC1");
		expFragFreq.add(new int[] {5,0,1,0});
		smiles.add("C1CC(=O)CC1");
		expFragFreq.add(new int[] {5,0,1,0});
		smiles.add("C1C=C(O)CC1");
		expFragFreq.add(new int[] {5,0,1,0});
		smiles.add("CCCC1CC(O)CC1");
		expFragFreq.add(new int[] {8,0,1,0});
		smiles.add("CC(O)CC1CC(O)CC1");
		expFragFreq.add(new int[] {8,0,2,0});
		smiles.add("OC#C");
		expFragFreq.add(new int[] {2,0,1,0});
		smiles.add("CC(O)CC#C");
		expFragFreq.add(new int[] {5,0,1,0});
		smiles.add("C1OC1CC#C");
		expFragFreq.add(new int[] {5,0,1,0});
		
		
		smiles.add("CC(N)C");
		expFragFreq.add(new int[] {3,1,0,0});
		smiles.add("CC(=N)C");
		expFragFreq.add(new int[] {3,1,0,0});
		smiles.add("CC=CCN");
		expFragFreq.add(new int[] {4,1,0,0});
		smiles.add("CC(N)CC(N)C");
		expFragFreq.add(new int[] {5,2,0,0});
		smiles.add("C1CNCC1");
		expFragFreq.add(new int[] {4,1,0,0});
		smiles.add("C1NC=CC1");
		expFragFreq.add(new int[] {4,1,0,0});
		smiles.add("C1CC(N)CC1");
		expFragFreq.add(new int[] {5,1,0,0});
		smiles.add("C1CC(=N)CC1");
		expFragFreq.add(new int[] {5,1,0,0});
		smiles.add("C1C=C(N)CC1");
		expFragFreq.add(new int[] {5,1,0,0});
		smiles.add("CCCC1CC(N)CC1");
		expFragFreq.add(new int[] {8,1,0,0});
		smiles.add("OC(C)CC1CC(N)CC1");
		expFragFreq.add(new int[] {8,1,1,0});
		smiles.add("NC#C");
		expFragFreq.add(new int[] {2,1,0,0});
		smiles.add("CC(N)CC#C");
		expFragFreq.add(new int[] {5,1,0,0});
		smiles.add("NC1CC1CC#C");
		expFragFreq.add(new int[] {6,1,0,0});
		
		smiles.add("CC(S)C");
		expFragFreq.add(new int[] {3,0,0,1});
		smiles.add("CC(=S)C");
		expFragFreq.add(new int[] {3,0,0,1});
		smiles.add("CC=CCS");
		expFragFreq.add(new int[] {4,0,0,1});
		smiles.add("CC(S)CC(S)C");
		expFragFreq.add(new int[] {5,0,0,2});
		smiles.add("C1CSCC1");
		expFragFreq.add(new int[] {4,0,0,1});
		smiles.add("C1SC=CC1");
		expFragFreq.add(new int[] {4,0,0,1});
		smiles.add("C1CC(S)CC1");
		expFragFreq.add(new int[] {5,0,0,1});
		smiles.add("C1CC(=S)CC1");
		expFragFreq.add(new int[] {5,0,0,1});
		smiles.add("C1C=C(S)CC1");
		expFragFreq.add(new int[] {5,0,0,1});
		smiles.add("CCCC1CC(S)CC1");
		expFragFreq.add(new int[] {8,0,0,1});
		smiles.add("CC(S)CC1CC(S)CC1");
		expFragFreq.add(new int[] {8,0,0,2});
		smiles.add("SC#C");
		expFragFreq.add(new int[] {2,0,0,1});
		smiles.add("CC(S)CC#C");
		expFragFreq.add(new int[] {5,0,0,1});
		smiles.add("C1SC1CC#C");
		expFragFreq.add(new int[] {5,0,0,1});
		
		
		smiles.add("NC(O)C");
		expFragFreq.add(new int[] {2,1,1,0});
		smiles.add("SC(=O)C");
		expFragFreq.add(new int[] {2,0,1,1});
		smiles.add("SC=CCO");
		expFragFreq.add(new int[] {3,0,1,1});
		smiles.add("NC(S)CC(O)C");
		expFragFreq.add(new int[] {4,1,1,1});
		smiles.add("C1C(CN)CC(=S)CC1");
		expFragFreq.add(new int[] {7,1,0,1});
		
		
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	
	
	public void test02_01() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,H");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C0","C1","C2","C3","N1","N2","O0","O1","S1"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CCN");
		expFragFreq.add(new int[] {0,0,1,1,0,1,0,0,0});
		smiles.add("CCO");
		expFragFreq.add(new int[] {0,0,1,1,0,0,0,1,0});
		smiles.add("CCS");
		expFragFreq.add(new int[] {0,0,1,1,0,0,0,0,1});
		smiles.add("C=CCN");
		expFragFreq.add(new int[] {0,1,2,0,0,1,0,0,0});
		smiles.add("C=CCO");
		expFragFreq.add(new int[] {0,1,2,0,0,0,0,1,0});
		smiles.add("CCCCS");
		expFragFreq.add(new int[] {0,0,3,1,0,0,0,0,1});
		smiles.add("CC(N)C");
		expFragFreq.add(new int[] {0,1,0,2,0,1,0,0,0});
		smiles.add("C1C(N)C1");
		expFragFreq.add(new int[] {0,1,2,0,0,1,0,0,0});
		smiles.add("CC(N)C(N)CCN");
		expFragFreq.add(new int[] {0,2,2,1,0,3,0,0,0});
		smiles.add("C1C(N)CCC(N)C1");
		expFragFreq.add(new int[] {0,2,4,0,0,2,0,0,0});
		
		smiles.add("CC(C)C");
		expFragFreq.add(new int[] {0,1,0,3,0,0,0,0,0});
		smiles.add("CC(=C)C");
		expFragFreq.add(new int[] {1,0,1,2,0,0,0,0,0});
		smiles.add("CC=CCC");
		expFragFreq.add(new int[] {0,2,1,2,0,0,0,0,0});
		smiles.add("CC(C)CC(C)C");
		expFragFreq.add(new int[] {0,2,1,4,0,0,0,0,0});
		smiles.add("C1CCCC1");
		expFragFreq.add(new int[] {0,0,5,0,0,0,0,0,0});
		smiles.add("C1CC=CC1");
		expFragFreq.add(new int[] {0,2,3,0,0,0,0,0,0});
		smiles.add("C1CC(C)CC1");
		expFragFreq.add(new int[] {0,1,4,1,0,0,0,0,0});
		smiles.add("C1CC(=C)CC1");
		expFragFreq.add(new int[] {1,0,5,0,0,0,0,0,0});
		smiles.add("C1=CC(=C)CC1");
		expFragFreq.add(new int[] {1,2,3,0,0,0,0,0,0});
		smiles.add("C1C=C(C)CC1");
		expFragFreq.add(new int[] {1,1,3,1,0,0,0,0,0});
		smiles.add("CCCC1CC(C)CC1");
		expFragFreq.add(new int[] {0,2,5,2,0,0,0,0,0});
		smiles.add("CC(C)CC1CC(C)CC1");
		expFragFreq.add(new int[] {0,3,4,3,0,0,0,0,0});
		smiles.add("CC#C");
		expFragFreq.add(new int[] {1,1,0,1,0,0,0,0,0});
		smiles.add("CC(C)CC#C");
		expFragFreq.add(new int[] {1,2,1,2,0,0,0,0,0});
		smiles.add("C1CC1CC#C");
		expFragFreq.add(new int[] {1,2,3,0,0,0,0,0,0});
		
		smiles.add("CC(O)C");	
		expFragFreq.add(new int[] {0,1,0,2,0,0,0,1,0});
		smiles.add("CC(=O)C");
		expFragFreq.add(new int[] {1,0,0,2,0,0,1,0,0});
		smiles.add("CC=CCO");
		expFragFreq.add(new int[] {0,2,1,1,0,0,0,1,0});
		smiles.add("CC(O)CC(C)C");
		expFragFreq.add(new int[] {0,2,1,3,0,0,0,1,0});
		smiles.add("CC(=O)CC(C)C");
		expFragFreq.add(new int[] {1,1,1,3,0,0,1,0,0});
		smiles.add("C1COCC1");
		expFragFreq.add(new int[] {0,0,4,0,0,0,1,0,0});
		smiles.add("C1OC=CC1");
		expFragFreq.add(new int[] {0,2,2,0,0,0,1,0,0});
		smiles.add("C1CC(O)CC1");
		expFragFreq.add(new int[] {0,1,4,0,0,0,0,1,0});
		smiles.add("C1CC(=O)CC1");
		expFragFreq.add(new int[] {1,0,4,0,0,0,1,0,0});
		smiles.add("C1C=C(O)CC1");
		expFragFreq.add(new int[] {1,1,3,0,0,0,0,1,0});
		smiles.add("CCCC1CC(O)CC1");
		expFragFreq.add(new int[] {0,2,5,1,0,0,0,1,0});
		smiles.add("CC(O)CC1CC(O)CC1");
		expFragFreq.add(new int[] {0,3,4,1,0,0,0,2,0});
		smiles.add("OC#C");
		expFragFreq.add(new int[] {1,1,0,0,0,0,0,1,0});
		smiles.add("CC(O)CC#C");
		expFragFreq.add(new int[] {1,2,1,1,0,0,0,1,0});
		smiles.add("C1OC1CC#C");
		expFragFreq.add(new int[] {1,2,2,0,0,0,1,0,0});
		
		
		smiles.add("CC(N)C");
		expFragFreq.add(new int[] {0,1,0,2,0,1,0,0,0});
		smiles.add("CC(=N)C");
		expFragFreq.add(new int[] {1,0,0,2,1,0,0,0,0});
	
		
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	
	
	public void test02_02() throws Exception
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,H");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C0","C1","C2","C3","N1","N2","O1","S0","S1"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CC=CCN");
		expFragFreq.add(new int[] {0,2,1,1,0,1,0,0,0});
		smiles.add("CC(N)CC(N)C");
		expFragFreq.add(new int[] {0,2,1,2,0,2,0,0,0});
		smiles.add("C1CNCC1");
		expFragFreq.add(new int[] {0,0,4,0,1,0,0,0,0});
		smiles.add("C1NC=CC1");
		expFragFreq.add(new int[] {0,2,2,0,1,0,0,0,0});
		smiles.add("C1CC(N)CC1");
		expFragFreq.add(new int[] {0,1,4,0,0,1,0,0,0});
		smiles.add("C1CC(=N)CC1");
		expFragFreq.add(new int[] {1,0,4,0,1,0,0,0,0});
		smiles.add("C1C=C(N)CC1");
		expFragFreq.add(new int[] {1,1,3,0,0,1,0,0,0});
		smiles.add("CCCC1CC(N)CC1");
		expFragFreq.add(new int[] {0,2,5,1,0,1,0,0,0});
		smiles.add("OC(C)CC1CC(N)CC1");
		expFragFreq.add(new int[] {0,3,4,1,0,1,1,0,0});
		smiles.add("NC#C");
		expFragFreq.add(new int[] {1,1,0,0,0,1,0,0,0});
		smiles.add("CC(N)CC#C");
		expFragFreq.add(new int[] {1,2,1,1,0,1,0,0,0});
		smiles.add("NC1CC1CC#C");
		expFragFreq.add(new int[] {1,3,2,0,0,1,0,0,0});
		
		smiles.add("CC(S)C");
		expFragFreq.add(new int[] {0,1,0,2,0,0,0,0,1});
		smiles.add("CC(=S)C");
		expFragFreq.add(new int[] {1,0,0,2,0,0,0,1,0});
		smiles.add("CC=CCS");
		expFragFreq.add(new int[] {0,2,1,1,0,0,0,0,1});
		smiles.add("CC(S)CC(S)C");
		expFragFreq.add(new int[] {0,2,1,2,0,0,0,0,2});
		smiles.add("C1CSCC1");
		expFragFreq.add(new int[] {0,0,4,0,0,0,0,1,0});
		smiles.add("C1SC=CC1");
		expFragFreq.add(new int[] {0,2,2,0,0,0,0,1,0});
		smiles.add("C1CC(S)CC1");
		expFragFreq.add(new int[] {0,1,4,0,0,0,0,0,1});
		smiles.add("C1CC(=S)CC1");
		expFragFreq.add(new int[] {1,0,4,0,0,0,0,1,0});
		smiles.add("C1C=C(S)CC1");
		expFragFreq.add(new int[] {1,1,3,0,0,0,0,0,1});
		smiles.add("CCCC1CC(S)CC1");
		expFragFreq.add(new int[] {0,2,5,1,0,0,0,0,1});
		smiles.add("CC(S)CC1CC(S)CC1");
		expFragFreq.add(new int[] {0,3,4,1,0,0,0,0,2});
		smiles.add("SC#C");
		expFragFreq.add(new int[] {1,1,0,0,0,0,0,0,1});
		smiles.add("CC(S)CC#C");
		expFragFreq.add(new int[] {1,2,1,1,0,0,0,0,1});
		smiles.add("C1SC1CC#C");
		expFragFreq.add(new int[] {1,2,2,0,0,0,0,1,0});
		
		
		smiles.add("NC(O)C");
		expFragFreq.add(new int[] {0,1,0,1,0,1,1,0,0});
		smiles.add("SC=CCO");
		expFragFreq.add(new int[] {0,2,1,0,0,0,1,0,1});
		smiles.add("NC(S)CC(O)C");
		expFragFreq.add(new int[] {0,2,1,1,0,1,1,0,1});
		smiles.add("C1C(CN)CC(=S)CC1");
		expFragFreq.add(new int[] {1,1,5,0,0,1,0,1,0});
		
			
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	
	public void test03() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,HeN");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C1","C2","C3","C4"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CCC");
		expFragFreq.add(new int[] {2,1,0,0});
		smiles.add("CCCC");
		expFragFreq.add(new int[] {2,2,0,0});
		smiles.add("CCCCC");
		expFragFreq.add(new int[] {2,3,0,0});
		smiles.add("CC(C)C");
		expFragFreq.add(new int[] {3,0,1,0});
		smiles.add("CC(C)(C)C");
		expFragFreq.add(new int[] {4,0,0,1});
		
		smiles.add("C=CC");
		expFragFreq.add(new int[] {2,1,0,0});
		smiles.add("CC=CC");
		expFragFreq.add(new int[] {2,2,0,0});
		smiles.add("C=CCC=C");
		expFragFreq.add(new int[] {2,3,0,0});
		smiles.add("C=C(C)C");
		expFragFreq.add(new int[] {3,0,1,0});
		
		smiles.add("C#CC");
		expFragFreq.add(new int[] {2,1,0,0});
		smiles.add("CC#CC");
		expFragFreq.add(new int[] {2,2,0,0});
		smiles.add("C#CCC=C");
		expFragFreq.add(new int[] {2,3,0,0});
		
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	

	public void test03_01() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,HeN");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C0","C1","C2","C3","C4","O1","O2"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("C");
		expFragFreq.add(new int[] {1,0,0,0,0,0,0});
		smiles.add("CCO");
		expFragFreq.add(new int[] {0,1,1,0,0,1,0});
		smiles.add("CCCO");
		expFragFreq.add(new int[] {0,1,2,0,0,1,0});
		smiles.add("CCCCO");
		expFragFreq.add(new int[] {0,1,3,0,0,1,0});
		smiles.add("CC(O)C");
		expFragFreq.add(new int[] {0,2,0,1,0,1,0});
		smiles.add("CC(O)(C)C");
		expFragFreq.add(new int[] {0,3,0,0,1,1,0});
		smiles.add("CC(O)(O)C");
		expFragFreq.add(new int[] {0,2,0,0,1,2,0});
		
		smiles.add("O=CC");
		expFragFreq.add(new int[] {0,1,1,0,0,1,0});
		smiles.add("CC=CO");
		expFragFreq.add(new int[] {0,1,2,0,0,1,0});
		smiles.add("C=CCC=O");
		expFragFreq.add(new int[] {0,1,3,0,0,1,0});
		smiles.add("C=C(O)C");
		expFragFreq.add(new int[] {0,2,0,1,0,1,0});
		
		smiles.add("C#CO");
		expFragFreq.add(new int[] {0,1,1,0,0,1,0});
		smiles.add("CC#CO");
		expFragFreq.add(new int[] {0,1,2,0,0,1,0});
		smiles.add("C#CCC=O");
		expFragFreq.add(new int[] {0,1,3,0,0,1,0});
		
		smiles.add("C1CCOC1");
		expFragFreq.add(new int[] {0,0,4,0,0,0,1});
		smiles.add("C1CCOCC1");
		expFragFreq.add(new int[] {0,0,5,0,0,0,1});
		smiles.add("C1COC1");
		expFragFreq.add(new int[] {0,0,3,0,0,0,1});
		smiles.add("C1OC1");
		expFragFreq.add(new int[] {0,0,2,0,0,0,1});
		
		smiles.add("C1C(C)COC1");
		expFragFreq.add(new int[] {0,1,3,1,0,0,1});
		smiles.add("C1C(C)COCC1");
		expFragFreq.add(new int[] {0,1,4,1,0,0,1});
		smiles.add("C1C(O)COCC1");
		expFragFreq.add(new int[] {0,0,4,1,0,1,1});
				
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	
	
	public void test03_02() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,HeN");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C1","C2","C3","C4","S1","S2"};
		
		//Add molecules and expected fragment frequencies
		
		smiles.add("CCS");
		expFragFreq.add(new int[] {1,1,0,0,1,0});
		smiles.add("CCCS");
		expFragFreq.add(new int[] {1,2,0,0,1,0});
		smiles.add("CCCCS");
		expFragFreq.add(new int[] {1,3,0,0,1,0});
		smiles.add("CC(S)C");
		expFragFreq.add(new int[] {2,0,1,0,1,0});
		smiles.add("CC(S)(C)C");
		expFragFreq.add(new int[] {3,0,0,1,1,0});
		smiles.add("CC(S)(S)C");
		expFragFreq.add(new int[] {2,0,0,1,2,0});
		
		smiles.add("S=CC");
		expFragFreq.add(new int[] {1,1,0,0,1,0}); 
		smiles.add("CC=CS");
		expFragFreq.add(new int[] {1,2,0,0,1,0});
		smiles.add("C=CCC=S");
		expFragFreq.add(new int[] {1,3,0,0,1,0}); 
		smiles.add("C=C(S)C");
		expFragFreq.add(new int[] {2,0,1,0,1,0});
		
		smiles.add("C#CS");
		expFragFreq.add(new int[] {1,1,0,0,1,0});
		smiles.add("CC#CS");
		expFragFreq.add(new int[] {1,2,0,0,1,0});
		smiles.add("C#CCC=S");
		expFragFreq.add(new int[] {1,3,0,0,1,0});
		
		smiles.add("C1CCSC1");
		expFragFreq.add(new int[] {0,4,0,0,0,1}); 
		smiles.add("C1CCSCC1");
		expFragFreq.add(new int[] {0,5,0,0,0,1});
		smiles.add("C1CSC1");
		expFragFreq.add(new int[] {0,3,0,0,0,1}); 
		smiles.add("C1SC1");
		expFragFreq.add(new int[] {0,2,0,0,0,1});
		
		smiles.add("C1C(C)CSC1");
		expFragFreq.add(new int[] {1,3,1,0,0,1});
		smiles.add("C1C(C)CSCC1");
		expFragFreq.add(new int[] {1,4,1,0,0,1});
		
				
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	
	public void test03_03() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,HeN");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C1","C2","C3","C4","N1","N2"};
		
		//Add molecules and expected fragment frequencies
		
		smiles.add("CCN");
		expFragFreq.add(new int[] {1,1,0,0,1,0});
		smiles.add("CCCN");
		expFragFreq.add(new int[] {1,2,0,0,1,0});
		smiles.add("CCCCN");
		expFragFreq.add(new int[] {1,3,0,0,1,0});
		smiles.add("CC(N)C");
		expFragFreq.add(new int[] {2,0,1,0,1,0});
		smiles.add("CC(N)(C)C");
		expFragFreq.add(new int[] {3,0,0,1,1,0});
		smiles.add("CC(N)(N)C");
		expFragFreq.add(new int[] {2,0,0,1,2,0});
		
		smiles.add("N=CC");
		expFragFreq.add(new int[] {1,1,0,0,1,0}); 
		smiles.add("CC=CN");
		expFragFreq.add(new int[] {1,2,0,0,1,0});
		smiles.add("C=CCC=N");
		expFragFreq.add(new int[] {1,3,0,0,1,0}); 
		smiles.add("C=C(N)C");
		expFragFreq.add(new int[] {2,0,1,0,1,0});
		
		smiles.add("C#CN");
		expFragFreq.add(new int[] {1,1,0,0,1,0});
		smiles.add("CC#CN");
		expFragFreq.add(new int[] {1,2,0,0,1,0});
		smiles.add("C#CCC=N");
		expFragFreq.add(new int[] {1,3,0,0,1,0});
		
		smiles.add("C1CCNC1");
		expFragFreq.add(new int[] {0,4,0,0,0,1}); 
		smiles.add("C1CCNCC1");
		expFragFreq.add(new int[] {0,5,0,0,0,1});
		smiles.add("C1CNC1");
		expFragFreq.add(new int[] {0,3,0,0,0,1}); 
		smiles.add("C1NC1");
		expFragFreq.add(new int[] {0,2,0,0,0,1});
		
		smiles.add("C1C(C)CNC1");
		expFragFreq.add(new int[] {1,3,1,0,0,1});
		smiles.add("C1C(C)CNCC1");
		expFragFreq.add(new int[] {1,4,1,0,0,1});
		
				
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}


	
	public void test04() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,Hyb");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C1","C2","C3"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CCC");
		expFragFreq.add(new int[] {0,0,3});
		smiles.add("C=CC");
		expFragFreq.add(new int[] {0,2,1});
		smiles.add("C#CC");
		expFragFreq.add(new int[] {2,0,1});
		smiles.add("CC(C)C");
		expFragFreq.add(new int[] {0,0,4});
		smiles.add("CCCC");
		expFragFreq.add(new int[] {0,0,4});
		smiles.add("CC=CCC");
		expFragFreq.add(new int[] {0,2,3});
		smiles.add("CC#CCC");
		expFragFreq.add(new int[] {2,0,3});
		smiles.add("CC(=C)C");
		expFragFreq.add(new int[] {0,2,2});
		smiles.add("CC(C)(C)C");
		expFragFreq.add(new int[] {0,0,5});
		smiles.add("CC(=C)C(=C)C");
		expFragFreq.add(new int[] {0,4,2});
		smiles.add("C=CC(C#C)C=C");
		expFragFreq.add(new int[] {2,4,1});
		
		smiles.add("C1CC1");
		expFragFreq.add(new int[] {0,0,3});
		smiles.add("C1CCC1");
		expFragFreq.add(new int[] {0,0,4});
		smiles.add("C1CCCC1");
		expFragFreq.add(new int[] {0,0,5});
		smiles.add("C1CCCCC1");
		expFragFreq.add(new int[] {0,0,6});
		smiles.add("C1C=C1");
		expFragFreq.add(new int[] {0,2,1});
		smiles.add("C1C=CC1");
		expFragFreq.add(new int[] {0,2,2});
		smiles.add("C1=CC=C1");
		expFragFreq.add(new int[] {0,4,0});
		smiles.add("C1C#CC1");
		expFragFreq.add(new int[] {2,0,2});
		smiles.add("C1=CC=CC1");
		expFragFreq.add(new int[] {0,4,1});
		smiles.add("C1CC(C)CC1");
		expFragFreq.add(new int[] {0,0,6});
		smiles.add("C1CC(=C)CC1");
		expFragFreq.add(new int[] {0,2,4});
				
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	
	
	
	public void test04_01() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,Hyb");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C1","C2","C3","N1","N2","N3"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CN");
		expFragFreq.add(new int[] {0,0,1,0,0,1});
		smiles.add("C=N");
		expFragFreq.add(new int[] {0,1,0,0,1,0});
		smiles.add("C#N");
		expFragFreq.add(new int[] {1,0,0,1,0,0});
		smiles.add("CCN");
		expFragFreq.add(new int[] {0,0,2,0,0,1});
		smiles.add("C=CCN");
		expFragFreq.add(new int[] {0,2,1,0,0,1});
		smiles.add("CCCCC1CNC1");
		expFragFreq.add(new int[] {0,0,7,0,0,1});
		smiles.add("CC=CC=C1CNC1");
		expFragFreq.add(new int[] {0,4,3,0,0,1});
		smiles.add("CC=CC=C1C=NC1");
		expFragFreq.add(new int[] {0,5,2,0,1,0});
		smiles.add("C1CC1C#N");
		expFragFreq.add(new int[] {1,0,3,1,0,0});
		smiles.add("C1=CC1C#N");
		expFragFreq.add(new int[] {1,2,1,1,0,0});
		smiles.add("C1NC1C#N");
		expFragFreq.add(new int[] {1,0,2,1,0,1});
	
				
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	
	
	public void test04_02() throws Exception 
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,Hyb");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C1","C2","C3","O2","O3"};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CO");
		expFragFreq.add(new int[] {0,0,1,0,1});
		smiles.add("C=O");
		expFragFreq.add(new int[] {0,1,0,1,0});
		smiles.add("C#CO");
		expFragFreq.add(new int[] {2,0,0,0,1});
		smiles.add("C=CCO");
		expFragFreq.add(new int[] {0,2,1,0,1});
		smiles.add("C=CC=O");
		expFragFreq.add(new int[] {0,3,0,1,0});
		smiles.add("CCCCC1COC1");
		expFragFreq.add(new int[] {0,0,7,0,1});
		smiles.add("CC=CC=C1COC1");
		expFragFreq.add(new int[] {0,4,3,0,1});
		smiles.add("CC=CC=C1COC1");
		expFragFreq.add(new int[] {0,4,3,0,1});
	
				
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
	
	
		
	public void test07() throws Exception
	{
		GroupContributionModel gcm = createGCM (GroupContributionModel.Type.ATOMIC, "A,H,HeN");
		List<String> smiles = new ArrayList<String>(); 
		List<int[]> expFragFreq = new ArrayList<int[]>();
		String[] expFragDesignations = {"C02","C11","C12","C21","C22","C31",
										"N01","N11","N21",
										"O01","O11",
										"S01","S11" 
				};
		
		//Add molecules and expected fragment frequencies
		smiles.add("CCC");
		expFragFreq.add(new int[] {0,0,0,0,1,2,
									0,0,0,
									0,0,
									0,0 
									});
		smiles.add("C#C");
		expFragFreq.add(new int[] {0,2,0,0,0,0,
									0,0,0,
									0,0,
									0,0 
									});
		smiles.add("C=CC");
		expFragFreq.add(new int[] {0,0,1,1,0,1,
									0,0,0,
									0,0,
									0,0 
									});
		smiles.add("C#CC");
		expFragFreq.add(new int[] {1,1,0,0,0,1,
									0,0,0,
									0,0,
									0,0 
									});
		smiles.add("CC#N");
		expFragFreq.add(new int[] {1,0,0,0,0,1,
									1,0,0,
									0,0,
									0,0 
									});
		smiles.add("CC=N");
		expFragFreq.add(new int[] {0,0,1,0,0,1,
									0,1,0,
									0,0,
								 	0,0	
									});
								 
		smiles.add("CCN");
		expFragFreq.add(new int[] {0,0,0,0,1,1,
									0,0,1,
									0,0,
									0,0 
									});
		smiles.add("CC=O");
		expFragFreq.add(new int[] {0,0,1,0,0,1,
									0,0,0,
									1,0,
									0,0 
								 	});
		smiles.add("CCO");
		expFragFreq.add(new int[] {0,0,0,0,1,1,
									0,0,0,
									0,1,
									0,0 
									});
		smiles.add("CC=S");
		expFragFreq.add(new int[] {0,0,1,0,0,1,
									0,0,0,
									0,0,
									1,0
									});
		smiles.add("CCS");
		expFragFreq.add(new int[] {0,0,0,0,1,1,
									0,0,0,
									0,0,
									0,1
									});		
		
		
		checkFragmentation (gcm, smiles, expFragDesignations, expFragFreq);		
	}
		
	
}
