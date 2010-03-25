package ambit2.rest.uri;

import java.util.Iterator;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;

public class URIStructureIterator extends AbstractBatchProcessor<String[], IStructureRecord>  {
	protected Reference baseReference;
	protected String[] uris;
	protected int index = -1;
	protected IStructureRecord record = new StructureRecord();
	public URIStructureIterator(String[] uris,Reference baseReference) {
		super();
		this.baseReference = baseReference;
		this.uris = uris;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3646297794526475692L;

	public Iterator<IStructureRecord> getIterator(String[] target)
			throws AmbitException {
		this.uris = target;
		this.index = -1;
		return new Iterator<IStructureRecord>() {
			public boolean hasNext() {
				index++;
				return index<uris.length;
			}
			public IStructureRecord next() {
				record.clear();
				Object[] ids = OpenTox.URI.conformer.getIds(uris[index], baseReference);
				if ((ids != null) && (ids[0]!=null) && (ids[1]!=null)) {
					record.setIdchemical((Integer) ids[0]);
					record.setIdstructure((Integer) ids[1]);
				} else {
					Object id = OpenTox.URI.compound.getId(uris[index], baseReference);
					if (id!=null) {
						record.setIdchemical((Integer) id);
						record.setIdstructure(-1);
					} else {
						retrieveStructure(uris[index]);
					}
				}
				return record;
			}
			public void remove() {
			}
		};
		
	}

	protected boolean retrieveStructure(String uri) throws ResourceException {

		Representation r = null;
		try {
			ClientResource client = new ClientResource(uri);
			
			
			r = client.get(ChemicalMediaType.CHEMICAL_MDLSDF);
			if (client.getStatus().equals(Status.SUCCESS_OK)) {
				record.setContent(r.getText());
				record.setFormat(MOL_TYPE.SDF.toString());
				return true;
			} 
			try { r.release(); } catch (Exception x) {}
			r = client.get(ChemicalMediaType.CHEMICAL_MDLMOL);
			if (client.getStatus().equals(Status.SUCCESS_OK)) {

				record.setContent(r.getText());
				record.setFormat(MOL_TYPE.SDF.toString());
				return true;
			} 
			
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format(
					"%s doesn't support %s or %s",
					uri,
					ChemicalMediaType.CHEMICAL_MDLSDF,
					ChemicalMediaType.CHEMICAL_MDLMOL));
			
		} catch (ResourceException x) {
			throw x;	
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,uri,x);
		} finally {
			try { r.release(); } catch (Exception x) {}
		}
	}
	
}
