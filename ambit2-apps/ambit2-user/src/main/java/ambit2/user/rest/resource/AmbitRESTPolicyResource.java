package ambit2.user.rest.resource;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.aalocal.policy.RESTPolicyResource;
import net.idea.restnet.i.aa.IRESTPolicy;

public class AmbitRESTPolicyResource<Q extends IQueryRetrieval<IRESTPolicy<Integer>>> extends RESTPolicyResource<Q> {

	public String getConfigFile() {
		return "ambit2/rest/config/config.prop";
	}
}
