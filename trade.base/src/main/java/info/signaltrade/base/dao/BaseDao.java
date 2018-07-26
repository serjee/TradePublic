package info.signaltrade.base.dao;

import info.signaltrade.base.model.AbstractModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Abstract BaseDAO class for realisation GRUD operations
 */
public abstract class BaseDao<T extends AbstractModel>
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(BaseDao.class.getName());

    /**
     * Query types
     */
    private enum QueryType
    {
        INSERT,
        UPDATE,
        DELETE
    }

    /**
     * Returns SQL request to get one element by id from the table
     * Ex.: SELECT * FROM [Table] WHERE id = ?;
     * */
    public abstract String getSelectByIdRequest();

    /**
     * Returns SQL request to insert some element to the DB
     * Ex.: INSERT INTO [Table] ([column, column, ...]) VALUES (?, ?, ...)
     * */
    public abstract String getInsertRequest();

    /**
     * Returns SQL request to update some element in the DB
     * Ex.: UPDATE [Table] SET [column = ?, column = ?, ...] WHERE id = ?;
     * */
    public abstract String getUpdateRequest();

    /**
     * Returns SQL request to delete some element in the DB
     * Ex.:DELETE FROM [Table] WHERE id= ?;
     * */
    public abstract String getDeleteRequest();

    /**
     * Prepare statement for insert
     *
     * @param statement - statement
     * @param object - object model
     * @throws SQLException
     */
    protected abstract void prepareStatementForInsert(PreparedStatement statement, T object);

    /**
     * Prepare statement for update
     *
     * @param statement - statement
     * @param object - object model
     */
    protected abstract void prepareStatementForUpdate(PreparedStatement statement, T object);

    /**
     * Prepare statement for delete
     * @param statement - statement
     * @param object - object model
     */
    protected abstract void prepareStatementForDelete(PreparedStatement statement, T object);

    /**
     * Prepare result set
     * @param resultSet - set from Db
     * @return - object list
     * @throws SQLException
     */
    protected abstract List<T> parseResultSet(ResultSet resultSet) throws SQLException;

    /**
     * Get value as object model from DB
     *
     * @param key - key for get
     * @return - object model
     */
    public T selectById(Integer key)
    {
        T object = null;
        ResultSet resultSet;
        Connection con = null;
        PreparedStatement stmt = null;

        try
        {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(getSelectByIdRequest());
            stmt.setInt(1, key);
            resultSet = stmt.executeQuery();
            object = parseResultSet(resultSet).get(0);
        }
        catch(Exception e)
        {
            log.warn("Error executing select query", e);
            return null;
        }
        finally
        {
            clean(con, stmt);
        }

        return object;
    }

    /**
     * Executes Insert Query
     *
     * @param object - object model
     * @return - result
     */
    public boolean insert(T object)
    {
        return insertUpdateDelete(object, QueryType.INSERT);
    }

    /**
     * Executes Update Query
     *
     * @param entity - entity ID for update
     * @return - result
     */
    public boolean update(T entity)
    {
        return insertUpdateDelete(entity, QueryType.UPDATE);
    }

    /**
     * Executes Delete Query
     *
     * @param entity - entity ID for delete
     * @throws SQLException
     */
    public boolean delete(T entity)
    {
        return insertUpdateDelete(entity, QueryType.DELETE);
    }

    /**
     * Clean Connection and PrepareStatement
     *
     * @param con - Connection
     * @param stmt - PrepareStatement
     */
    public void clean(Connection con, PreparedStatement stmt)
    {
        try
        {
            if(con != null)
                con.close();
            if(stmt != null)
                stmt.close();
        }
        catch(Exception e)
        {
            log.warn("Failed to close DB connection", e);
        }
    }

    /**
     * Executes Insert or Update Query
     *
     * @param object - object model
     * @return - result
     */
    private boolean insertUpdateDelete(T object, QueryType qType)
    {
        String query = null;
        Connection con = null;
        PreparedStatement stmt = null;

        // different query for insert and update
        if (qType== QueryType.INSERT)
            query = getInsertRequest();
        else if (qType== QueryType.UPDATE)
            query = getUpdateRequest();
        else if (qType== QueryType.DELETE)
            query = getDeleteRequest();

        try
        {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(query);

            if (qType== QueryType.INSERT)
                prepareStatementForInsert(stmt, object);
            else if (qType== QueryType.UPDATE)
                prepareStatementForUpdate(stmt, object);
            else if (qType== QueryType.DELETE)
                prepareStatementForDelete(stmt, object);

            // execute
            if (stmt.executeUpdate()!= 1)
            {
                log.warn("Can't executed query " + qType.toString() + " to Db");
                return false;
            }
            // clean
            con.close();
            stmt.close();
        }
        catch(Exception e)
        {
            log.warn("Failed to execute query to Db", e);

            if (stmt!=null)
                log.warn("Failed query is : ", stmt.toString());

            return false;
        }
        finally
        {
            clean(con, stmt);
        }

        return true;
    }
}

