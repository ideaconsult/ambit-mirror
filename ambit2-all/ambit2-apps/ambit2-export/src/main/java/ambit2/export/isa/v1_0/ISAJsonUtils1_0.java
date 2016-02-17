package ambit2.export.isa.v1_0;

import ambit2.export.isa.base.ISAConst;
import ambit2.export.isa.v1_0.objects.Comment;
import ambit2.export.isa.v1_0.objects.Factor;
import ambit2.export.isa.v1_0.objects.FactorValue;
import ambit2.export.isa.v1_0.objects.Investigation;
import ambit2.export.isa.v1_0.objects.MaterialAttributeValue;
import ambit2.export.isa.v1_0.objects.Study;

public class ISAJsonUtils1_0 
{	
	
	//Study utilities
	public static void addStudyDescriptionContent(Study study, String content)
	{
		if (study.description == null)
			study.description = content;
		else
			study.description = study.description + ISAConst.addSeparator + content; 
	}
	
	
	public static Comment getComment(String name, String value)
	{
		Comment c = new Comment();
		c.name = name;
		c.value = value;
		return c;
	};
	
	public static MaterialAttributeValue getMaterialAttributeValue()
	{
		MaterialAttributeValue mav = new MaterialAttributeValue();
		//TODO
		
		return mav;
	}
	
	public static FactorValue getFactorValue(String factorName, Object value)
	{
		FactorValue fv = new FactorValue();
		fv.category = new Factor();
		fv.category.factorName = factorName;
		fv.value = value;
		return fv;
	}
	
}
