package ambit2.base.interfaces;

import java.io.Serializable;

public interface IStructureRelation<RELATION_TYPE,RELATION_METRIC,FIRSTSTRUC extends IStructureRecord,SECONDSTRUC extends IStructureRecord> extends Serializable {
	RELATION_TYPE getRelationType();
	void setRelationType(RELATION_TYPE relation);
	RELATION_METRIC getRelation();
	void setRelation(RELATION_METRIC relation);
	FIRSTSTRUC getFirstStructure();
	SECONDSTRUC getSecondStructure();
	void setFirstStructure(FIRSTSTRUC struc);
	void setSecondStructure(SECONDSTRUC struc);
	public String getName();
	public void setName(String name);

}
