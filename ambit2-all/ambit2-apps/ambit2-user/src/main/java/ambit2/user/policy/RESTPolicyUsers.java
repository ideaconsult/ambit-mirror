package ambit2.user.policy;

import java.util.List;

import net.idea.restnet.i.aa.RESTPolicy;
import net.idea.restnet.user.DBUser;

public class RESTPolicyUsers extends RESTPolicy {
    /**
     * 
     */
    private static final long serialVersionUID = -5447223123997245656L;
    protected List<DBUser> users;
    
    public List<DBUser> getUsers() {
        return users;
    }

    public void setUsers(List<DBUser> users) {
        this.users = users;
    }

    public RESTPolicyUsers(List<DBUser> users) {
	super();
	setUsers(users);
    }
}
