package ru.mibix.bar.forming.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mibix.bar.forming.database.impl.BaseDAO;
import ru.mibix.bar.forming.database.impl.DatabaseFactory;
import ru.mibix.bar.forming.model.TickModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * TickDao for working with DB
 */
public class TickDao extends BaseDAO<TickModel>
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(TickDao.class.getName());

    /**
     * Queries
     */
    private static final String SELECT_LAST_TICK = "SELECT * FROM td_tick WHERE symbol = ? ORDER BY id DESC LIMIT 1";
    private static final String SELECT_TICK_BY_TIME = "SELECT * FROM `td_tick` WHERE `symbol` = ? AND `time` = ? ORDER BY `id` ASC";
    private static final String SELECT_TICK_BY_TIME_BETWEEN = "SELECT * FROM `td_tick` WHERE `symbol` = ? AND `time` >= ? AND `time` < ? ORDER BY `id` ASC";

    /**
     * Constructor by default
     */
    public TickDao() { }

    /**
     * Select last bar
     * @return - query
     */
    private String getSelectLastTick()
    {
        return "SELECT * FROM td_tick WHERE symbol = ? ORDER BY id DESC LIMIT 1";
    }

    @Override
    public String getSelectByIdRequest()
    {
        return null;
    }

    @Override
    public String getInsertRequest()
    {
        return null;
    }

    @Override
    public String getUpdateRequest()
    {
        return null;
    }

    @Override
    public String getDeleteRequest()
    {
        return null;
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, TickModel object) { }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, TickModel object) { }

    @Override
    protected void prepareStatementForDelete(PreparedStatement statement, TickModel object) { }

    @Override
    protected List<TickModel> parseResultSet(ResultSet resultSet) throws SQLException
    {
        if (resultSet == null)
            throw new SQLException("Statement cannot be null");

        List<TickModel> setQueries = new LinkedList<>();
        while (resultSet.next())
        {
            TickModel tick = new TickModel();
            tick.setId(resultSet.getInt("id"));
            tick.setSymbol(resultSet.getInt("symbol"));
            tick.setTime(new Date(resultSet.getTimestamp("time").getTime()));
            tick.setOpen(resultSet.getDouble("open"));
            tick.setHigh(resultSet.getDouble("high"));
            tick.setLow(resultSet.getDouble("low"));
            tick.setClose(resultSet.getDouble("close"));
            setQueries.add(tick);
        }

        return setQueries;
    }

    /**
     * Get last tick by symbol
     *
     * @param symbol - symbol
     * @return - list TickModel
     */
    public TickModel getLastTickBySymbol(int symbol)
    {
        TickModel tick = null;
        ResultSet resultSet;
        Connection con = null;
        PreparedStatement stmt = null;

        try
        {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(SELECT_LAST_TICK);
            stmt.setInt(1, symbol);
            resultSet = stmt.executeQuery();
            tick = parseResultSet(resultSet).get(0);
        }
        catch(Exception e)
        {
            log.warn("Error executing select query " + e, e);
            return null;
        }
        finally
        {
            clean(con, stmt);
        }

        return tick;
    }

    /**
     * Get all ticks by symbol and by close date
     *
     * @param symbol - symbol
     * @return - list TickModel
     */
    public List<TickModel> getAllTickBySymbolByDate(int symbol, Date closeTime) throws SQLException
    {
        List<TickModel> listTick = null;
        ResultSet resultSet;
        Connection con = null;
        PreparedStatement stmt = null;

        try
        {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(SELECT_TICK_BY_TIME);
            stmt.setInt(1, symbol);
            stmt.setTimestamp(2, new java.sql.Timestamp(closeTime.getTime()));
            resultSet = stmt.executeQuery();
            listTick = parseResultSet(resultSet);
        }
        catch(Exception e)
        {
            log.warn("Error executing select query " + e, e);
            return null;
        }
        finally
        {
            clean(con, stmt);
        }

        return listTick;
    }

    /**
     * Get all ticks by symbol and by between open and close dates
     *
     * @param symbol - symbol
     * @return - list TickModel
     */
    public List<TickModel> getAllTickBySymbolByDateBetween(int symbol, Date openTime, Date closeTime)
    {
        List<TickModel> listTick = null;
        ResultSet resultSet;
        Connection con = null;
        PreparedStatement stmt = null;

        try
        {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(SELECT_TICK_BY_TIME_BETWEEN);
            stmt.setInt(1, symbol);
            stmt.setTimestamp(2, new java.sql.Timestamp(openTime.getTime()));
            stmt.setTimestamp(3, new java.sql.Timestamp(closeTime.getTime()));
            resultSet = stmt.executeQuery();
            listTick = parseResultSet(resultSet);
        }
        catch(Exception e)
        {
            log.warn("Error executing select query " + e, e);
            return null;
        }
        finally
        {
            clean(con, stmt);
        }

        return listTick;
    }
}
