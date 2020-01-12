package ambit2.rest.substance.templates;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import net.enanomapper.maker.TR;
import net.enanomapper.maker.TemplateMakerExtended;
import net.enanomapper.maker.TemplateMakerSettings;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_CMD;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_TYPE;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryReporter;

public class AssayTemplateEntrySpreadsheetReporter<Q extends IQueryRetrieval<TR>>
		extends QueryReporter<TR, Q, OutputStream> {
	protected final HashSet<String> templateids = new HashSet<String>();
	protected final List<TR> records = new ArrayList<TR>();
	protected _TEMPLATES_TYPE templates_type; 
	/**
	 * 
	 */
	private static final long serialVersionUID = -4995013793566213468L;

	public AssayTemplateEntrySpreadsheetReporter(_TEMPLATES_TYPE templates_type) {
		this.templates_type = templates_type;
	}
	@Override
	public Object processItem(TR item) throws Exception {
		templateids.add(TR.hix.id.get(item).toString());
		records.add(item);
		return item;
	}

	@Override
	public OutputStream process(Q arg0) throws Exception {
		return super.process(arg0);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		super.close();
	}

	@Override
	public void footer(OutputStream output, Q query) {
		TemplateMakerSettings settings = new TemplateMakerSettings() {
			public java.lang.Iterable<TR> getTemplateRecords() throws Exception {
				return records;
			};

			@Override
			public File write(String templateid, _TEMPLATES_TYPE ttype, Workbook workbook) throws IOException {
				try {
					workbook.write(output);
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
					workbook.close();
				}
				return null;
			}

		};

		try {
			TemplateMakerExtended maker = new TemplateMakerExtended();
			settings.setTemplatesType(templates_type);
			settings.setTemplatesCommand(_TEMPLATES_CMD.generate);
			//settings.setTemplatesType(_TEMPLATES_TYPE.jrc);
			settings.setSinglefile(true);
			// FIXME no input for generation needed, this is a placeholder
			File tmpdir = new File(System.getProperty("java.io.tmpdir"));
			settings.setInputfolder(tmpdir);
			settings.setOutputfolder(tmpdir);
			settings.setSinglefile(true);
			maker.generate(settings, templateids);
		} catch (Exception x) {
			x.printStackTrace();
		} finally {

		}
	}

	@Override
	public String getFileExtension() {
		return "xlsx";
	}

	@Override
	public void header(OutputStream output, Q query) {

	}

}
