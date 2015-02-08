package ambit2.rest.dataset;

import java.util.logging.Level;

import javax.xml.stream.XMLStreamWriter;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.QueryStaXReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

/**
 * <pre>
 * <rdf:RDF
 *     xmlns:ac="http://apps.ideaconsult.net:8080/ambit2/compound/"
 *     xmlns:ot="http://www.opentox.org/api/1.1#"
 *     xmlns:bx="http://purl.org/net/nknouf/ns/bibtex#"
 *     xmlns:otee="http://www.opentox.org/echaEndpoints.owl#"
 *     xmlns:dc="http://purl.org/dc/elements/1.1/"
 *     xmlns:ar="http://apps.ideaconsult.net:8080/ambit2/reference/"
 *     xmlns="http://apps.ideaconsult.net:8080/ambit2/"
 *     xmlns:am="http://apps.ideaconsult.net:8080/ambit2/model/"
 *     xmlns:af="http://apps.ideaconsult.net:8080/ambit2/feature/"
 *     xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
 *     xmlns:ad="http://apps.ideaconsult.net:8080/ambit2/dataset/"
 *     xmlns:ag="http://apps.ideaconsult.net:8080/ambit2/algorithm/"
 *     xmlns:owl="http://www.w3.org/2002/07/owl#"
 *     xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
 *     xmlns:ota="http://www.opentox.org/algorithmTypes.owl#"
 *     xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
 *   xml:base="http://apps.ideaconsult.net:8080/ambit2/">
 *   <owl:Class rdf:about="http://www.opentox.org/api/1.1#Dataset"/>
 *   <owl:Class rdf:about="http://www.opentox.org/api/1.1#Compound"/>
 *   <owl:Class rdf:about="http://www.opentox.org/api/1.1#Feature"/>
 *   <owl:Class rdf:about="http://www.opentox.org/api/1.1#FeatureValue"/>
 *   <owl:Class rdf:about="http://www.opentox.org/api/1.1#DataEntry"/>
 *   <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#dataEntry"/>
 *   <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#compound"/>
 *   <ot:Dataset rdf:about="dataset/112">
 *     <ot:dataEntry>
 *       <ot:DataEntry>
 *         <ot:compound>
 *           <ot:Compound rdf:about="compound/147678/conformer/419677"/>
 *         </ot:compound>
 *       </ot:DataEntry>
 *     </ot:dataEntry>
 *   </ot:Dataset>
 *   <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/description"/>
 *   <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/type"/>
 *   <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/title"/>
 * </rdf:RDF>
 * </pre>
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class DatasetRDFStaxReporter<Q extends IQueryRetrieval<IStructureRecord>> extends
	QueryStaXReporter<IStructureRecord, Q, DatasetRDFWriter> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1676895914680469360L;

    public DatasetRDFStaxReporter(String prefix, Request request, Template template, Profile groupedProperties) {
	super(prefix, request);
	recordWriter.setGroupProperties(groupedProperties);
	recordWriter.setTemplate(template == null ? new Template(null) : template);
	initProcessors();

    }

    @Override
    public void setLicenseURI(String licenseURI) {
	recordWriter.setLicenseURI(licenseURI);
    }

    @Override
    public String getLicenseURI() {
	return recordWriter.getLicenseURI();
    }

    @Override
    protected DatasetRDFWriter createRecordWriter(Request request, ResourceDoc doc) {
	return new DatasetRDFWriter(new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(compoundInDatasetPrefix,
		request, false), new PropertyURIReporter(request));
    }

    @Override
    public void setOutput(XMLStreamWriter output) throws Exception {
	super.setOutput(output);
	recordWriter.setOutput(output);
    }

    @Override
    public void close() throws Exception {
	try {
	    recordWriter.setOutput(null);
	} catch (Exception x) {
	}
	super.close();
    }

    @Override
    protected QueryURIReporter<IStructureRecord, IQueryRetrieval<IStructureRecord>> createURIReporter(Request req,
	    ResourceDoc doc) {
	return new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(compoundInDatasetPrefix, req, false);
    }

    protected void initProcessors() {

	getProcessors().clear();
	if ((recordWriter.getGroupProperties() != null) && (recordWriter.getGroupProperties().size() > 0))
	    getProcessors()
		    .add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(recordWriter
			    .getGroupProperties())) {
			/**
			     * 
			     */
			private static final long serialVersionUID = -2319988961094728115L;

			@Override
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
			    ((RetrieveGroupedValuesByAlias) getQuery()).setRecord(target);
			    return super.process(target);
			}
		    });

	if ((recordWriter.getTemplate() != null) && (recordWriter.getTemplate().size() > 0))
	    getProcessors().add(
		    new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty, recordWriter
			    .getTemplate(), true)) {
			/**
			     * 
			     */
			private static final long serialVersionUID = 2528074455468780353L;

			@Override
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
			    ((RetrieveProfileValues) getQuery()).setRecord(target);
			    return super.process(target);
			}
		    });

	getProcessors().add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = 3131128715226486359L;

	    public IStructureRecord process(IStructureRecord target) throws AmbitException {
		try {
		    processItem(target);
		} catch (Exception x) {
		    logger.log(Level.WARNING, x.getMessage(), x);
		}
		return target;
	    };
	});
    }

    @Override
    public Object processItem(IStructureRecord item) throws AmbitException {
	return recordWriter.process(item);

    }

    public void open() throws DbAmbitException {

    }

    protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch(Q query) {
	if (query.isPrescreen()) {
	    DbReader<IStructureRecord> reader = new DbReaderStructure();
	    reader.setHandlePrescreen(true);
	    return reader;
	} else
	    return super.createBatch(query);
    };

    @Override
    public String getFileExtension() {
	return "rdf";
    }
}
