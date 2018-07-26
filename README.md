# TradePublic
Different tools for financial markets analysis and auto trading.

# trade.base
Simple bases of interfaces and abstract classes for build application.

# trade.stat.getticks
Loads the tick data from http://ticks.alpari.org/ and glues them into a single CSV file (placed to MQL4/Files).
After it this CSV file you can import to MetaTrader using CSV2FXT plugin.

# trade.candles.oanda
Gets ticks from https://api-fxpractice.oanda.com/ using API and form candles of different timeframes to DB tables.

# trade.bars.forming
Get last ticks from DB and form candles for different timeframes to DB tables.