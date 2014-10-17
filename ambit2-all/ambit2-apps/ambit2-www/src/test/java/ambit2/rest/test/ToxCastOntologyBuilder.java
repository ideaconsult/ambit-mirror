package ambit2.rest.test;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;

public class ToxCastOntologyBuilder {
	protected static final String _NS = "http://www.opentox.org/toxcast#%s";
	public static final String NS = String.format(_NS, "");
	protected OntModel jenaModel;
	protected OntClass assay;
	protected OntProperty hasProperty;
	protected OntProperty gene;
	protected DatatypeProperty hasValue;
	protected DatatypeProperty source_name_aid;
	protected DatatypeProperty assay_code;
	protected DatatypeProperty assay_name;
	protected DatatypeProperty assay_description;
	protected DatatypeProperty assay_components;

		
	public ToxCastOntologyBuilder() {
		super();
		jenaModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,
				null);
		jenaModel.setNsPrefix("toxcast", NS);
		jenaModel.setNsPrefix("dc", DC.NS);

		/*
		 * jenaModel.setNsPrefix( "ot", OT.NS ); jenaModel.setNsPrefix( "ota",
		 * OTA.NS ); jenaModel.setNsPrefix( "otee", OTEE.NS );
		 * jenaModel.setNsPrefix( "owl", OWL.NS ); jenaModel.setNsPrefix( "dc",
		 * DC.NS ); jenaModel.setNsPrefix( "bx", BibTex.NS );
		 */
		jenaModel.setNsPrefix("xsd", XSDDatatype.XSD + "#");

	}

	protected void processRow(Row header, Row row) throws Exception {

		Individual a = null;
		Iterator<Cell> cols = row.cellIterator();
		while (cols.hasNext()) {
			Cell cell = cols.next();
			a = a == null ? getIndividual(NS, String.format("%s", row
					.getCell(0).getStringCellValue()), assay) : a;
			if (a == null)
				continue;

			Object value = null;
			if (cell != null) {
				Cell top = header.getCell(cell.getColumnIndex());

				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					value = cell.getNumericCellValue();
					/*
					jenaModel.createLiteralStatement(a, hasValue, cell
							.getNumericCellValue());
							*/
					if (top.getStringCellValue().equals("ASSAY_GENE_ID")) {
						Individual goid = jenaModel.createIndividual(String.format("http://bio2rdf.org/geneid:%d",(int)cell.getNumericCellValue()),null);
						//p.addSameAs(goid);
						jenaModel.add(a, gene, goid);

					} else 
						value = value.toString();
					break;
				case Cell.CELL_TYPE_STRING:
					value = cell.getStringCellValue();
					
					if (top.getStringCellValue().equals("SOURCE_NAME_AID") && (!"NA".equals(value))) {
						jenaModel.add(a, source_name_aid, value.toString());
						value = null;
					} else 	
					if (top.getStringCellValue().equals("ASSAY_CODE") && (!"NA".equals(value))) {
						jenaModel.add(a, assay_code, value.toString());
						value = null;
					} else if (top.getStringCellValue().equals("ASSAY_DESCRIPTION") && (!"NA".equals(value))) {
						jenaModel.add(a, assay_description, value.toString());
						value = null;
					} else if (top.getStringCellValue().equals("ASSAY_NAME") && (!"NA".equals(value))) {
						jenaModel.add(a, assay_name, value.toString());
						value = null;
					} else if (top.getStringCellValue().equals("ASSAY_COMPONENTS") && (!"NA".equals(value))) {
						jenaModel.add(a, assay_components, value.toString());
						value = null;
					} else
					if (top.getStringCellValue().equals("ASSAY_GENE_ID") && (!"NA".equals(value))) {
						
						String[] ids = value.toString().split(",");
						for (String id:ids) {
						Individual goid = jenaModel.createIndividual(String.format("http://bio2rdf.org/geneid:%s",id),null);
						//p.addSameAs(goid);
						jenaModel.add(a, gene, goid);
						}
					} 					
					break;
				case Cell.CELL_TYPE_BLANK:
					value = "";
					break;
				case Cell.CELL_TYPE_ERROR:
					value = "";
					break;
				case Cell.CELL_TYPE_FORMULA:
					try {
						value = cell.getStringCellValue();
						break;
					} catch (Exception x) {
						try {
							//jenaModel.createLiteralStatement(a, hasValue, cell	.getNumericCellValue());
							value = cell.getNumericCellValue();
							
							/*
							jenaModel.createLiteralStatement(a, hasValue, cell
									.getNumericCellValue());
									*/
							value = value.toString();							

						} catch (Exception z) {
							x.printStackTrace();
						}
					}
				}

				if (value != null) {
					System.out.println(String.format("%d\t%s\t%s", row
							.getRowNum(), top.getStringCellValue(), value));
					Resource c = jenaModel.getResource(String.format(_NS, top
							.getStringCellValue()));
					Individual p = null;
					if (value.toString().startsWith("http://"))
						p = getIndividual("", value.toString(), null);
					else
						p = getIndividual(
								NS, String
										.format("%s", value.toString()), c);
					if (p == null)
						continue;
					jenaModel.add(a, hasProperty, p);
					/*
					if (top.getStringCellValue().equals("ASSAY_GENE_ID")) {
						Individual goid = jenaModel.createIndividual(String.format("http://bio2rdf.org/geneid:%s",value.toString()),null);
						p.addSameAs(goid);
					}
					*/
				}

			}

			// c.setSuperClass(superClass);
		}
	}

	protected Individual getIndividual(String NS, String value, Resource c)
			throws Exception {
		
		
		if ("NA".equals(value)) return null;
		if ("".equals(value)) return null;
		String originalValue = value;
		value = value.trim();
		value = value.replace("[", "_");
		value = value.replace("]", "_");
		value = value.replace(" ", "_");
		value = value.replace("#", "_");
		Individual p = jenaModel
				.getIndividual(String.format("%s%s", NS, value));
		if (p == null) {
			p = jenaModel.createIndividual(String.format("%s%s", NS, value), c);
			jenaModel.createLiteralStatement(p, DC.title, originalValue
					.toString());
		} else {
			p.addOntClass(c);
		}
		return p;

	}

	protected Row processHeader(Row row) throws Exception {
		Iterator<Cell> cols = row.cellIterator();

		OntClass superClass = jenaModel.createClass(String.format(_NS,
				"AssayProperty"));

		assay = jenaModel.createClass(String.format(_NS, "Assay"));
		hasProperty = jenaModel.createOntProperty(String.format(_NS,
				"hasProperty"));
		hasProperty.setDomain(assay);
		hasProperty.setRange(superClass);
		
		gene = jenaModel.createOntProperty(String.format(_NS,"gene"));
		gene.setDomain(assay);

		source_name_aid = jenaModel.createDatatypeProperty(String.format(_NS,"source_name_aid"));
		source_name_aid.setDomain(assay);

		assay_code = jenaModel.createDatatypeProperty(String.format(_NS,"assay_code"));
		assay_code.setDomain(assay);

		assay_components = jenaModel.createDatatypeProperty(String.format(_NS,"assay_components"));
		assay_components.setDomain(assay);

		
		assay_name = jenaModel.createDatatypeProperty(String.format(_NS,"assay_name"));
		assay_name.setDomain(assay);
		
		assay_description = jenaModel.createDatatypeProperty(String.format(_NS,"assay_description"));
		assay_description.setDomain(assay);
		
		hasValue = jenaModel.createDatatypeProperty(String.format(_NS,
				"hasValue"));
		hasValue.setDomain(superClass);

		while (cols.hasNext()) {
			Cell cell = cols.next();
			
			if (cell.getStringCellValue().equals("SOURCE_NAME_AID")) continue;
			if (cell.getStringCellValue().equals("ASSAY_CODE")) continue;
			if (cell.getStringCellValue().equals("ASSAY_DESCRIPTION")) continue;
			if (cell.getStringCellValue().equals("ASSAY_NAME")) continue;
			if (cell.getStringCellValue().equals("ASSAY_ATP_CONCENTRATION_M")) continue;
			if (cell.getStringCellValue().equals("ASSAY_ENZYME_AFFINITY_ATP_KM_M")) continue;
			/*
			ASSAY_SUBSTRATE_CONCENTRATION_M
			ASSAY_ENZYME_SUBSTRATE_AFFINITY_KM_M

			ASSAY_ATP_CONCENTRATION_M
			ASSAY_ENZYME_AFFINITY_ATP_KM_M
			*/
			OntClass c = jenaModel.createClass(String.format(_NS, cell.getStringCellValue()));
			c.setSuperClass(superClass);
		}
		return row;
	}

	protected void build(InputStream input) throws Exception {
		Workbook workbook = new HSSFWorkbook(input);
		Sheet sheet = workbook.getSheetAt(0);

		int record = 0;
		Iterator<Row> iterator = sheet.rowIterator();
		Row header = null;
		while (iterator.hasNext()) {
			Row row = iterator.next();
			if (record == 0)
				header = processHeader(row);
			else
				processRow(header, row);
			record++;
		}
	}

	protected void save(Writer writer) throws Exception {
		jenaModel.write(writer);
	}

	public static void main(String[] args) {
		ToxCastOntologyBuilder t = new ToxCastOntologyBuilder();
		InputStream in = null;
		FileWriter writer = null;
		try {
			in = ToxCastOntologyBuilder.class.getClassLoader()
					.getResourceAsStream("ToxCastAssayMaster_20091214.xls");
			t.build(in);
			writer = new FileWriter(new File("toxcast.owl"));
			t.save(writer);
		} catch (Exception x) {
			x.printStackTrace();
		} finally {

			try {
				in.close();
			} catch (Exception x) {
			}
			try {
				writer.close();
			} catch (Exception x) {
			}
		}
	}
}
