package ambit2.rest.query;

import java.io.Serializable;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.resource.ResourceException;

import ambit2.rest.task.FactoryTaskConvertor;
import ambit2.user.rest.resource.AmbitDBQueryResource;

public abstract class AmbitDBResource<Q extends IQueryRetrieval<T>, T extends Serializable> extends
	AmbitDBQueryResource<Q, T> {

    @Override
    public String getConfigFile() {
	return "ambit2/rest/config/ambit2.pref";
    }

    @Override
    protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage) throws ResourceException {
	return new FactoryTaskConvertor(storage);
    }
}
