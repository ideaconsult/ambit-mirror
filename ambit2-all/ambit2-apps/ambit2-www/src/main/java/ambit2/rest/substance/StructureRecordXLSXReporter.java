package ambit2.rest.substance;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.idea.i5.io.I5_ROOT_OBJECTS;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.data.study.IValue;
import ambit2.base.data.study.MultiValue;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ReliabilityParams._r_flags;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.io.study.StudyFormatter;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.search.QuerySmilesByID;
import ambit2.rendering.CachedImage;

public class StructureRecordXLSXReporter<Q extends IQueryRetrieval<IStructureRecord>>
		extends AbstractXLSXReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2075636527988009741L;
	// private CreationHelper helper;
	private Drawing drawing;
	protected ImageReporter<Q> imageReporter;
	protected ProcessorStructureRetrieval component;
	protected Dimension d = new Dimension(200, 200);
	protected int offset = 0;
	protected int rowIndex = 0;
	protected StudyFormatter formatter;
	protected Map<String, Integer> mergedProperties = new HashMap<String, Integer>();
	protected CellStyle style;

	public StructureRecordXLSXReporter(Request request, boolean hssf,
			Template template, Profile groupedProperties,
			SubstanceEndpointsBundle[] bundles, String urlPrefix,
			boolean includeMol) {
		super(request, hssf, template, groupedProperties, bundles, urlPrefix,
				includeMol);

		RetrieveStructure q = new RetrieveStructure(true);
		q.setFieldname(true);
		q.setPage(0);
		q.setPageSize(1);
		component = new ProcessorStructureRetrieval(q);

		imageReporter = new ImageReporter<Q>(MediaType.IMAGE_PNG.getName(),
				MediaType.IMAGE_PNG.getSubType(), d);
		formatter = new StudyFormatter();

		style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
	}

	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		imageReporter.setConnection(connection);
		component.setConnection(connection);
	}

	@Override
	public void close() throws Exception {
		try {
			component.close();
		} catch (Exception x) {
		}
		try {
			imageReporter.close();
		} catch (Exception x) {
		}
		super.close();
	}

	@Override
	public void header(OutputStream output, Q query) {

		// helper = workbook.getCreationHelper();
		drawing = sheet.createDrawingPatriarch();

		Row row = sheet.createRow(rowIndex);

		for (_columns col : _columns.values()) {
			Cell cell = row.createCell(col.ordinal());
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(col.toString());
			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			style.setAlignment(CellStyle.ALIGN_LEFT);
			style.setVerticalAlignment(CellStyle.VERTICAL_TOP);

			cell.setCellStyle(style);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			sheet.autoSizeColumn(col.ordinal(), true);
		}
		// sheet.setColumnWidth(
		// _columns.diagram.ordinal(),(d.width+2*offset)*XSSFShape.EMU_PER_PIXEL);

		for (I5_ROOT_OBJECTS section : I5_ROOT_OBJECTS.values()) {
			if (section.isIUCLID5() && section.isScientificPart() && section.isSupported()  && !section.isNanoMaterialTemplate()) {
				int last = mergedProperties.size();
				mergedProperties.put(
						"http://www.opentox.org/echaEndpoints.owl#"
								+ section.name(), last);
				Cell hcell = sheet.getRow(0).createCell(
						last + _columns.component.ordinal() + 1);
				hcell.setCellType(Cell.CELL_TYPE_STRING);
				hcell.setCellValue(section.getNumber() + ". "
						+ section.getTitle());

			}
			//
		}
		rowIndex++;
	}

	private enum _columns {
		tag {
			@Override
			public String toString() {
				return "Tag";
			}
		},
		name {
			@Override
			public String toString() {
				return "Substance name";
			}
		},
		cas {
			@Override
			public String toString() {
				return "CAS No.";
			}
		},
		diagram {
			@Override
			public String toString() {
				return "Structure";
			}
		},
		component {
			@Override
			public String toString() {
				return "Contained as";
			}
		};

	}

	private static final String prefix = "http://www.opentox.org/echaEndpoints.owl#";

	@Override
	public Object processItem(IStructureRecord item) throws Exception {
		Row row = sheet.createRow(rowIndex);
		int r = 0;
		if (item instanceof SubstanceRecord) {
			SubstanceRecord record = (SubstanceRecord) item;
			Cell cell = row.createCell(_columns.tag.ordinal());
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(rowIndex);

			cell = row.createCell(_columns.name.ordinal());
			cell.setCellType(Cell.CELL_TYPE_STRING);
			try {
				Object name = record.getProperty(new SubstancePublicName());
				cell.setCellValue(name == null ? record.getProperty(
						new SubstanceName()).toString() : name.toString());
			} catch (Exception x) {

			}

			for (Property p : record.getProperties())
				if (p.getLabel().startsWith(prefix)) {
					Integer colIndex = mergedProperties.get(p.getLabel());
					if (colIndex == null) {
						int last = mergedProperties.size();
						mergedProperties.put(p.getLabel(), last);
						colIndex = last;
					}
					Cell pcell = row.getCell(colIndex
							+ _columns.component.ordinal() + 1);
					if (pcell == null) {

						pcell = row.createCell(colIndex
								+ _columns.component.ordinal() + 1);

						pcell.setCellStyle(style);
						pcell.setCellType(Cell.CELL_TYPE_STRING);

						Cell hcell = sheet.getRow(0).createCell(
								colIndex + _columns.component.ordinal() + 1);
						hcell.setCellType(Cell.CELL_TYPE_STRING);
						String h = p.getLabel().replace(prefix, "");
						try {
							Protocol._categories c = Protocol._categories
									.valueOf(h + "_SECTION");
							hcell.setCellValue(c.getNumber() + ". "
									+ c.toString());
						} catch (Exception x) {
							hcell.setCellValue(h);
						}
					}
					Object value = record.getProperty(p);

					try {

						String cellvalue = formatter.format(
								(SubstanceProperty) p, value);

						_r_flags studyResultType = ((SubstanceProperty) p)
								.getStudyResultType();
						String flag = (studyResultType == null) ? ""
								: (_r_flags.experimentalresult
										.equals(studyResultType)) ? ""
										: (" ("
												+ studyResultType
														.getIdentifier() + ")");

						if (pcell.getStringCellValue() == null
								|| "".equals(pcell.getStringCellValue() == null))
							pcell.setCellValue(cellvalue + flag);
						else
							pcell.setCellValue(pcell.getStringCellValue()
									+ "\n" + cellvalue + flag);
					} catch (Exception x) {
						if (value instanceof IValue) {
							value = p.getName()
									+ "="
									+ ((IValue) value).toHumanReadable()
									+ ((p.getUnits() != null) ? p.getUnits()
											: "")
									+ (p.getReference().getURL() != null ? ("["
											+ p.getReference().getURL() + "]")
											: "");
						} else if (value instanceof MultiValue) {
							StringBuilder b = new StringBuilder();
							for (int i = 0; i < ((MultiValue) value).size(); i++) {
								if (i > 0)
									b.append("\n");
								b.append(p.getName());
								b.append("=");
								b.append(((IValue) ((MultiValue) value).get(i))
										.toHumanReadable());
								if (p.getUnits() != null)
									b.append(p.getUnits());
							}
							if (p.getReference().getURL() != null) {
								b.append(" [");
								b.append(p.getReference().getURL());
								b.append("]");
							}
							value = b.toString();
						} else {
							value = p.getName()
									+ "="
									+ value.toString()
									+ ((p.getUnits() != null) ? p.getUnits()
											: "")
									+ (p.getReference().getURL() != null ? ("["
											+ p.getReference().getURL() + "]")
											: "");
						}

						pcell.setCellValue(String.format(
								"%s%s%s",
								pcell.getStringCellValue() == null ? "" : pcell
										.getStringCellValue(), pcell
										.getStringCellValue() == null ? ""
										: "\n", value.toString()));
					}

				}
			//
			if (record.getRelatedStructures() != null) {

				for (CompositionRelation relation : record
						.getRelatedStructures()) {

					Row structure = (r == 0) ? row : sheet
							.createRow((short) (rowIndex + r));
					try {
						structure.setHeightInPoints(new Float(d.getHeight()
								+ offset));

						addPicture(relation.getSecondStructure(),
								structure.getRowNum(),
								_columns.diagram.ordinal());

					} catch (Exception x) {
						x.printStackTrace();
					}
					Cell cellStruc = structure.createCell(_columns.component
							.ordinal());
					cellStruc.setCellValue(relation.getRelationType()
							.toHumanReadable());
					r++;
				}
			}

		}

		if (r > 1) {
			for (int c = 0; c < mergedProperties.size(); c++) {
				sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex
						+ r - 1, c + _columns.component.ordinal() + 1, c
						+ _columns.component.ordinal() + 1));
			}
			rowIndex += r;
		} else
			rowIndex++;
		return item;
	}

	@Override
	protected void configureProcessors(String baseRef, boolean includeMol) {
		if (includeMol) {
			RetrieveStructure r = new RetrieveStructure();
			r.setPage(0);
			r.setPageSize(1);
			getProcessors().add(new ProcessorStructureRetrieval(r));
		}
		configurePropertyProcessors();
		getProcessors().add(
				new ProcessorStructureRetrieval(new QuerySmilesByID()));
		// configureCollectionProcessors(baseRef);
		getProcessors()
				.add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
					private static final long serialVersionUID = -7316415661186235101L;

					@Override
					public IStructureRecord process(IStructureRecord target)
							throws Exception {
						processItem(target);
						return target;
					};
				});

	};

	protected void addPicture(IStructureRecord record, int row, int column)
			throws AmbitException {
		record = component.process(record);
		Object o = imageReporter.processItem(record);
		try {
			CachedImage<BufferedImage> img = (CachedImage<BufferedImage>) o;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img.getImage(), "png", baos);
			baos.flush();
			baos.close();
			addPicture(baos.toByteArray(), row, column);
		} catch (IOException x) {
			throw new AmbitException(x);
		} finally {

		}
	}

	protected void addPicture(byte[] bytes, int row, int column)
			throws IOException {
		int pictureIdx = -1;
		pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
		// https://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/ss/examples/AddDimensionedImage.java
		// Create an anchor that is attached to the worksheet
		ClientAnchor anchor = drawing.createAnchor(offset, offset, 1023, 255,
				column, row, column, row);
		anchor.setAnchorType(3); // EXPAND_ROW_AND_COLUMN
		anchor.setAnchorType(ClientAnchor.MOVE_AND_RESIZE);
		// Creates a picture
		Picture pict = drawing.createPicture(anchor, pictureIdx);
		// sheet.autoSizeColumn(column, true);
		pict.resize();
	}

}
