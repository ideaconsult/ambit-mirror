package ambit2.export.isa.v1_0;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

import ambit2.export.isa.base.IISADataMapper;
import ambit2.export.isa.base.ISAConst;
import ambit2.export.isa.base.ISALocation;
import ambit2.export.isa.base.ISALocation.Layer;
import ambit2.export.isa.json.ISAJsonExporter;
import ambit2.export.isa.v1_0.objects.Assay;
import ambit2.export.isa.v1_0.objects.Investigation;
import ambit2.export.isa.v1_0.objects.Study;
import ambit2.export.isa.v1_0.objects.Process;

public class ISAJsonMapper1_0 implements IISADataMapper
{
	protected final static Logger logger = Logger.getLogger("ISAJsonMapper1_0");
	
	protected Investigation investigation = null;
	
	@Override
	public void putObject(Object o, ISALocation location) throws Exception 
	{
		if (o == null)
			return;
		
		if (o instanceof Integer)
		{	
			putInteger((Integer) o, location);
			return;
		}
		
		if (o instanceof Double)
		{	
			putDouble((Double) o, location);
			return;
		}
		
		if (o instanceof String)
		{	
			putString((String) o, location);
			return;
		}
		
		//By default object is treated as string
		putString(o.toString(), location);
	}
	
	@Override
	public void setTargetDataObject(Object target) throws Exception 
	{
		if (target instanceof Investigation)
			investigation = (Investigation) target;
		else
			throw new Exception("Target object is not instance of Investigation class!");
	}
	
	@Override
	public void putInteger(Integer k, ISALocation location) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putDouble(Double d, ISALocation location) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void putString(String s, ISALocation location) throws Exception {
		putString(s, location,  false);
	}

	@Override
	public void putString(String s, ISALocation location,  boolean isAdditiveContent) throws Exception 
	{
		switch (location.layer)
		{
		case INVESTIGATION:
			putDataInObject(s, location, investigation, isAdditiveContent);
			break;
			
		case STUDY:
			Study study = null;
			if (location.layerPosIndex == 0)
			{
				if (investigation.studies.isEmpty())
					investigation.studies.add(new Study());
				study = investigation.studies.get(investigation.studies.size() -1);
			}
			else
			{	
				int studyNum = location.layerPosIndex-1; //1-base to 0-base indexing
				checkStudy(studyNum);
				study = investigation.studies.get(studyNum);
			}
			
			if (location.processIndex < 0)
			{
				//No process is specified hence the data is put directly into the Study object
				putDataInObject(s, location, study, isAdditiveContent);
			}
			else
			{
				Process process = null;
				
				if (location.processIndex == 0)
				{
					if (study.processSequence.isEmpty())
						study.processSequence.add(new Process());
					process = study.processSequence.get(study.processSequence.size()-1);
				}
				else
				{
					int processNum = location.processIndex-1; //1-base to 0-base indexing
					checkStudyProcess(study, processNum);
					process = study.processSequence.get(processNum);
				}
				
				putDataInObject(s, location, process, isAdditiveContent);
			}
			
			break;
			
		case ASSAY:
			break;
		}
	}
	
	/** 
	 * Data is put into the Object using element and subElement information
	 **/	
	public void putDataInObject(String data, ISALocation location, Object obj, boolean isAdditiveContent) throws Exception
	{
		Class cls = obj.getClass();
		Field field = null;
		try{
			field = cls.getDeclaredField(location.elementName);
			
		}
		catch (Exception e)
		{
			logger.info("Location element not found: " + location.toString());
			return;
		}
		
		String fType = field.getType().getName();	
		if (fType.equals("java.lang.String"))
		{	
			if (isAdditiveContent)
			{
				String s = (String)field.get(obj);
				if (s == null)
					field.set(obj, data);
				else
					field.set(obj, s + ISAConst.addSeparator + data);
			}
			else
				field.set(obj, data);
		}
		else
		{
			//TODO
		}
	}
	
	
	protected int checkStudy(int studyIndex)
	{
		if (studyIndex < investigation.studies.size())
			return 0;
		else
		{
			int k = investigation.studies.size()-1;
			for (int i = k; i < studyIndex; i++)
				investigation.studies.add(new Study());
			return studyIndex - k;
		}
	}
	
	protected int checkStudyProcess(Study study, int processIndex)
	{
		if (processIndex < study.processSequence.size())
			return 0;
		else
		{
			int k = study.processSequence.size()-1;
			for (int i = k; i < processIndex; i++)
				study.processSequence.add(new Process());
			return processIndex - k;
		}
	}
	
	protected int checkAssay(Study study, int assayIndex)
	{
		if (assayIndex < study.assays.size())
			return 0;
		else
		{
			int k = study.assays.size()-1;
			for (int i = k; i < assayIndex; i++)
				study.assays.add(new Assay());
			return assayIndex - k;
		}
	}
	
	protected int checkAssayProcess(Assay assay, int processIndex)
	{
		if (processIndex < assay.processSequence.size())
			return 0;
		else
		{
			int k = assay.processSequence.size()-1;
			for (int i = k; i < processIndex; i++)
				assay.processSequence.add(new Process());
			return processIndex - k;
		}
	}
	
	
	
	

	
	
	
}
