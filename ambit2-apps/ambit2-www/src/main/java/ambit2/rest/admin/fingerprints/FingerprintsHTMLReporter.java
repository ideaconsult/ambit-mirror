package ambit2.rest.admin.fingerprints;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.db.update.fp.IFingerprint;
import ambit2.db.update.fp.QueryFingerprints;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;

@Deprecated
public class FingerprintsHTMLReporter extends QueryHTMLReporter<IFingerprint<FPTable, String>, QueryFingerprints> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -952577019571483126L;
	public FingerprintsHTMLReporter(Request request) {
		super(request,DisplayMode.table);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request,
			ResourceDoc doc) {
		return new FingerprintURIReporter<QueryFingerprints>(request);
	}

	@Override
	public Object processItem(IFingerprint<FPTable, String> item)
			throws AmbitException {
		try {
			getOutput().write(String.format("%10d&nbsp;<a href='%s'>%s</a><br>",item.getFrequency(),getUriReporter().getURI(item),item.getBits()));
		} catch (Exception x) {
			
		}
		return item;
	}

}
