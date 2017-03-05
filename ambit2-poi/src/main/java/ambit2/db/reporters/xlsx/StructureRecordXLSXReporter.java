package ambit2.db.reporters.xlsx;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

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
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

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
	protected Dimension d = new Dimension(125, 125);
	protected int offset = 0;
	protected int rowIndex = 0;
	protected StudyFormatter formatter;
	protected Map<String, Integer> mergedProperties = new HashMap<String, Integer>();
	protected CellStyle style, hstyle, blueStyle;
	protected Font blackFont, blueFont, redFont;
	protected boolean autosizecolumn = false;

	public boolean isAutosizeColumns() {
		return autosizecolumn;
	}

	public void setAutosizeColumns(boolean autosizecolumn) {
		this.autosizecolumn = autosizecolumn;
	}

	protected AddDimensionedImage imgHelper = new AddDimensionedImage();

	public StructureRecordXLSXReporter(String baseRef, boolean hssf,
			Template template, Profile groupedProperties,
			SubstanceEndpointsBundle[] bundles, String urlPrefix,
			boolean includeMol,String configResource) {
		super(baseRef, hssf, template, groupedProperties, bundles, urlPrefix,
				includeMol);

		RetrieveStructure q = new RetrieveStructure(true);
		q.setFieldname(true);
		q.setPage(0);
		q.setPageSize(1);
		component = new ProcessorStructureRetrieval(q);

		imageReporter = new ImageReporter<Q>("image", "png", d);
		formatter = new StudyFormatter(configResource);

		style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		blackFont = workbook.createFont();
		blackFont.setColor(IndexedColors.BLACK.getIndex());
		style.setFont(blackFont);

		blueStyle = workbook.createCellStyle();
		blueStyle.setWrapText(true);
		blueStyle.setAlignment(CellStyle.ALIGN_LEFT);
		blueStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		blueFont = workbook.createFont();
		blueFont.setColor(IndexedColors.BLUE.getIndex());
		blueStyle.setFont(blueFont);
		redFont = workbook.createFont();
		redFont.setColor(IndexedColors.RED.getIndex());

		hstyle = workbook.createCellStyle();
		hstyle.setWrapText(true);
		hstyle.setAlignment(CellStyle.ALIGN_LEFT);
		hstyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		hstyle.setBorderBottom(HSSFCellStyle.BORDER_THICK);
		hstyle.setBorderLeft(HSSFCellStyle.BORDER_THICK);
		hstyle.setBorderRight(HSSFCellStyle.BORDER_THICK);
		hstyle.setBorderTop(HSSFCellStyle.BORDER_THICK);
		Font headerFont = workbook.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		hstyle.setFont(headerFont);
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
		row.setRowStyle(hstyle);

		for (_columns col : _columns.values()) {
			Cell cell = row.createCell(col.ordinal());
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellStyle(hstyle);
			cell.setCellValue(col.toString());

			sheet.autoSizeColumn(col.ordinal(), col.autoSize());
		}
		initColumns(_columns.component.ordinal() + 1);
		rowIndex++;
	}

	@Override
	public void footer(OutputStream output, Q query) {
		// DO the autosizecolumn here, otherwise it is extremely slow
		super.footer(output, query);
	}
	protected void initColumns(int afterCol) {
		/*
		 * for (I5_ROOT_OBJECTS section : I5_ROOT_OBJECTS.values()) { if
		 * (section.isIUCLID5() && section.isScientificPart() &&
		 * section.isSupported() && !section.isNanoMaterialTemplate()) { int
		 * last = mergedProperties.size(); mergedProperties.put(
		 * "http://www.opentox.org/echaEndpoints.owl#" + section.name(), last);
		 * Cell hcell = sheet.getRow(0).createCell( last +
		 * _columns.component.ordinal() + 1); hcell.setCellStyle(hstyle);
		 * hcell.setCellType(Cell.CELL_TYPE_STRING);
		 * hcell.setCellValue(section.getNumber() + ". " + section.getTitle());
		 * 
		 * sheet.autoSizeColumn(hcell.getColumnIndex(), true); } // }
		 */

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

			@Override
			public boolean autoSize() {
				return false;
			}
		},
		component {
			@Override
			public String toString() {
				return "Contained as";
			}
		};
		public boolean autoSize() {
			return true;
		}
	}

	private static final String prefix = "http://www.opentox.org/echaEndpoints.owl#";

	@Override
	public Object processItem(IStructureRecord item) throws Exception {
		Row row = sheet.createRow(rowIndex);
		row.setHeightInPoints(new Float(d.getHeight() + 2 * offset));

		int r = 0;
		if (item instanceof SubstanceRecord) {
			SubstanceRecord record = (SubstanceRecord) item;
			Cell cell = row.createCell(_columns.tag.ordinal());
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style);
			cell.setCellValue(rowIndex);

			cell = row.createCell(_columns.name.ordinal());
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellStyle(style);
			try {
				Object name = record
						.getRecordProperty(new SubstancePublicName());
				cell.setCellValue(name == null ? record.getRecordProperty(
						new SubstanceName()).toString() : name.toString());
			} catch (Exception x) {

			}

			for (Property p : record.getRecordProperties())
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

						pcell.setCellStyle(blueStyle);
						pcell.setCellType(Cell.CELL_TYPE_STRING);

						Cell hcell = sheet.getRow(0).createCell(
								colIndex + _columns.component.ordinal() + 1);
						hcell.setCellType(Cell.CELL_TYPE_STRING);
						hcell.setCellStyle(hstyle);
						String h = p.getLabel().replace(prefix, "");
						try {
							Protocol._categories c = Protocol._categories
									.valueOf(h + "_SECTION");
							hcell.setCellValue(c.getNumber() + ". "
									+ c.toString());
						} catch (Exception x) {
							hcell.setCellValue(h);
						}
						if (isAutosizeColumns()) 
							sheet.autoSizeColumn(hcell.getColumnIndex(), true);
						sheet.setColumnHidden(hcell.getColumnIndex(),false);

					}
					Object value = record.getRecordProperty(p);

					try {

						String cellvalue = formatter.format(
								(SubstanceProperty) p, value);

						if (cellvalue == null || "".equals(cellvalue.trim())) {
							// do nothing
						} else {
							_r_flags studyResultType = ((SubstanceProperty) p)
									.getStudyResultType();

							String flag = "";
							Font font = redFont;
							if (studyResultType != null)
								switch (studyResultType) {
								case experimentalresult: {
									font = blueFont;
									break;
								}
								case other: {
									font = blackFont;
									break;
								}
								case unsupported: {
									font = blackFont;
									break;
								}
								case NOTSPECIFIED: {
									font = blackFont;
									break;
								}
								case nodata: {
									font = blackFont;
									break;
								}
								default: {
									font = redFont;
									flag = studyResultType.getIdentifier()
											+ ": ";
								}
								}

							RichTextString rtvalue = pcell
									.getRichStringCellValue();
							if (rtvalue == null || rtvalue.length() == 0
									|| "".equals(rtvalue.getString().trim())) {
								pcell.setCellValue(createRichTextString(flag
										+ cellvalue, font));
							} else {
								if (rtvalue instanceof XSSFRichTextString) {
									((XSSFRichTextString) rtvalue)
											.append("\n" + flag + cellvalue,
													(XSSFFont) font);
								} else {
									pcell.setCellValue(createRichTextString(
											pcell.getStringCellValue() + "\n"
													+ flag + cellvalue, font));
								}
							}
							if (isAutosizeColumns())
								sheet.autoSizeColumn(pcell.getColumnIndex(),
										true);
						}
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
						structure.setHeightInPoints(new Float(d.getHeight() + 2
								* offset));

						addPicture(relation.getSecondStructure(),
								structure.getRowNum(),
								_columns.diagram.ordinal());

					} catch (Exception x) {
						x.printStackTrace();
					}
					Cell cellStruc = structure.createCell(_columns.component
							.ordinal());
					cellStruc.setCellStyle(style);
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
		// https://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/ss/examples/AddDimensionedImage.java
		// Create an anchor that is attached to the worksheet
		/*
		 * ClientAnchor anchor = drawing.createAnchor(offset, offset, -offset,
		 * -offset, column, row, column + 1, row + 1);
		 * anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE);
		 */
		imgHelper.addImageToSheet(column, row, sheet, drawing, bytes,
				Workbook.PICTURE_TYPE_PNG, d.width, d.height,
				AddDimensionedImage.EXPAND_ROW_AND_COLUMN);
	}

}

