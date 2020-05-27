package gui;

import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import controller.ControlException;
import controller.StockController;
import model.Stock;

public class StockMenu extends JScrollPane {

	/**
	 * Create the panel.
	 */
	public StockMenu() {
		super(stocksTable());
	}
	
	private static JTable stocksTable() {
		// Column names
		String[] columnNames = {"ProductID",
				"Product",
				"Quantity",
				"Quantity Min",
				"Price",
				"Weight",
		};

		// Create table with row edit disable
		try {
			// Convert data to 3d array
			List<Stock> stocks = StockController.getStocks();
			Object[][] alldata = new Object[stocks.size()][];
			int i = 0;
			for (Stock row : stocks)
			{
				Object[] data = {
						row.getProduct().getId(),
						row.getProduct().getName(),
						row.getQuantity(),
						row.getMinQuantity(),
						row.getProduct().getPrice(),
						row.getProduct().getWeight(),
				};
				alldata[i] = data;
				i++;
			}

			JTable table = new JTable(alldata, columnNames) {
				@Override
				public boolean editCellAt(int row, int column, java.util.EventObject e) {
					return false;
				}
			};
			
			// Disable selection
			table.setRowSelectionAllowed(false);
			// Set border
			table.setBorder(BorderFactory.createLineBorder(Color.blue));
			return table;

		} catch (ControlException e) {
			PopUp.newPopUp(null, e.getMessage(), "Error", PopUp.PopUpType.WARNING);
		}
		return null;
	}

}
