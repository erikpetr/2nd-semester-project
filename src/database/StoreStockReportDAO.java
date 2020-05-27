package database;

import java.util.List;

import model.Store;
import model.StoreStockReport;

public interface StoreStockReportDAO extends DAOInterface<StoreStockReport> {
    List<StoreStockReport> getByStore(Store store) throws DataAccessException;
}
