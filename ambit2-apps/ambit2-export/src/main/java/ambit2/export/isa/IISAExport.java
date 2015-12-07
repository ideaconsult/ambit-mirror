package ambit2.export.isa;

import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;

public interface IISAExport 
{
	public ISAFormat getISAFormat();
	public ISAVersion getISAVersion();
	
	//public Investigation getInvestigation();
	
}
