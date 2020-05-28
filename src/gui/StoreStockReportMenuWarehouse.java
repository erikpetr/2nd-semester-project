package gui;

import java.util.LinkedList;
import java.util.List;

import controller.StoreController;
import database.DataAccessException;
import javax.swing.*;
import model.Store;

public class StoreStockReportMenuWarehouse extends StoreStockReportMenu {
    private JSplitPane storePanel;
    protected JList<Store> storeList;
    private List<Store> stores = new LinkedList<>();


    public StoreStockReportMenuWarehouse () {
        super();
        this.currentStore = null;

        reloadDataAndGui();
        refreshStoreSplitPanel();

        this.add(storePanel);
        this.setVisible(true);
    }

    protected void refreshStoreSplitPanel () {
        // Report Split Panel
        storePanel = new JSplitPane();
        storePanel.setOrientation(SwingConstants.VERTICAL);
        //storePanel.setResizeWeight(0.3);

        storePanel.setLeftComponent(storeList);

        // List Reports
        refreshStoreJList();

        storePanel.setLeftComponent(storeList);

        // Item Table
        itemTable = new JTable();
        refreshReportSplitPanel();
        storePanel.setRightComponent(reportPanel);
    }

    private void refreshStoreJList () {
        storeList = new JList<>(stores.toArray(new Store[reports.size()]));

        storeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storeList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        storeList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        storeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {// && (storeList.getSelectedValue() != null)) {
                currentStore = storeList.getSelectedValue();
                loadReports();
                refreshReportSplitPanel();
                storePanel.setRightComponent(reportPanel);
                this.revalidate();
            }
        });
    }

    private void loadStores () {
        try {
            StoreController storeController = new StoreController();
            stores = storeController.getStores();
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
    }

    protected void reloadDataAndGui () {
        loadStores();
        //refreshStoreSplitPanel();
        //this.revalidate();
    }
}

