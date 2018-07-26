package info.signaltrade.base.dao;

import info.signaltrade.base.model.DbModel;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * This file is used for creating a pool of connections for the server
 */
public class DatabaseFactory
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(DatabaseFactory.class.getName());

    /**
     * Data Source Generates all Connections This variable is also used as indicator for "initialized" state of
     * DatabaseFactory
     */
    private static DataSource dataSource;

    /**
     * Connection Pool holds all connections - Idle or Active
     */
    private static GenericObjectPool connectionPool;

    /**
     * Database model
     */
    private static DbModel dbModel;

    /**
     * Initializes DatabaseFactory.
     */
    public synchronized static void init(DbModel dm)
    {
        dbModel = dm;

        if(dataSource != null || dbModel == null)
            return;

        try {
            Class.forName(dbModel.getDbDriver()).newInstance();
        } catch(Exception e) {
            log.fatal("Error obtaining DB driver", e);
            throw new Error("DB Driver doesn't exist!");
        }

        // Create GenericObjectPool
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxIdle(dbModel.getDbMinConnection());
        connectionPool.setMaxIdle(dbModel.getDbMaxConnection());

        /* test if connection is still valid before returning */
        connectionPool.setTestOnBorrow(true);

        try
        {
            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dbModel.getDbUrl(), dbModel.getDbUser(), dbModel.getDbPwd());

            // Create PoolableConnectionFactory
            new PoolableConnectionFactory( connectionFactory, connectionPool, null, "SELECT 1", false, true );

            // Create data source to utilize Factory and Pool
            dataSource = new PoolingDataSource(connectionPool);

            Connection c = getConnection();
            DatabaseMetaData dmd = c.getMetaData();
            log.info ("Database: " + dmd.getDatabaseProductName() + ", Version: " + dmd.getDatabaseMajorVersion() + "." + dmd.getDatabaseMinorVersion());
            c.close();
        }
        catch(Exception e)
        {
            log.fatal("Error with connection string: " + dbModel.getDbUrl(), e);
            throw new Error("DatabaseFactory not initialized!");
        }

        log.info("Successfully connected to database");
    }

    /**
     * Returns an active connection from pool. This function utilizes the dataSource which grabs an object from the
     * ObjectPool within its limits. Throws SQLException in case of a Failed Connection
     *
     * @return Connection pooled connection
     * @throws SQLException
     *             if can't get connection
     */
    public static Connection getConnection() throws SQLException
    {
        if (dataSource==null)
            init(dbModel);

        return dataSource.getConnection();
    }

    /**
     * Returns number of active connections in the pool
     *
     * @return int Active DB Connections
     */
    public int getActiveConnections()
    {
        return connectionPool.getNumActive();
    }

    /**
     * Returns number of Idle connections. Idle connections represent the number of instances in Database Connections
     * that have once been connected and now are closed and ready for re-use. The 'getConnection' function will grab
     * idle connections before creating new ones.
     *
     * @return int Idle DB Connections
     */
    public int getIdleConnections()
    {
        return connectionPool.getNumIdle();
    }

    /**
     * Shuts down pool and closes connections
     */
    public static synchronized void shutdown()
    {
        try
        {
            connectionPool.close();
        }
        catch(Exception e)
        {
            log.warn("Failed to shutdown DatabaseFactory", e);
        }

        // set datasource to null so we can call init() once more...
        dataSource = null;
    }


}