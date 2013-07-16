package ambit2.base.interfaces;

import java.io.Serializable;

public interface IStructureRelation<RELATION_TYPE,RELATION_METRIC> extends Serializable {
	RELATION_TYPE getRelationType();
	void setRelationType(RELATION_TYPE relation);
	RELATION_METRIC getRelation();
	void setRelation(RELATION_METRIC relation);
	IStructureRecord[] getStructures();
	void setStructures(IStructureRecord[] structures);
}
