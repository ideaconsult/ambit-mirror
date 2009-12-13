package ambit2.db.search;

import java.io.Serializable;



public interface IQueryCondition extends Serializable {
	String getName();
	String getSQL();
}
