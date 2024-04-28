package dao;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class DBConnection {

	private static final String URL = "jdbc:mysql://esbpocdb.cyozk4yxxn7c.eu-west-3.rds.amazonaws.com:3306/ESB_POC_DB_APP";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "Admin12345";

	public static DataSource setupDataSource() {
//		ClassUtils.loadClass();
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(URL, USERNAME, PASSWORD);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
		poolableConnectionFactory.setPool(connectionPool);
		PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<>(connectionPool);
		return dataSource;
	}

}
