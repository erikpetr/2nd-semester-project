package database;

import java.util.LinkedList;
import java.util.List;

import java.sql.*;
import model.Store;
import model.Product;
import model.StoreStockReport;
import model.StoreStockReportItem;

public class StoreStockReportDB implements StoreStockReportDAO {
    DBConnection db = DBConnection.getInstance();
    
    public StoreStockReportDB() throws DataAccessException {
        //This constructor is empty because it only exists to pass along DataAccessException from DBConnection.getInstance()
    }

    /**
     * This method takes an object and converts it to a valid SQL INSERT query, which is the executed
     * Given a valid StockReport object which doesn't exist in the database, it inserts it into the DB
     * @param value it's the given T type object (in this case StockReport)
     * @return the generated key after the insertion to the DB
     * @see DBConnection executeInsertWithID() method
     */
    @Override
    public int create(StoreStockReport value) throws DataAccessException {
        // TODO : Test and remove
        System.out.println("######################################");
        List<StoreStockReportItem> l = value.getItems();
        for (int i = 0; i < l.size(); i++) {
            System.out.print(value.getItems().get(i) + " , ");
        }
        System.out.println();


        String queryReport = "INSERT INTO StoreStockReport (storeID, date, note) VALUES (?, ?, ?);";
        String queryItem = "INSERT INTO StoreStockReportItem (storeStockReportID, quantity, productID) VALUES (?, ?, ?);";
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryReport, Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1, value.getStore().getId());
            s.setDate(2, Date.valueOf(value.getDate()));
            s.setString(3, value.getNote());
            int reportId = db.executeInsertWithID(s);
            for (StoreStockReportItem item : value.getItems()) {
                try (PreparedStatement ps = db.getDBConn().prepareStatement(queryItem, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, reportId);
                    ps.setInt(2, item.getQuantity());
                    ps.setInt(3, item.getProduct().getId());
                    db.executeInsertWithID(ps);
                }
            }
            return reportId;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * This method takes an ID and converts it to a valid SQL SELECT query, which is the executed
     * Given an ID this method returns a single StoreStockReport which has the given ID
     * @param id is the ID which we want to search for in the database
     * @return the single object with the given ID
     * @see DBConnection executeSelect() method
     */
    @Override
    public StoreStockReport selectByID(int id) throws DataAccessException {
        StoreStockReport report = null;
        String queryReport = "SELECT TOP 1 * FROM StoreStockReport WHERE id=?";
        String queryItem = "SELECT * FROM StoreStockReportItem WHERE storeStockReportID=?;";
        ProductDB productDB = new ProductDB();
        List<StoreStockReportItem> items = new LinkedList<>();
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryReport)) {
            s.setInt(1, id);
            ResultSet rsStore = db.executeSelect(s);
            try (PreparedStatement ps = db.getDBConn().prepareStatement(queryItem)) {
                ps.setInt(1, id);
                ResultSet rsItem = db.executeSelect(ps);
                while (rsItem.next()) {
                    items.add(new StoreStockReportItem(
                            productDB.selectByID(rsItem.getInt("productID")),
                            rsItem.getInt("quantity")
                    ));
                }
            }
            if (rsStore.next()) {
                //TODO
                /*report = new StoreStockReport(
                        rsStore.getInt("id"),
                        rsStore.getDate("date").toLocalDate(),
                        rsStore.getString("note"),
                        (Store) userDB.selectByID(rsStore.getInt("date")),
                        items
                );
                return report;*/
            } else throw new DataAccessException("There are no reports with the given ID");
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
		return report;
    }

    /**
     * This method takes an object and converts it to a valid SQL UPDATE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows affected by the update
     * @see DBConnection executeQuery() method
     */
    @Override
    public int update(StoreStockReport value) throws DataAccessException {
        String queryReport = "UPDATE StoreStockReport SET date=?, note=? WHERE id=? AND storeID=?;";
        String queryItem = "UPDATE StoreStockReportItem SET quantity=? WHERE stpreStockReportID=?;";
        int rows = -1;
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryReport)) {
            s.setDate(1, Date.valueOf(value.getDate()));
            s.setString(2, value.getNote());
            s.setInt(3, value.getId());
            s.setInt(4, value.getStore().getId());
            rows = db.executeQuery(s);
            for (StoreStockReportItem item : value.getItems()) {
                try (PreparedStatement ps = db.getDBConn().prepareStatement(queryItem)) {
                    s.setInt(1, item.getQuantity());
                    s.setInt(2, value.getId());
                    rows += db.executeQuery(s);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return rows;
    }

    /**
     * This method takes an object and converts it to a valid SQL DELETE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows deleted from the table
     * @see DBConnection executeQuery()
     */
    @Override
    public int delete(StoreStockReport value) throws DataAccessException {
        // Because of the tables cascade rule, we dont need to have separate stock query
        String query = "DELETE FROM StoreStockReport WHERE id=?";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, value.getId());
            return db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException();
        }
    }

    @Override
    public List<StoreStockReport> all() throws DataAccessException {
        //TODO FIX
        return new LinkedList<>();
    }


    /**
     * This method give a list of all report of a store
     *
     * @param store The store
     * @return All the report of a store
     */
    @Override
    public List<StoreStockReport> getByStore(Store store) throws DataAccessException {
        String query = "SELECT "
               + "[StoreStockReport].id as storestockreport_id,"
               + "[StoreStockReportItem].quantity,"
               + "[StoreStockReport].note, [StoreStockReport].date,"
               + "[Product].id as product_id, [Product].name, [Product].price, [Product].weight "
               + "FROM [StoreStockReport] "
               + "  JOIN [StoreStockReportItem] ON [StoreStockReport].id = [StoreStockReportItem].productID "
               + "  JOIN [Product] ON [StoreStockReportItem].productID = [product].id "
               + "WHERE [StoreStockReport].storeID= ? "
               + "ORDER BY [StoreStockReport].id DESC;";


        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, store.getId());
            ResultSet rs = db.executeSelect(s);
            List<StoreStockReport> resultList = new LinkedList<>();
            List<Product> productList = new LinkedList<>();

            StoreStockReport actualReport = null;
            while (rs.next()) {

                String lol = "";
                for (int i = 1; i < 9; i++) {
                    lol += rs.getString(i) + " , ";
                }
                System.out.println(lol);


                // Keep only one instance of each product
                int newProductId = rs.getInt("product_id");
                Product actualProduct = null;
                for (Product product: productList) {
                    if (product.getId() == newProductId) {
                        actualProduct = product;
                        break;
                    }
                }

                if (actualProduct == null) {
                    actualProduct = new Product(
                        newProductId,
                        rs.getString("name"),
                        rs.getDouble("weight"),
                        rs.getDouble("price")
                    );
                    productList.add(actualProduct);
                }

                // Keep only one instance of each report
                int newReportId = rs.getInt("storestockreport_id");
                if ((actualReport == null) || (newReportId != actualReport.getId())) {
                    actualReport = new StoreStockReport(
                        newReportId,
                        rs.getDate("date").toLocalDate(),
                        rs.getString("note"),
                        store,
                        new LinkedList<StoreStockReportItem>()
                    );
                    resultList.add(actualReport);
                }
                
                // Add StoreStockReportItem to StoreStockReport
                actualReport.addItem(new StoreStockReportItem(
                    actualProduct,
                    rs.getInt("quantity")
                ));
            }
            return resultList;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
