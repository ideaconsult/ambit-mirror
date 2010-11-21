package ambit2.db.pool;

import javax.sql.DataSource;

public interface IDataSourcePool {
	DataSource getDatasource();
}
