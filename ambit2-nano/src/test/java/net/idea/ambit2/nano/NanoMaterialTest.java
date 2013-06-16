package net.idea.ambit2.nano;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.bitbucket.nanojava.data.Nanomaterial;
import org.bitbucket.nanojava.data.measurement.MeasurementValue;
import org.bitbucket.nanojava.data.measurement.Unit;
import org.bitbucket.nanojava.io.Serializer;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMolecule;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;

public class NanoMaterialTest {
	@Test
	public void test() throws Exception {
		
		List<Nanomaterial> materials = new ArrayList<Nanomaterial>();
		materials.add(new Nanomaterial("GRAPHENE"));
		Nanomaterial material = createNM();
		materials.add(material);
		CMLList list = Serializer.toCML(materials);
		Assert.assertNotNull(list);
	
		StringBuilder b = new StringBuilder();
		b.append("<?xml version=\"1.0\"?>\n");
		b.append(list.toXML());
		System.out.println(b);

	}
	@Test
	public void testNormalize() throws Exception {
		CMLMolecule list = Serializer.toCML(createNM());
		IStructureRecord structure = new StructureRecord();
		StringBuilder b = new StringBuilder();
		b.append("<?xml version=\"1.0\"?>\n");
		b.append(list.toXML());
		structure.setContent(b.toString());
		structure.setFormat(IStructureRecord.STRUC_TYPE.NANO.name());
		structure = NanoStructureNormalizer.normalizeNano(structure);
		System.out.println(structure.getFormula());
		for (Property p : structure.getProperties()) {
			System.out.println(p.toString() + " = " + structure.getProperty(p));
		}
	}
	@Test
	public void testNanoCMLReader() throws Exception {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("net/idea/ambit2/nano/test/nano.nmx");
		Assert.assertNotNull(in);
		IIteratingChemObjectReader reader = FileInputState.getReader(in, ".nmx");
		Assert.assertTrue(reader instanceof IRawReader);
		int records = 0;
		while (reader.hasNext()) {
			Nanomaterial nm = (Nanomaterial)reader.next();
			Assert.assertNotNull(nm);
			IStructureRecord ac = ((IRawReader<IStructureRecord>)reader).nextRecord();
			System.out.println(ac);
			System.out.println(ac.getContent());
			Assert.assertEquals("NANO",ac.getFormat());
			records ++;
		}
		Assert.assertEquals(2,records);
		in.close();
	}
	protected Nanomaterial createNM() {
		Nanomaterial material = new Nanomaterial("METALOXIDE");
		material.setChemicalComposition(MolecularFormulaManipulator.getMolecularFormula(
                "ZnO", DefaultChemObjectBuilder.getInstance()
            ));
        List<String> labels = new ArrayList<String>();
        labels.add("NM1"); labels.add("ZnO");
        material.setLabels(labels);
		material.setSize(new MeasurementValue(20.0, 7, Unit.NM));		
		return material;
	}
}
