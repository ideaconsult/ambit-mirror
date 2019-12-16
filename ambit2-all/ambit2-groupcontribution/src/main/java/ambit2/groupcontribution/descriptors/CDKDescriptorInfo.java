package ambit2.groupcontribution.descriptors;


import ambit2.groupcontribution.transformations.IValueTransformation;

public class CDKDescriptorInfo 
{
	public String fullString = null;
	public String name = null;
	public int descrInstanceIndex = -1;
	public int resultPos = 0;
	public int hAtomsFlag = 0;
	public IValueTransformation valueTranform = null;
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("fullString: " + fullString + "\n");
		sb.append("name: " + name + "\n");
		sb.append("descrInstanceIndex: " + descrInstanceIndex + "\n");
		sb.append("resultPos: " + resultPos + "\n");
		sb.append("hAtomsFlag: " + hAtomsFlag + "\n");
		sb.append("valueTranform: " + ((valueTranform==null)?"null":valueTranform.getTransformationName()) + "\n");
		return sb.toString();
	}
}
