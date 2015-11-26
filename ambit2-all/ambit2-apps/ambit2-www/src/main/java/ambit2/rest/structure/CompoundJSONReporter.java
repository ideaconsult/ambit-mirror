package ambit2.rest.structure;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.data.study.IValue;
import ambit2.base.data.study.MultiValue;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.json.JSONUtils;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.facets.compounds.ChemicalRoleByBundle;
import ambit2.db.facets.compounds.CollectionsByChemical;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.search.QuerySmilesByID;
import ambit2.rest.property.PropertyJSONReporter;

/**
 * JSON
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class CompoundJSONReporter<Q extends IQueryRetrieval<IStructureRecord>> extends CSVReporter<Q> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 410930501401847402L;
    protected String comma = null;

    public String getComma() {
	return comma;
    }

    public void setComma(String comma) {
	this.comma = comma;
    }

    protected String jsonpCallback = null;
    protected PropertyJSONReporter propertyJSONReporter;

    public PropertyJSONReporter getPropertyJSONReporter() {
	return propertyJSONReporter;
    }

    protected String hilightPredictions = null;
    protected MoleculeReader reader = null;

    public String getHilightPredictions() {
	return hilightPredictions;
    }

    public void setHilightPredictions(String hilightPredictions) {
	this.hilightPredictions = hilightPredictions;
    }

    enum jsonCompound {
	URI, mol, structype, compound, dataset, dataEntry, values, facets, bundles, composition;

	public String jsonname() {
	    return name();
	}
    }

    public CompoundJSONReporter(Template template, Request request, String urlPrefix, Boolean includeMol,
	    String jsonpCallback) {
	this(template, null, null, null, request, urlPrefix, includeMol, jsonpCallback);
    }

    public CompoundJSONReporter(Template template, Profile groupedProperties, String[] folders,
	    SubstanceEndpointsBundle[] bundles, Request request, String urlPrefix, Boolean includeMol,
	    String jsonpCallback) {
	super(request.getRootRef().toString(), template, groupedProperties, folders, bundles, urlPrefix, includeMol);
	this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpCallback);

	propertyJSONReporter = new PropertyJSONReporter(request);
	hilightPredictions = request.getResourceRef().getQueryAsForm().getFirstValue("model_uri");
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
		configureCollectionProcessors(baseRef);
		getProcessors()
				.add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
					/**
	     * 
	     */
					private static final long serialVersionUID = -7316415661186235101L;

					public IStructureRecord process(IStructureRecord target)
							throws AmbitException {
						processItem(target);
						return target;
					};
				});
    };

    @Override
    protected void configureCollectionProcessors(String baseURI) {
	if (folders != null && folders.length > 0) {
	    CollectionsByChemical collections = new CollectionsByChemical(null);
	    collections.setValue(folders);
	    MasterDetailsProcessor<IStructureRecord, IFacet<String>, IQueryCondition> facetReader = new MasterDetailsProcessor<IStructureRecord, IFacet<String>, IQueryCondition>(
		    collections) {
		/**
			 * 
			 */
		private static final long serialVersionUID = 3644502232491880656L;

		@Override
		protected IStructureRecord processDetail(IStructureRecord master, IFacet<String> detail)
			throws Exception {
		    master.clearFacets();
		    master.addFacet(detail);
		    return master;
		}
	    };
	    facetReader.setCloseConnection(false);
	    getProcessors().add(facetReader);
	}
	if (bundles != null && bundles.length > 0) {
	    ChemicalRoleByBundle q = new ChemicalRoleByBundle(baseURI);
	    q.setValue(bundles[0]);
	    MasterDetailsProcessor<IStructureRecord, BundleRoleFacet, IQueryCondition> bundleReader = new MasterDetailsProcessor<IStructureRecord, BundleRoleFacet, IQueryCondition>(
		    q) {
		/**
			 * 
			 */
		private static final long serialVersionUID = -5489861883749717778L;

		@Override
		protected IStructureRecord processDetail(IStructureRecord master, BundleRoleFacet detail)
			throws Exception {
		    master.clearFacets();
		    master.addFacet(detail);
		    return master;
		}

		@Override
		public IStructureRecord process(IStructureRecord target) throws AmbitException {
		    return super.process(target);
		}
	    };
	    bundleReader.setCloseConnection(false);
	    getProcessors().add(bundleReader);
	}

    }

    @Override
    public void setOutput(Writer output) throws Exception {
	super.setOutput(output);
	propertyJSONReporter.setOutput(output);
    }

    @Override
    protected void writeHeader(Writer writer) throws IOException {
	if (header == null) {
	    header = template2Header(template, true);
	    /*
	     * writer.write("@attribute URI string\n"); for (Property p :
	     * header) { writer.write(getPropertyHeader(p)); }
	     * 
	     * writer.write("\n@data\n");
	     */
	}
    }

    protected List<Property> template2Header(Template template, boolean propertiesOnly) {
	List<Property> h = new ArrayList<Property>();
	Iterator<Property> it;
	if (groupProperties != null) {
	    it = groupProperties.getProperties(true);
	    while (it.hasNext()) {
		Property t = it.next();
		h.add(t);
	    }
	}
	it = template.getProperties(true);
	while (it.hasNext()) {
	    Property t = it.next();
	    if (!propertiesOnly || (propertiesOnly && (t.getId() > 0)))
		h.add(t);
	}

	Collections.sort(h, new Comparator<Property>() {
	    public int compare(Property o1, Property o2) {
		return Integer.toString(o1.getId()).compareTo(Integer.toString(o2.getId()));
		// mimic URI comparison as strings
	    }
	});

	/*
	 * Collections.sort(h,new Comparator<Property>() { public int
	 * compare(Property o1, Property o2) { return
	 * o1.getOrder()-o2.getOrder(); } });
	 */
	return h;
    }

    protected void append2header(Writer writer, IStructureRecord item) {

    }

    @Override
    public Object processItem(IStructureRecord item) throws AmbitException {
	try {
	    Writer writer = getOutput();

	    writeHeader(writer);
	    append2header(writer, item);
	    
	    int i = 0;
	    String uri = getURI(item);

	    StringBuilder builder = new StringBuilder();
	    if (comma != null)
		builder.append(comma);

	    builder.append("\n\t{\n");
	    builder.append(String.format("\t\"%s\":{\n", jsonCompound.compound.jsonname()));
	    builder.append(String.format("\t\t\"%s\":\"%s\",\n", jsonCompound.URI.jsonname(), uri));
	    builder.append(String.format("\t\t\"%s\":\"%s\",\n", jsonCompound.structype.jsonname(), item.getType()
		    .name()));
	    if (includeMol)
		if (item.getContent() == null)
		    builder.append(String.format("\t\t\"%s\":null,\n", jsonCompound.mol.jsonname()));
		else
		    builder.append(String.format("\t\t\"%s\":\"%s\",\n", jsonCompound.mol.jsonname(),
			    JSONUtils.jsonEscape(getSDFContent(item))));
	    // similarity
	    Object similarityValue = null;
	    for (Property p : item.getRecordProperties())
		if ("metric".equals(p.getName())) {
		    similarityValue = item.getRecordProperty(p);
		    break;
		}
	    builder.append(String.format("\t\t\"metric\":%s,\n", similarityValue));

	    builder.append(String.format("\t\t\"%s\":\"\",\n", "name")); // placeholders
	    builder.append(String.format("\t\t\"%s\":\"\",\n", "cas"));
	    builder.append(String.format("\t\t\"%s\":\"\"\n", "einecs"));
	    if (item.getInchiKey() != null)
		builder.append(String.format(",\t\t\"%s\":\"%s\"\n", "inchikey", item.getInchiKey()));
	    if (item.getInchi() != null)
		builder.append(String.format(",\t\t\"%s\":\"%s\"\n", "inchi", JSONUtils.jsonEscape(item.getInchi())));
	    if (item.getFormula() != null)
		builder.append(String.format(",\t\t\"%s\":\"%s\"", "formula", JSONUtils.jsonEscape(item.getFormula())));

	    builder.append("\n\t\t},\n");

	    builder.append(String.format("\t\"%s\":{\n", jsonCompound.values.jsonname()));
	    String comma1 = null;
	    for (int j = 0; j < header.size(); j++) {

		Property p = header.get(j);
		Object value = item.getRecordProperty(p);

		String key = propertyJSONReporter.getURI(p);
		if (key.contains("cdk:Title") || key.contains("cdk:Formula"))
		    continue;
		if (key.contains("SMARTSProp"))
		    continue;
		if (value == null) {
		    continue; // builder.append(String.format("\t\t\"%s\":null",key));
		}
		if (comma1 != null) {
		    builder.append(comma1);
		    builder.append("\n");
		}
		if (value instanceof Double) {
		    if (((Double) value).isNaN())
			builder.append(String.format(Locale.ENGLISH, "\t\t\"%s\":null", key));
		    else
			builder.append(String.format(Locale.ENGLISH, "\t\t\"%s\":%6.3f", key, (Double) value));			
		} else if (value instanceof Integer)
		    builder.append(String.format("\t\t\"%s\":%d", key, (Integer) value));
		else if (value instanceof Long)
		    builder.append(String.format("\t\t\"%s\":%l", key, (Long) value));
		else if (value instanceof IValue)
		    builder.append(String.format("\t\t\"%s\":%s", key, ((IValue) value)).toString());
		else if (value instanceof MultiValue) {
		    StringBuilder b = new StringBuilder();
		    String delimiter = "[";
		    for (IValue v : (MultiValue<IValue>) value) {
			b.append(delimiter);
			b.append(v.toString());
			delimiter = ",";
		    }
		    b.append("]\n");
		    builder.append(String.format("\t\t\"%s\":%s", key, b.toString()));
		} else
		    builder.append(String.format("\t\t\"%s\":\"%s\"", key,
			    JSONUtils.jsonEscape(value.toString().replace("\n", "|"))));
		i++;
		comma1 = ",";
	    }
	    builder.append("\n\t\t},\n");

	    builder.append(String.format("\t\"%s\":[\n", jsonCompound.facets.jsonname()));
	    Iterable<IFacet> facets = item.getFacets();
	    String delimiter = "";
	    if (facets != null)
		for (IFacet facet : facets) {
		    if (facet instanceof BundleRoleFacet)
			continue;
		    if (facet.getValue() == null)
			continue;
		    builder.append(delimiter);
		    builder.append(String.format("\t\t{\"%s\":%d}", facet.getValue() == null ? "" : facet.getValue(),
			    facet.getCount()));
		    delimiter = ",";
		}
	    builder.append("\n\t\t],");

	    if ((item instanceof SubstanceRecord) && ((SubstanceRecord) item).getRelatedStructures() != null) {
		SubstanceRecord substance = (SubstanceRecord) item;
		builder.append(String.format("\n\t%s:[\n", JSONUtils.jsonQuote(jsonCompound.composition.jsonname())));
		List<CompositionRelation> composition = substance.getRelatedStructures();
		for (int j = 0; j < composition.size(); j++) {
		    CompositionRelation cr = composition.get(j);
		    if (j > 0)
			builder.append(",\n");
		    String component = "{}";
		    if (cr.getSecondStructure() != null && cr.getSecondStructure().getIdchemical() > 0) {
			// StringBuilder bundles = new StringBuilder();
			// printBundles(cr.getSecondStructure().getFacets(),
			// bundles);

			Writer o = getOutput();
			String savecomma = comma;
			StringWriter w = new StringWriter();
			setOutput(w);
			setComma(null);
			processItem(cr.getSecondStructure());
			setOutput(o);
			setComma(savecomma);
			// System.out.println(w);
			component = w.toString();
			/*
			 * component = String.format(
			 * "{\"compound\":{\"URI\":\"%s/compound/%d\",\n\t%s}}",
			 * urlPrefix
			 * ,cr.getSecondStructure().getIdchemical(),bundles
			 * .toString());
			 */
		    }
		    builder.append(cr.toJSON(uri, component));

		}
		builder.append("\n\t\t],");
	    }

	    printBundles(item.getFacets(), builder);

	    builder.append("\n\t}");
	    writer.write(builder.toString());
	    comma = ",";
	} catch (Exception x) {
	    logger.log(java.util.logging.Level.SEVERE, x.getMessage(), x);
	}
	return item;

    }

    protected void printBundles(Iterable<IFacet> facets, StringBuilder builder) {
	builder.append(String.format("\n\t%s:{\n", JSONUtils.jsonQuote(jsonCompound.bundles.jsonname())));
	String delimiter = "";
	if (facets != null)
	    for (IFacet facet : facets)
		if (facet instanceof BundleRoleFacet) {
		    builder.append(delimiter);
		    builder.append("\n\t\t");
		    builder.append(facet.toJSON(propertyJSONReporter.getRequest().getRootRef().toString(), null));
		    delimiter = ",";
		}
	builder.append("\n\t\t}");
    }

    @Override
    public void header(java.io.Writer output, Q query) {
	try {
	    if (jsonpCallback != null) {
		output.write(jsonpCallback);
		output.write("(");
	    }
	    output.write("{\n");
	    output.write("\"query\": {");
	    output.write("\n\t\"summary\":");
	    output.write("\"");
	    // output.write(query==null?"":JSONUtils.jsonEscape(query.toString()));
	    output.write("query");
	    output.write("\"");
	    output.write("\n},");
	    output.write("\n\"dataEntry\":[");

	} catch (Exception x) {
	    logger.log(Level.WARNING, x.getMessage(), x);
	}
    };

    /**
     * "{"f1":"feature1","f2":{"uri":"feature2","smth":"smb"}}"
     */
    @Override
    public void footer(java.io.Writer output, Q query) {
	try {
	    output.write("\n],");
	} catch (Exception x) {
	}

	try {
	    if (hilightPredictions == null)
		output.write(String.format("\n\"%s\":null,", "model_uri"));
	    else
		output.write(String.format("\n\"%s\":\"%s\",", "model_uri", hilightPredictions));
	    output.write("\n\"feature\":{\n");
	    if (header != null)
		for (int j = 0; j < header.size(); j++)
		    propertyJSONReporter.processItem(header.get(j));

	} catch (Exception x) {
	    // x.printStackTrace();
	} finally {
	    try {
		output.write("}\n");
	    } catch (Exception x) {
	    }
	}

	try {
	    output.write("}\n");

	    if (jsonpCallback != null) {
		output.write(");");
	    }
	} catch (Exception x) {
	}

    };

    @Override
    public String getFileExtension() {
	return null;// "json";
    }

    protected String getSDFContent(IStructureRecord item) throws AmbitException {
	// most common case
	if (MOL_TYPE.SDF.toString().equals(item.getFormat()))
	    return item.getContent();
	// otherwise
	if (reader == null)
	    reader = new MoleculeReader();
	try {
	    StringWriter w = new StringWriter();
	    SDFWriter sdfwriter = new SDFWriter(w);
	    IAtomContainer ac = reader.process(item);
	    MoleculeTools.clearProperties(ac);    
	    sdfwriter.write(ac);
	    sdfwriter.close();
	    return w.toString();
	} catch (Exception x) {
	    throw new AmbitException(x);
	}
    }

}
