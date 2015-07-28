package ambit2.rest.substance;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.matrix.CreateMatrixFromBundle;
import ambit2.db.update.bundle.matrix.DeleteMatrixFromBundle;
import ambit2.db.update.bundle.matrix.ReadEffectRecordByBundleMatrix._matrix;
import ambit2.rest.dataset.DatasetURIReporter;

public class CallableBundleMatrixCreator extends
	CallableDBUpdateTask<SubstanceEndpointsBundle, SubstanceEndpointsBundle, String> {
    protected DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle> reporter;
    protected boolean deleteMatrix = false;
    protected _matrix matrix = _matrix.matrix_working;

    public CallableBundleMatrixCreator(_matrix matrix, Method method, Form form, SubstanceEndpointsBundle bundle,
	    DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>, SubstanceEndpointsBundle> reporter,
	    Connection connection, String token) {
	super(method, bundle, connection, token);
	this.reporter = reporter;
	if (form != null)
	    try {
		deleteMatrix = Boolean.parseBoolean(form.getFirstValue("deletematrix"));
	    } catch (Exception x) {
		deleteMatrix = false;
	    }
	this.matrix = matrix;
    }

    @Override
    protected SubstanceEndpointsBundle getTarget(SubstanceEndpointsBundle input) throws Exception {
	return input;
    }

    @Override
    protected IQueryUpdate<? extends Object, SubstanceEndpointsBundle> createUpdate(SubstanceEndpointsBundle target)
	    throws Exception {
	switch (matrix) {
	case matrix_working: {
	    if (method.equals(Method.POST))
		return new CreateMatrixFromBundle(target, deleteMatrix, matrix);
	    else if (method.equals(Method.DELETE))
		return new DeleteMatrixFromBundle(target);

	}
	/*
	case matrix_final: {
	    if (method.equals(Method.POST))
		return new CreateMatrixFromBundle(target, deleteMatrix, matrix);
	}
	*/
	default: {
	    break;
	}
	}

	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected String getURI(SubstanceEndpointsBundle item) throws Exception {
	if (item.getID() > 0)
	    return reporter.getURI(item);
	else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
    }

}
