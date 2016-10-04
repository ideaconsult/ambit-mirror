package ambit2.rest.admin;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.db.processors.DbCreateDatabase;
import ambit2.db.version.AmbitDBVersion;
import ambit2.db.version.DBVersionQuery;
import ambit2.rest.DBConnection;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;

/**
 * Reports version of the database used. The database name is defined at compile
 * time. It it is either ambit2, or taken from .m2/settings.xml property $ambit.db
 <pre> 
 curl -X GET http://host:port/ambit2/admin/database
 </pre>
 */
public class DatabaseResource extends
		QueryResource<DBVersionQuery, AmbitDBVersion> {
	public static final String resource = "database";

	public DatabaseResource() {
		super();
		maxRetry = 1;
	}

	@Override
	public IProcessor<DBVersionQuery, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return new StringConvertor(new DBTextReporter(), MediaType.TEXT_PLAIN);
	}

	@Override
	protected Representation processNotFound(NotFoundException x, int retry)
			throws Exception {
		return null;
	}

	@Override
	protected DBVersionQuery createQuery(Context context, Request request,
			Response response) throws ResourceException {
		return new DBVersionQuery();
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		DBConnection c = new DBConnection(getContext());

		try {
			if (!dbExists(c.getLoginInfo().getDatabase(), c.getLoginInfo()
					.getUser(), c.getLoginInfo().getPassword()))
				return processSQLError(null, 1, variant);
		} catch (ResourceException x) {
		} catch (Throwable e) {
		} finally {

		}
		return super.get(variant);
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	public boolean dbExists(String dbname, String user, String pass)
			throws Exception {
		Connection c = null;
		try {
			DBConnection dbc = new DBConnection(getContext());
			c = dbc.getConnection();
			return DbCreateDatabase.dbExists(c, dbname);
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (c != null)
					c.close();
			} catch (Exception x) {
			}
		}
	}

}
