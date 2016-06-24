package ambit2.base.data.study.test;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;

public class TestSubstanceFactory {

	public static SubstanceEndpointsBundle getTestSubstanceEndpointsBundle() {
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();

		bundle.setDescription("Test Bundle description");
		bundle.setTitle("Test Bundle title");
		// TODO

		return bundle;
	}

	public static SubstanceRecord getTestSubstanceRecord() {
		SubstanceRecord record = new SubstanceRecord();
		record.setContent("Generated test content");
		record.setOwnerUUID("owner-uuid-1234567890");
		record.setSubstanceUUID("record-uuid-1234567890");

		// Add measurements
		ProtocolApplication pa = ProtocolApplicationTestFactory.initpa();
		pa.setSubstanceUUID(record.getOwnerUUID());
		record.addMeasurement(pa);

		EffectRecord eff = (EffectRecord) pa.getEffects().get(0);
		IParams par = (IParams) eff.getConditions();
		par.put("condition1", "value1");

		addCompositionData(record);

		// record.addMeasurement(ProtocolApplicationTestFactory.initpc());
		// record.addMeasurement(ProtocolApplicationTestFactory.initbiodeg());

		return record;
	}

	protected static void addCompositionData(SubstanceRecord record) {
		List<CompositionRelation> list = new ArrayList<CompositionRelation>();

		IStructureRecord s1 = new StructureRecord();
		Proportion p1 = new Proportion();
		CompositionRelation rel1 = new CompositionRelation(record, s1,
				STRUCTURE_RELATION.HAS_CONSTITUENT, p1);
		s1.setFormula("C5H10");
		s1.setSmiles("CCC=C");
		s1.setInchi("DUMMYINCHI0001");
		s1.setInchiKey("DUMMYINCHIKEY0001");
		p1.setReal_lowervalue(0.5);
		list.add(rel1);

		IStructureRecord s2 = new StructureRecord();
		Proportion p2 = new Proportion();
		CompositionRelation rel2 = new CompositionRelation(record, s2,
				STRUCTURE_RELATION.HAS_CONSTITUENT, p2);
		s2.setFormula("C6H12");
		s2.setSmiles("CCCC=C");
		s2.setInchi("DUMMYINCHI0002");
		s2.setInchiKey("DUMMYINCHIKEY0002");
		p2.setReal_lowervalue(0.5);
		list.add(rel2);

		record.setRelatedStructures(list);
	}

}
