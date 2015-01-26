package ambit2.rest.uri;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Iterator;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.restnet.c.task.ClientResourceWrapper;

import org.restlet.data.Reference;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;

public class URIStructureIterator extends AbstractBatchProcessor<String[], IStructureRecord> {
    protected Reference baseReference;
    protected String[] uris;
    protected int index = -1;
    protected IStructureRecord record = new StructureRecord();

    public URIStructureIterator(String[] uris, Reference baseReference) {
	super();
	this.baseReference = baseReference;
	this.uris = uris;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 3646297794526475692L;

    public Iterator<IStructureRecord> getIterator(String[] target) throws AmbitException {
	this.uris = target;
	this.index = -1;
	return new Iterator<IStructureRecord>() {
	    public boolean hasNext() {
		index++;
		return index < uris.length;
	    }

	    public IStructureRecord next() {
		record.clear();
		Object[] ids = OpenTox.URI.conformer.getIds(uris[index], baseReference);
		if ((ids != null) && (ids[0] != null) && (ids[1] != null)) {
		    record.setIdchemical((Integer) ids[0]);
		    record.setIdstructure((Integer) ids[1]);
		} else {
		    Object id = OpenTox.URI.compound.getId(uris[index], baseReference);
		    if (id != null) {
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

    public boolean retrieveStructure(String uri) {
		InputStream in = null;
		HttpURLConnection uc = null;
		try {

				uc = ClientResourceWrapper.getHttpURLConnection(uri, "GET", ChemicalMediaType.CHEMICAL_MDLSDF.toString());
				HttpURLConnection.setFollowRedirects(true);
				if (HttpURLConnection.HTTP_OK== uc.getResponseCode()) {
					in = uc.getInputStream();
					StringBuilder b = new StringBuilder();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					final String newline = System.getProperty("line.separator");
					while ((line = reader.readLine())!=null) {
						b.append(line);
						b.append(newline);
					}	
					record.setContent(b.toString());
					record.setFormat(MOL_TYPE.SDF.toString());
				}
	
				return true;
					
		} catch (Exception x) {
			record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
			record.setContent(uri);	
			return false;
		} finally {
			try {if (in != null) in.close();} catch (Exception x) {}
			try {if (uc != null) uc.disconnect();} catch (Exception x) {}
		}
	}
    /*
     * protected boolean retrieveStructure(String uri) throws ResourceException
     * {
     * 
     * Representation r = null; try { ClientResourceWrapper client = new
     * ClientResourceWrapper(uri);
     * 
     * 
     * r = client.get(ChemicalMediaType.CHEMICAL_MDLSDF); if
     * (client.getStatus().equals(Status.SUCCESS_OK)) {
     * record.setContent(r.getText());
     * record.setFormat(MOL_TYPE.SDF.toString()); return true; } try {
     * r.release(); } catch (Exception x) {} r =
     * client.get(ChemicalMediaType.CHEMICAL_MDLMOL); if
     * (client.getStatus().equals(Status.SUCCESS_OK)) {
     * 
     * record.setContent(r.getText());
     * record.setFormat(MOL_TYPE.SDF.toString()); return true; }
     * 
     * throw new
     * ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format(
     * "%s doesn't support %s or %s", uri, ChemicalMediaType.CHEMICAL_MDLSDF,
     * ChemicalMediaType.CHEMICAL_MDLMOL));
     * 
     * } catch (ResourceException x) { throw x; } catch (Exception x) { throw
     * new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,uri,x); } finally {
     * try { r.release(); } catch (Exception x) {} } }
     */

}
