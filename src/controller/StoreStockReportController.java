package controller;

import java.time.LocalDateTime;
import java.util.List;

import database.DataAccessException;
import database.StoreStockReportDAO;
import database.StoreStockReportDB;
import model.Product;
import model.Store;
import model.StoreStockReport;
import model.StoreStockReportItem;

/**
 * Controller class which connects the GUI with the DAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class StoreStockReportController {
    private StoreStockReport report;

    /**
     * Get report stocks of the current store logged in
     *
     * @return List of Stock
     */
    public static List<StoreStockReport> getReportStoreStockReportByStore () throws ControlException {
    	// Get reports 
        List<StoreStockReport> reports = null;
        try {
            StoreStockReportDB storeStockReportDB = new StoreStockReportDB();

            reports = storeStockReportDB.getByStore((Store) LoginController.getLoggedInUser());
        } catch (DataAccessException e1) {
            throw new ControlException(e1.getMessage());
        }

        return reports ;
    }

    /**
     * This method finds all reports created by a specific store
     * @param store the store we're searching for
     * @return a list containing all reports
     * @throws ControlException when there were an exception thrown by StoreStockReportDB.getByStore() method
     */
    public static List<StoreStockReport> getReportStoreStockReportByStore(Store store) throws ControlException {
        // Get reports
        List<StoreStockReport> reports = null;
        try {
            StoreStockReportDB storeStockReportDB = new StoreStockReportDB();

            reports = storeStockReportDB.getByStore(store);
        } catch (DataAccessException e1) {
            throw new ControlException(e1.getMessage());
        }

        return reports ;
    }

    /**
     * Creates a new report for a given store
     *
     * @param store     the store for the order
     */
    public void createReport(Store store) throws ControlException {
        if (store == null) {
            throw new ControlException("Provide valid information");
        } else {
            this.report = new StoreStockReport(store);
        }
    }
    /**
     * Creates a new report for login store
     *
     */
    public void createReport() throws ControlException {
        createReport((Store) LoginController.getLoggedInUser());
    }

    /**
     * Decrements amount of products in OrderItem
     * or removes OrderItem if amount is bigger than quantity
     *
     * @param product the product to be removed
     * @param amount  the amount of products to be removed
     * @return boolean
     */
    public boolean removeProduct(Product product, int amount) {
        if (report == null || product == null || amount <= 0) {
            throw new IllegalStateException("There's no Report object initialized. Please call createReport() method first.");
        }

        for (StoreStockReportItem reportItem : report.getItems()) {
            if (reportItem .getProduct().equals(product)) {
                if (reportItem.getQuantity() > amount) {
                    reportItem.setQuantity(reportItem .getQuantity() - amount);
                } else {
                    report.removeReportItem(reportItem );
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the whole product from the order
     ntroller.addProduct(*
     * @param product the product to be removed
     * @return the value of {@link StoreStockReport#removeReportItem(StoreStockReportItem)} (reportItem)}
     */
    public boolean removeProduct(Product product) {
        for (StoreStockReportItem reportItem : report.getItems()) {
            if (product.equals(reportItem .getProduct())) {
                return report.removeReportItem(reportItem );
            }
        }
        return false;
    }

    /**
     * Adds a given amount of products to the order
     *
     * @param product the product to be added
     * @param amount  the amount of products to be added
     * @return the value of {@link StoreStockReport#addItem(StoreStockReportItem)} (reportItem)}
     */
    public boolean addProduct(Product product, int amount) throws ControlException {
        if (report == null || product == null || amount <= 0) {
            throw new IllegalStateException("There's no Report object initialized. Please call createOrder() method first.");
        }
        //StoreStockReportDAO reportDAO; TODO This line is unnecessary and should be removed, pls check!
        try {
            //reportDAO = new StoreStockReportDB(); TODO This line is unnecessary and should be removed, pls check!
            for (StoreStockReportItem reportItem: report.getItems()) {
                if (reportItem.getProduct().getId() == product.getId()) {
                    reportItem.setQuantity(reportItem.getQuantity() + amount);
                    return true;
                }
            }
            StoreStockReportItem reportItem = new StoreStockReportItem (product, amount);
            return report.addItem(reportItem);
        } catch (DataAccessException e) {
            throw new ControlException("Unable to connect to server.\n" + e.getMessage());
        }
    }

    public StoreStockReport getReport () {
        return this.report;
    }

    public void setReport(StoreStockReport report) {
        this.report = report;
    }

    /**
     * This method finalizes the report and saves it to the DB
     * @return the ID of the newly created report
     * @throws ControlException
     */
    public int finishReport() throws ControlException {
        if (report == null) {
            throw new ControlException("Create Report first");
        }

        if (report.getItems().isEmpty()) {
            throw new ControlException("Please add items to your report!");
        }
        report.setDate(LocalDateTime.now());

        try {
            StoreStockReportDAO reportDAO = new StoreStockReportDB();

            return reportDAO.create(report);
        } catch (DataAccessException e) {
            throw new ControlException(e.getMessage());
        }
    }
}
