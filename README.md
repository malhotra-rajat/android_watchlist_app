# watchlist_app

An app for managing watchlists. 

Usage
-> When the app first starts up, a default watchlist "My first list" is populated in the database. It contains the symbols: AAPL, MSFT and GOOG 
-> The list shows the following data points: Stock Symbol, Bid Price, Ask Price and Last Price which are refreshed every 5 seconds
-> When you click on an item in the list, a stock price history chart is shown along with the above data which is refreshed every 5 seconds as well
-> Adding a symbol: The user can click on the '+' icon in the menu to add a symbol. It shows up a search box which auto completes as you type. You can only add a symbol if it is one of the search results in the dropdown list. Otherwise the "Add Symbol to Watchlist" button is disabled.
-> Deleting a symbol: The user can long press on an item to bring up the dialog to delete the symbol
-> The user can add and delete the watchlist from the top right menu.
-> The watchlist and symbols are persisted on the device. So when you open the app again, you'll see previously added watchlists and symbols
-> Note: Switching watchlists which shows up symbols along with it is not working currently.
