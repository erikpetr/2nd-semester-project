package gui;

import controller.StoreController;
import database.DataAccessException;
import model.Store;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class StoreStockReportMenuWarehouse extends StoreStockReportMenu {
    protected JList<Store> storeList;
    private JSplitPane storePanel;
    private List<Store> stores = new LinkedList<>();


    public StoreStockReportMenuWarehouse() {
        super();
        this.currentStore = null;

        reloadDataAndGui();
        refreshStoreSplitPanel();

        this.add(storePanel);
        this.setVisible(true);
    }

    protected void refreshStoreSplitPanel() {
        // Report Split Panel
        storePanel = new JSplitPane();
        storePanel.setOrientation(SwingConstants.VERTICAL);

        storePanel.setLeftComponent(storeList);

        // List Reports
        refreshStoreJList();

        JScrollPane storeScroll = new JScrollPane(storeList);

        storePanel.setLeftComponent(storeScroll);

        // Item Table
        itemTable = new JTable();
        refreshReportSplitPanel();
        storePanel.setRightComponent(reportPanel);
    }

    private void refreshStoreJList() {
        storeList = new JList<>(stores.toArray(new Store[reports.size()]));

        storeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storeList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        storeList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        storeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                currentStore = storeList.getSelectedValue();
                loadReports();
                refreshReportSplitPanel();
                storePanel.setRightComponent(reportPanel);
                this.revalidate();
            }
        });
    }

    private void loadStores() {
        try {
            StoreController storeController = new StoreController();
            stores = storeController.getStores();
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
    }

    protected void reloadDataAndGui() {
        loadStores();
    }
}

