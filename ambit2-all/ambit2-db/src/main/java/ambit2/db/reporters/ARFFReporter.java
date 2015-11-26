package ambit2.db.reporters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.data.study.IValue;
import ambit2.base.data.study.MultiValue;
import ambit2.base.interfaces.IStructureRecord;

public class ARFFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryHeaderReporter<Q, Writer> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 2931123688036795689L;
    protected File tempFile;
    protected Writer tmpWriter;

    protected String urlPrefix = "";
    protected String delimiter = "";

    public String getUrlPrefix() {
	return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
	this.urlPrefix = urlPrefix;
    }

    public ARFFReporter() throws IOException {
	this(null, null);
    }

    public ARFFReporter(Template template) {
	this(template, null);
    }

    public ARFFReporter(Template template, Profile groupedProperties, IProcessor... processors) {
	setGroupProperties(groupedProperties);
	setTemplate(template == null ? new Template(null) : template);
	getProcessors().clear();
	if (processors != null)
	    for (IProcessor processor : processors)
		getProcessors().add(processor);
	else
	    configureProcessors(null, false);
    }

    @Override
    public void setOutput(Writer output) throws Exception {
	super.setOutput(output);
	try {
	    tmpWriter = createTempWriter();
	} catch (Exception x) {
	    throw new AmbitException(x);
	}
    }

    protected Writer createTempWriter() throws IOException {
	tempFile = File.createTempFile("temp_", ".arff");
	tempFile.deleteOnExit();
	return new BufferedWriter(new FileWriter(tempFile));
    }

    protected void completeTheHeader() {
	try {
	    // complete the URI attribute
	    output.write("}\n");
	    for (Property p : header) {
		output.write(getPropertyHeader(p));
	    }

	    output.write("\n@data\n");
	} catch (Exception x) {
	    x.printStackTrace();
	    logger.log(Level.WARNING, x.getMessage(), x);
	} finally {
	}
    }

    @Override
    public void footer(Writer output, Q query) {

	try {
	    tmpWriter.flush();
	} catch (Exception x) {

	} finally {
	    try {
		tmpWriter.close();
	    } catch (Exception x) {
	    }
	}
	// now the header
	completeTheHeader();

	BufferedReader in = null;
	try {
	    String line;
	    in = new BufferedReader(new FileReader(tempFile));
	    while ((line = in.readLine()) != null) {
		output.write(line);
		output.write("\n");
	    }

	} catch (Exception x) {

	} finally {
	    try {
		if (in != null)
		    in.close();
	    } catch (Exception x) {
	    }
	    try {
		if (tempFile != null)
		    tempFile.delete();
	    } catch (Exception x) {
	    }
	}
    };

    protected void writeHeader(Writer writer) throws IOException {
	if (header == null) {
	    header = template2Header(template, true);

	    // in order to handle nominal attributes, will write the header at
	    // the end.
	}
    }

    protected String getPropertyHeader(Property p) {
	StringBuilder allowedValues = null;
	if (p.isNominal()) {
	    if (p.getAllowedValues() == null) {

	    } else {
		for (Comparable value : p.getAllowedValues()) {
		    if (allowedValues == null) {
			allowedValues = new StringBuilder();
			allowedValues.append("{");
		    } else
			allowedValues.append(",");
		    allowedValues.append(value);
		}
		allowedValues.append("}");
	    }
	}

	String d = p.getName().indexOf(" ") >= 0 ? "\"" : "";
	return String.format("@attribute %s%s%s %s\n", d, p.getName(), d, p.isNominal() ? allowedValues : (p.getClazz()
		.equals(Number.class) || p.getClazz().equals(MultiValue.class)) ? "numeric" : "string");
    }

    @Override
    public void header(Writer writer, Q query) {
	try {
	    if (getLicenseURI() == null)
		writer.write(String.format("@relation %s\n\n", getRelationName()));
	    else
		writer.write(String.format("@relation %s_License:_%s\n\n", getRelationName().trim().replace(" ", "_")
			.replace("?", "_").replace("&", "_"), getLicenseURI().trim().replace(" ", "_")));

	    output.write("@attribute URI {");

	} catch (IOException x) {
	    logger.log(Level.WARNING, x.getMessage(), x);
	    // TODO throw exception
	}
    };

    protected String getRelationName() {
	return "Dataset";
    }

    @Override
    public Object processItem(IStructureRecord item) throws AmbitException {
	try {
	    Writer writer = tmpWriter;
	    writeHeader(writer);
	    int i = 0;
	    String uri = null;
	    if (item instanceof SubstanceRecord) {
		uri = String.format("%s/substance/%s", urlPrefix, ((SubstanceRecord) item).getSubstanceUUID());
	    } else {
		uri = String.format("%s/compound/%d", urlPrefix, item.getIdchemical());
		if (item.getIdstructure() > 0)
		    uri = String.format("%s/conformer/%d", uri, item.getIdstructure());
	    }

	    output.append(delimiter);
	    output.append(uri);
	    delimiter = ",";
	    writer.write(uri);

	    for (Property p : header) {
		Object value = item.getRecordProperty(p);
		if (value instanceof MultiValue) {
		    MultiValue<IValue> mv = (MultiValue<IValue>) value;
		    for (IValue m : mv) {
			writer.write(String.format(",%s", m.getLoValue()));
			break;
		    }
		} else {
		    if ((value != null) && p.isNominal()) {
			p.addAllowedValue(value.toString());
		    }
		    if (p.isNominal())
			writer.write(String.format(",%s",
				(value == null) || (IQueryRetrieval.NaN.equals(value.toString())) ? "?" : value));
		    else if (p.getClazz() == Number.class) {
			writer.write(String.format(",%s",
				(value == null) || (value instanceof String) || (IQueryRetrieval.NaN.equals(value.toString())) ? "?" : value));

		    } else
			writer.write(String.format(",%s%s%s", value == null ? "" : "\"", value == null ? "?" : value
				.toString().replace("\n", "").replace("\r", ""), value == null ? "" : "\""));
		}
		i++;
	    }
	    writer.write('\n');
	} catch (Exception x) {
	    logger.log(java.util.logging.Level.SEVERE, x.getMessage(), x);
	}
	return null;

    }

    public void open() throws DbAmbitException {
	// TODO Auto-generated method stub

    }

    @Override
    public String getFileExtension() {
	return "arff";
    }

}