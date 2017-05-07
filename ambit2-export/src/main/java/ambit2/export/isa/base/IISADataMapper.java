package ambit2.export.isa.base;

public interface IISADataMapper 
{	
	public void setTargetDataObject(Object target) throws Exception;
	public void putObject(Object o, ISALocation location) throws Exception;
	public void putInteger(Integer k, ISALocation location) throws Exception;
	public void putDouble(Double d, ISALocation location) throws Exception;
	public void putString(String s, ISALocation location) throws Exception;
	public void putString(String s, ISALocation location, boolean isAdditiveContent) throws Exception;
}
