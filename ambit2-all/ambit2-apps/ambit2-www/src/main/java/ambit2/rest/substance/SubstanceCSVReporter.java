package ambit2.rest.substance;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.json.JSONUtils;

public class SubstanceCSVReporter<Q extends IQueryRetrieval<SubstanceRecord>>
		extends SubstanceJSONReporter<Q> {

	/**
     * 
     */
	private static final long serialVersionUID = -3848727989098277273L;

	public SubstanceCSVReporter(Request request,
			SubstanceEndpointsBundle[] bundles) {
		super(request, null, bundles, false);
		comma = ",";
	}

	@Override
	public void header(java.io.Writer output, Q query) {
		try {
			output.write("Substance Name,Substance UUID,Substance Type,Public name,Reference Substance UUID,Owner, Owner UUID,Info\n");

		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage(), x);
		}
	};

	@Override
	public void footer(java.io.Writer output, Q query) {

	};

	@Override
	public Object processItem(SubstanceRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			if (item.getSubstanceName() != null)
				writer.write(JSONUtils.jsonQuote(item.getSubstanceName()));
			writer.write(comma);
			writer.write(item.getSubstanceUUID());
			writer.write(comma);
			writer.write(JSONUtils.jsonQuote(item.getSubstancetype()));
			writer.write(comma);
			if (item.getPublicName() != null)
				writer.write(JSONUtils.jsonQuote(item.getPublicName()));
			writer.write(comma);
			if (item.getReferenceSubstanceUUID()!=null)
				writer.write(JSONUtils.jsonQuote(item.getReferenceSubstanceUUID()));
			writer.write(comma);
			writer.write(JSONUtils.jsonQuote(item.getOwnerName()));
			writer.write(comma);
			writer.write(item.getOwnerUUID());
			writer.write(comma);

			if (item.getExternalids() != null) {
				for (ExternalIdentifier id : item.getExternalids()) {
					writer.write(id.getSystemDesignator());
					writer.write(":");
					writer.write(id.getSystemIdentifier());
					writer.write(" ");
				}
			} else
				writer.write(" ");
			writer.write("\n");
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE, x.getMessage(), x);
		}
		return item;
	}

	@Override
	public String getFileExtension() {
		return "csv";
	}
}
