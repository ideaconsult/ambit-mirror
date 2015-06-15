package ambit2.db.update.bundle.substance;

import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.substance.CreateSubstance;
import ambit2.db.substance.relation.UpdateSubstanceRelation;

public class AddCompoundAsSubstanceToBundle extends AbstractUpdate<SubstanceEndpointsBundle, SubstanceRecord> {
    protected CreateSubstance q_create;
    protected UpdateSubstanceRelation q_composition;
    protected AddSubstanceToBundle q_addtobundle;

    public AddCompoundAsSubstanceToBundle() {
	super();
    }
    public AddCompoundAsSubstanceToBundle(SubstanceEndpointsBundle bundle,SubstanceRecord record) {
	super();
	setObject(record);
	setGroup(bundle);
    }

    public void setCompound(IStructureRecord compound) {

	setObject(structure2substance(compound));

    }

    public static SubstanceRecord structure2substance(IStructureRecord compound) {
	SubstanceRecord record = new SubstanceRecord();
	record.setIdchemical(compound.getIdchemical());
	record.setSubstanceUUID("CHEM-" + UUID.nameUUIDFromBytes(Integer.toString(compound.getIdchemical()).getBytes()));
	record.setOwnerName("STRUCTURE");
	record.setOwnerUUID("CHEM-" + UUID.nameUUIDFromBytes(record.getOwnerName().getBytes()));
	record.setContent(String.format("/compound/%d", compound.getIdchemical()));
	record.setFormat("URI");

	record.setSubstancetype("Monoconstituent");
	Proportion proportion = new Proportion();
	proportion.setTypical_value(100.0);
	proportion.setTypical_unit("%");
	proportion.setTypical("=");
	proportion.setReal_lower("=");
	proportion.setReal_lowervalue(100.0);
	proportion.setReal_unit("%");
	CompositionRelation relation = new CompositionRelation(record, compound,STRUCTURE_RELATION.HAS_STRUCTURE, proportion);
	relation.setName(record.getContent());
	relation.setCompositionUUID(record.getSubstanceUUID());
	record.addStructureRelation(relation);

	return record;

    }

    @Override
    public void setObject(SubstanceRecord object) {
	super.setObject(object);
	if (q_create == null) {
	    q_create = new CreateSubstance();
	    q_create.setDocumentType("Structure");
	}
	q_create.setObject(object);
	if (q_composition == null)
	    q_composition = new UpdateSubstanceRelation();
	q_composition.setGroup(object);
	try {
	    q_composition.setCompositionRelation(object.getRelatedStructures().get(0));
	} catch (Exception x) {
	    q_composition.setObject(null);
	}
	if (q_addtobundle == null)
	    q_addtobundle = new AddSubstanceToBundle();
	q_addtobundle.setObject(object);
    }

    @Override
    public void setGroup(SubstanceEndpointsBundle group) {
	super.setGroup(group);
	q_addtobundle.setGroup(group);
    }

    @Override
    public List<QueryParam> getParameters(int index) throws AmbitException {
	switch (index) {
	case 0: {
	    return q_create.getParameters(0);
	}
	case 1: {
	    return q_composition.getParameters(0);
	}
	case 2: {
	    return q_addtobundle.getParameters(0);
	}
	}
	throw new AmbitException("Unsupported index " + index);
    }

    @Override
    public String[] getSQL() throws AmbitException {
	return new String[] { q_create.getSQL()[0], q_composition.getSQL()[0], q_addtobundle.getSQL()[0] };
    }

    @Override
    public void setID(int index, int id) {
	switch (index) {
	case 0: {
	    q_create.setID(0, id);
	    break;
	}
	case 1: {
	    q_composition.setID(0, id);
	    break;
	}
	case 2: {
	    q_addtobundle.setID(0, id);
	    break;
	}
	}

    }

    @Override
    public boolean returnKeys(int index) {
	switch (index) {
	case 0:
	    q_create.returnKeys(0);
	    break;
	case 1:
	    q_composition.returnKeys(0);
	    break;
	case 2:
	    q_addtobundle.returnKeys(0);
	    break;

	default:
	    break;
	}
	return false;
    }
}
