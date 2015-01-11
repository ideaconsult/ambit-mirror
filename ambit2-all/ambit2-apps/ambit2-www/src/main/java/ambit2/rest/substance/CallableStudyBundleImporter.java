package ambit2.rest.substance;

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.restlet.Context;
import org.restlet.data.Reference;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.processor.DBBundleStudyWriter;
import ambit2.db.substance.processor.DBSubstanceWriter;
import ambit2.rest.dataset.DatasetURIReporter;

public class CallableStudyBundleImporter<USERID> extends CallableSubstanceImporter<USERID> {
	private SubstanceEndpointsBundle bundle;
	
	public SubstanceEndpointsBundle getBundle() {
		return bundle;
	}

	public void setBundle(SubstanceEndpointsBundle bundle) {
		this.bundle = bundle;
	}

	public CallableStudyBundleImporter(List<FileItem> items,
			String fileUploadField, Reference applicationRootReference,
			Context context, SubstanceURIReporter substanceReporter,
			DatasetURIReporter datasetURIReporter, USERID token)
			throws Exception {
		super(items, fileUploadField, applicationRootReference, context,
				substanceReporter, datasetURIReporter, token);
	}

	@Override
	protected DBSubstanceWriter createWriter() {
		return new DBBundleStudyWriter(bundle,dataset,importedRecord);	
	}
}
