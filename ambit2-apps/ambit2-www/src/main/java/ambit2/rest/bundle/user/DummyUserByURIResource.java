package ambit2.rest.bundle.user;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;


public class DummyUserByURIResource<T> extends  UserByURIResource<T> {


    @Override
    public void check(Context context, Request request, Response response) throws ResourceException {

    }
}
