package database;

import model.Store;
import model.StoreStockReport;

import java.util.List;

public interface StoreStockReportDAO extends DAOInterface<StoreStockReport> {
    List<StoreStockReport> getByStore(Store store) throws DataAccessException;
}
