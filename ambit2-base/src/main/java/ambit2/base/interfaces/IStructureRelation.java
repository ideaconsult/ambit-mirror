package ambit2.base.interfaces;

import java.io.Serializable;

public interface IStructureRelation<RELATION> extends Serializable {
	RELATION getRelation();
	void setRelation(RELATION relation);
	IStructureRecord[] getStructures();
	void setStructures(IStructureRecord[] structures);
}
