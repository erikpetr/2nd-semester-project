package controller;

import database.DataAccessException;
import database.OrderDAO;
import database.OrderDB;
import database.StockDB;
import model.Order;
import model.Stock;
import model.Warehouse;

import java.util.List;

/**
 * Controller class which connects the GUI with the DAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class StockController {

    /**
     * Get stocks of the current warehouse logged in
     *
     * @return List of Stock
     */
    public static List<Stock> getStocks () throws ControlException
    {
    	// Get stocks
        List<Stock> stocks = null;
        try {
            StockDB stockDB = new StockDB();

			stocks = stockDB.getStocks((Warehouse) LoginController.getLoggedInUser());
		} catch (DataAccessException e1) {
            throw new ControlException(e1.getMessage());
		}

        return stocks;
    }
}
