package ambit2.rest.structure;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;

/**
 * Generates conformer URI as /compound/{id}/conformer/{id}
 * @author nina
 *
 * @param <Q>
 */
public class ConformerURIReporter<Q extends IQueryRetrieval<IStructureRecord>> extends CompoundURIReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3240656616658257198L;

	public ConformerURIReporter(Reference baseRef) {
		super(baseRef);
	}
	@Override
	public void processItem(IStructureRecord item, Writer output) {
		try {
			String ref = baseReference.toString();
			if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);
			output.write(String.format("%s%s/%d%s/%d",
					ref,
					CompoundResource.compound,item.getIdchemical(),ConformerResource.conformerKey,item.getIdstructure())
			);
			output.flush();
		} catch (IOException x) {
			x.printStackTrace();
		}
		
	}
}
