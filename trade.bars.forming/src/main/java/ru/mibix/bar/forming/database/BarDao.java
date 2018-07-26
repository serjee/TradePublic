package ru.mibix.bar.forming.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mibix.bar.forming.TUtils;
import ru.mibix.bar.forming.database.impl.BaseDAO;
import ru.mibix.bar.forming.database.impl.DatabaseFactory;
import ru.mibix.bar.forming.model.BarModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * BarDao for working with DB
 */
public class BarDao extends BaseDAO<BarModel>
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(BarDao.class.getName());

    /**
     * Timeframe
     */
    private int timeframe = 0;

    /**
     * Constructor by default
     */
    public BarDao() { }

    /**
     * Timeframe
     *
     * @param timeframe - timeframe
     */
    public BarDao(int timeframe)
    {
        this.timeframe = timeframe;
    }

    /**
     * Select last bar
     *
     * @return - query
     */
    private String getSelectLastBar()
    {
        String name_db = TUtils.getNameDbByTf(timeframe,true);
        assert name_db != null;

        if (name_db.isEmpty())
            return null;

        return "SELECT * FROM "+name_db+" WHERE symbol = ? ORDER BY id DESC LIMIT 1";
    }

    @Override
    public String getSelectByIdRequest()
    {
        String name_db = TUtils.getNameDbByTf(timeframe,true);
        assert name_db != null;

        if (name_db.isEmpty())
            return null;

        return "SELECT * FROM "+name_db+" WHERE id= ?";
    }

    @Override
    public String getInsertRequest()
    {
        String name_db = TUtils.getNameDbByTf(timeframe,true);
        assert name_db != null;

        if (name_db.isEmpty())
            return null;

        return "INSERT INTO "+name_db+" (symbol, time, open, high, low, close) \n" +
                "VALUES(?, ?, ?, ?, ?, ?)";
    }

    @Override
    public String getUpdateRequest()
    {
        String name_db = TUtils.getNameDbByTf(timeframe,true);
        assert name_db != null;

        if (name_db.isEmpty())
            return null;

        return "UPDATE "+name_db+" SET symbol= ?, time= ?, open= ?, high= ?, low= ?, close= ? WHERE id= ?";
    }

    @Override
    public String getDeleteRequest()
    {
        String name_db = TUtils.getNameDbByTf(timeframe,true);
        assert name_db != null;

        if (name_db.isEmpty())
            return null;

        return "DELETE FROM "+name_db+" WHERE id= ?";
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, BarModel object)
    {
        try
        {
            if (object == null)
                throw new SQLException("Given object cannot be null");

            statement.setInt(1, object.getSymbol());
            statement.setTimestamp(2, new java.sql.Timestamp(object.getTime().getTime()));
            statement.setDouble(3, object.getOpen());
            statement.setDouble(4, object.getHigh());
            statement.setDouble(5, object.getLow());
            statement.setDouble(6, object.getClose());
        }
        catch (SQLException e)
        {
            log.error("Cannot find the required field or type of field is incorrect");
        }
    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, BarModel object)
    {
        try
        {
            if (object == null)
                throw new SQLException("Given object cannot be null");

            statement.setInt(1, object.getSymbol());
            statement.setTimestamp(2, new java.sql.Timestamp(object.getTime().getTime()));
            statement.setDouble(3, object.getOpen());
            statement.setDouble(4, object.getHigh());
            statement.setDouble(5, object.getLow());
            statement.setDouble(6, object.getClose());
            statement.setInt(7, object.getId());
        }
        catch (SQLException e)
        {
            log.error("Cannot find the required field or type of field is incorrect");
        }
    }

    @Override
    protected void prepareStatementForDelete(PreparedStatement statement, BarModel object)
    {
        try
        {
            if (object == null)
                throw new SQLException("Given object cannot be null");

            statement.setInt(1, object.getId());
        }
        catch (SQLException e)
        {
            log.error("Cannot find the required field or type of field is incorrect");
        }
    }

    @Override
    protected List<BarModel> parseResultSet(ResultSet resultSet) throws SQLException
    {
        if (resultSet == null)
            throw new SQLException("Statement cannot be null");

        BarModel bar = null;
        List<BarModel> setQueries = new LinkedList<>();
        while (resultSet.next())
        {
            bar = new BarModel();
            bar.setId(resultSet.getInt("id"));
            bar.setTimeframe(timeframe);
            bar.setSymbol(resultSet.getInt("symbol"));
            bar.setTime(new Date(resultSet.getTimestamp("time").getTime()));
            bar.setOpen(resultSet.getDouble("open"));
            bar.setHigh(resultSet.getDouble("high"));
            bar.setLow(resultSet.getDouble("low"));
            bar.setClose(resultSet.getDouble("close"));
            setQueries.add(bar);
        }

        return setQueries;
    }

    /**
     * Get last bar
     *
     * @param symbol - selected symbol
     * @return - BarModel of last symbol
     */
    public BarModel getLastBarBySymbol(int symbol)
    {
        BarModel bar = null;
        ResultSet resultSet;
        Connection con = null;
        PreparedStatement stmt = null;

        try
        {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(getSelectLastBar());
            stmt.setInt(1, symbol);
            resultSet = stmt.executeQuery();
            bar = parseResultSet(resultSet).get(0);
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

        return bar;
    }
}
