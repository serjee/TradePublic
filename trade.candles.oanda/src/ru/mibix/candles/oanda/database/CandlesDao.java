package ru.mibix.candles.oanda.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mibix.candles.oanda.TUtils;
import ru.mibix.candles.oanda.database.impl.BaseDAO;
import ru.mibix.candles.oanda.database.impl.DatabaseFactory;
import ru.mibix.candles.oanda.model.CandlesModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * CandlesDao for working with DB
 */
public class CandlesDao extends BaseDAO<CandlesModel>
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(CandlesDao.class.getName());

    /**
     * Timeframe
     */
    private int timeframe = 0;

    /**
     * Constructor by default
     */
    public CandlesDao() { }

    /**
     * Constructor
     *
     * @param timeframe - timeframe
     */
    public CandlesDao(int timeframe)
    {
        setTimeframe(timeframe);
    }

    /**
     * Set timeframe
     *
     * @param timeframe - timeframe int
     */
    public void setTimeframe(int timeframe)
    {
        this.timeframe = timeframe;
    }

    /**
     * Select last candle
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

    /**
     * Select between candles
     *
     * @return - query
     */
    private String getSelectCandleByTimeBetween()
    {
        String name_db = TUtils.getNameDbByTf(timeframe,true);
        assert name_db != null;

        if (name_db.isEmpty())
            return null;

        return "SELECT * FROM "+name_db+" WHERE `symbol` = ? AND `time` >= ? AND `time` < ? ORDER BY `id` ASC";
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
    protected void prepareStatementForInsert(PreparedStatement statement, CandlesModel object)
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
    protected void prepareStatementForUpdate(PreparedStatement statement, CandlesModel object)
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
    protected void prepareStatementForDelete(PreparedStatement statement, CandlesModel object)
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
    protected List<CandlesModel> parseResultSet(ResultSet resultSet) throws SQLException
    {
        if (resultSet == null)
            throw new SQLException("Statement cannot be null");

        CandlesModel bar = null;
        List<CandlesModel> setQueries = new LinkedList<>();
        while (resultSet.next())
        {
            bar = new CandlesModel();
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
     * @return - CandlesModel of last symbol
     */
    public CandlesModel getLastBarBySymbol(int symbol)
    {
        CandlesModel bar = null;
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

    /**
     * Get all ticks by symbol and by between open and close dates
     *
     * @param symbol - symbol
     * @return - list CandleModel
     */
    public List<CandlesModel> getAllCandlesBySymbolByDateBetween(int symbol, Date openTime, Date closeTime)
    {
        List<CandlesModel> listCandle = null;
        ResultSet resultSet;
        Connection con = null;
        PreparedStatement stmt = null;

        try
        {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(getSelectCandleByTimeBetween());
            stmt.setInt(1, symbol);
            stmt.setTimestamp(2, new java.sql.Timestamp(openTime.getTime()));
            stmt.setTimestamp(3, new java.sql.Timestamp(closeTime.getTime()));
            resultSet = stmt.executeQuery();
            listCandle = parseResultSet(resultSet);
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

        return listCandle;
    }
}
