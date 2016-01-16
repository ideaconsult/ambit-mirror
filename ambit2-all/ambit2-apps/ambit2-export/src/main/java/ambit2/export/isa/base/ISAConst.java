package ambit2.export.isa.base;

public class ISAConst 
{	
	public static enum DataFileFormat {
		TEXT_TAB, TEXT_CSV
	}
	
	
	public static enum ISAVersion {
		Ver1_0, Ver2_0
	}
	
	public static enum ISAFormat {
		TAB, JSON
	}
	
	public static final String investigationFilePrefix = "i_";
	public static final String studyFilePrefix = "s_";
	public static final String assayFilePrefix = "a_";
	
	public static final String addSeparator = " ";
	
}
