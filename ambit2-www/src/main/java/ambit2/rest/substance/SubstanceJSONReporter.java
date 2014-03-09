package ambit2.rest.substance;

import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.json.JSONUtils;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IParameterizedQuery;
import ambit2.db.search.IQueryCondition;
import ambit2.db.substance.ids.ReadSubstanceIdentifiers;
import ambit2.rest.ResourceDoc;

public class SubstanceJSONReporter<Q extends IQueryRetrieval<SubstanceRecord>> extends SubstanceURIReporter<Q> {
	protected String comma = null;
	protected String jsonpCallback = null;
	enum jsonSubstance {
		URI,
		externalIdentifiers,
		i5uuid,
		documentType,
		format,
		name,
		publicname,
		content,
		substanceType,
		referenceSubstance,
		ownerUUID,
		ownerName;
		public String jsonname() {
			return name();
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 2315457985592934727L;
	public SubstanceJSONReporter(Request request, ResourceDoc doc,String jsonpCallback) {
		super(request, doc);
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpCallback);
		
		getProcessors().clear();
		IQueryRetrieval<ExternalIdentifier> queryP = new ReadSubstanceIdentifiers(); 
		MasterDetailsProcessor<SubstanceRecord,ExternalIdentifier,IQueryCondition> idReader = 
							new MasterDetailsProcessor<SubstanceRecord,ExternalIdentifier,IQueryCondition>(queryP) {
			@Override
			protected void configureQuery(
							SubstanceRecord target,
							IParameterizedQuery<SubstanceRecord, ExternalIdentifier, IQueryCondition> query)
							throws AmbitException {
							query.setFieldname(target);
			}
			@Override
			protected SubstanceRecord processDetail(
							SubstanceRecord target,
							ExternalIdentifier detail)	throws Exception {
				if (target.getExternalids()==null) target.setExternalids(new ArrayList<ExternalIdentifier>());
						target.getExternalids().add(detail);
				return target;
			}
		};
		idReader.setCloseConnection(false);
		getProcessors().add(idReader);
		getProcessors().add(new DefaultAmbitProcessor<SubstanceRecord,SubstanceRecord>() {
			public SubstanceRecord process(SubstanceRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});			
	}
	public void header(java.io.Writer output, Q query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}
			output.write("{\n");
			output.write("\n\"substance\":[");
			
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	};
	
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n\t]");
		} catch (Exception x) {}
		try {
			output.write("\n}\n");
			
			if (jsonpCallback!=null) {
				output.write(");");
			}
		} catch (Exception x) {}
	};
	
	@Override
	public Object processItem(SubstanceRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			String uri = getURI(item);
						
			StringBuilder builder = new StringBuilder();
			if (comma!=null) builder.append(comma);
			
			builder.append("\n\t{\n");
			builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.URI.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri))));
			builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.ownerUUID.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getOwnerUUID()))));
			builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.ownerName.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getOwnerName()))));
			builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.i5uuid.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getCompanyUUID()))));
			builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.name.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getCompanyName()))));
			builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.publicname.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getPublicName()))));
			builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.format.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getFormat()))));
			builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.substanceType.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getSubstancetype()))));
			builder.append(String.format("\t\t\"%s\": {\n\t\t\t\"%s\":%s,\n\t\t\t\"%s\":%s\n\t\t},\n",
					jsonSubstance.referenceSubstance.name(),
					jsonSubstance.i5uuid.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getReferenceSubstanceUUID())),
					"uri",
					JSONUtils.jsonQuote(
						getBaseReference()+"/query/compound/search/all?search="+
						JSONUtils.jsonEscape(item.getReferenceSubstanceUUID())
					)
					));
			builder.append(String.format("\t\t\"%s\":[\n",jsonSubstance.externalIdentifiers.name()));
			if (item.getExternalids()!=null) {
				String d = "";
				for (ExternalIdentifier id : item.getExternalids()) {
					builder.append(d);
					builder.append(id.toString());
					d = ",";
				}	
			}	
			builder.append("\t]\n");
			builder.append("\t}");
			
			writer.write(builder.toString());
			
			comma = ",";
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return item;
	}

}
