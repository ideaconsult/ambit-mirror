package ambit2.notation.dataextractors;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.notation.NotationData;

public interface IDataExtractor 
{
	public NotationData extract(SubstanceRecord substRec) throws Exception;
	public NotationData extract(StructureRecord substRec) throws Exception;
}
