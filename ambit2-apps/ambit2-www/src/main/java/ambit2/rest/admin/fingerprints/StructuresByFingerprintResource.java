package ambit2.rest.admin.fingerprints;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.db.update.fp.Fingerprint;
import ambit2.db.update.fp.IFingerprint;
import ambit2.db.update.fp.ReadStructuresByFingerprint;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource;

public class StructuresByFingerprintResource extends StructureQueryResource<ReadStructuresByFingerprint> {

	public final static String resource = "/structure";

	
	@Override
	protected ReadStructuresByFingerprint createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}
		Object fptype = request.getAttributes().get(FingerprintResource.resourceKey);
		ReadStructuresByFingerprint q = new ReadStructuresByFingerprint();
		IFingerprint<FPTable,String> fp = new Fingerprint<FPTable, String>();
		try {
			if (fptype==null) fp.setType(FPTable.fp1024);
			else fp.setType(FPTable.valueOf(fptype.toString()));
		} catch (Exception x) {
			fp.setType(FPTable.fp1024);
		}
		Object fingerprint = getResourceRef(request).getQueryAsForm().getFirstValue(QueryResource.search_param);
		if (fingerprint==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No query parameter ?search=<fingerprint>");
		fp.setBits(fingerprint.toString());
		q.setValue(fp);
		return q;
	}

}
