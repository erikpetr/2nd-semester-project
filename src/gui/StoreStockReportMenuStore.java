package gui;

import controller.LoginController;
import model.Store;

import javax.swing.*;
import java.awt.*;


public class StoreStockReportMenuStore extends StoreStockReportMenu {

    public StoreStockReportMenuStore() {
        super();
        this.currentStore = (Store) LoginController.getLoggedInUser();
        this.reloadDataAndGui();

        refreshReportSplitPanel();

        // Button Create for stores
        JButton btnCreateReport = ColorStyle.newButton("Create Stock Report");
        optionsPanel.add(btnCreateReport);
        btnCreateReport.addActionListener(actionEvent -> openCreateStoreStockMenu());

        // Add Report split panel to main panel
        this.add(reportPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    protected void reloadDataAndGui() {
        loadReports();
    }

    private void openCreateStoreStockMenu() {
        JComponent createStoreStockReports = new CreateStoreStockReportMenu();
        LayoutChangeMonitor.getInstance().setLayout(createStoreStockReports, "createStoreStockMenu");
    }

}
