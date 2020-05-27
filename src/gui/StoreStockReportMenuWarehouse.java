package gui;

import java.util.LinkedList;
import java.util.List;

import controller.StoreController;
import database.DataAccessException;
import gui.StoreStockReportMenu;
import java.awt.*;
import javax.swing.*;
import model.Store;


public class StoreStockReportMenuWarehouse extends StoreStockReportMenu {
    private JSplitPane storeSplit;
    protected JList<Store> storeList;
    private List<Store> stores = new LinkedList<>();


    public StoreStockReportMenuWarehouse () {
        super();
        this.currentStore = null;

        refreshStoreJList();
        refreshStoreSplitPanel();
        refreshReportSplitPanel();

        // Add Report split panel to Right store panel
        storeSplit.setRightComponent(reportPanel);

        this.add(storeSplit);

        this.setVisible(true);
    }

    protected void refreshStoreSplitPanel () {
        // Report Split Panel
        storeSplit = new JSplitPane();
        storeSplit.setOrientation(SwingConstants.VERTICAL);

        refreshStoreSplitPanel();
        storeSplit.setResizeWeight(0.3);

        // List Reports
        refreshStoreJList();
        storeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && (storeList.getSelectedValue() != null)) {
                currentStore = storeList.getSelectedValue();
                loadReports();
                reportPanel.setLeftComponent(getReportJList());
                this.revalidate();
            }
        });
        storeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storeList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        storeList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        storeSplit.setLeftComponent(storeList);

        // Item Table
        itemTable = new JTable();
        storeSplit.setRightComponent(itemTable);
    }

    private void refreshStoreJList () {
        storeList = new JList<>(stores.toArray(new Store[reports.size()]));
    }

    protected void reloadDataAndGui () {
        try {
            StoreController storeController = new StoreController();
            stores = storeController.getStores();
            refreshStoreJList();
            storeSplit.setLeftComponent(storeList);
        }
        catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
    }
}

