package gui;

import controller.StoreController;
import database.DataAccessException;
import gui.PopUp.PopUpType;
import model.Store;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StoreMenu extends JPanel {
    private final JSplitPane storePane;
    private final JTextPane storeInfo;
    private final JList<Store> storeList;
    private final JPanel optionPanel;
    private StoreController storeController;
    private DefaultListModel<Store> storeListModel;

    /**
     * Create the panel.
     */
    public StoreMenu() {
        try {
            storeController = new StoreController();
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
        }
        setLayout(new BorderLayout(0, 0));

        storePane = new JSplitPane();
        storePane.setResizeWeight(0.5);
        storePane.setContinuousLayout(true);
        add(storePane, BorderLayout.CENTER);

        storeInfo = new JTextPane();
        storePane.setRightComponent(storeInfo);

        storeList = new JList<>();
        storeList.addListSelectionListener(e -> {
            if (storeList.getSelectedValue() != null) {
                showControlsFor(storeList.getSelectedValue());
                showInfoFor(storeList.getSelectedValue());
            }
        });
        storePane.setLeftComponent(storeList);

        optionPanel = new JPanel();
        add(optionPanel, BorderLayout.NORTH);

        init();
    }

    public void init() {
        loadStores();
        optionPanel.removeAll();
        optionPanel.revalidate();
        optionPanel.repaint();
    }

    private void showControlsFor(Store store) {
        optionPanel.removeAll();

        JButton btnCreateStore = ColorStyle.newButton("New store");
        btnCreateStore.addActionListener(e -> createStore());
        optionPanel.add(btnCreateStore);

        JButton btnUpdateStore = ColorStyle.newButton("Update store");
        btnUpdateStore.addActionListener(e -> {
            LayoutChangeMonitor.getInstance().setLayout(new UpdateStoreMenu(store), "update_store");
            init();
        });
        optionPanel.add(btnUpdateStore);

        JButton btnDeleteStore = ColorStyle.newButton("Delete store");
        btnDeleteStore.addActionListener(e -> {
            try {
                storeController.deleteStore(store);
            } catch (DataAccessException e1) {
                PopUp.newPopUp(this, e1.getMessage(), "Error", PopUpType.ERROR);
            }
            PopUp.newPopUp(this, "Store deleted!", "Success", PopUpType.INFORMATION);
            init();
        });
        optionPanel.add(btnDeleteStore);

        optionPanel.revalidate();
        optionPanel.repaint();

    }

    private void showInfoFor(Store selectedValue) {
        String info = "";
        info += "<html><h1><b>Store name: " + selectedValue.getName() + " (ID: " + selectedValue.getId() + ")</h1></br>";
        info += "<h2>Email address: " + selectedValue.getEmail() + "</h2></br>";
        info += "<h2>Address: " + selectedValue.getAddress().toString() + "</h2></br>";
        storeInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        storeInfo.setEditable(false);
        storeInfo.setContentType("text/html");
        storeInfo.setText(info);
    }

    public void loadStores() {
        new StoreLoader(this).start();
    }

    public void createStore() {
        LayoutChangeMonitor.getInstance().setLayout(new UpdateStoreMenu(null), "update_store");
    }

    private class StoreLoader extends Thread {
        JComponent parent;

        public StoreLoader(JComponent parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            try {
                storeListModel = new DefaultListModel<>();
                List<Store> dataList = storeController.getStores();
                for (Store store : dataList) {
                    storeListModel.addElement(store);
                }

                storeList.setModel(storeListModel);

                storeList.setCellRenderer(new ListCell());
            } catch (DataAccessException e) {
                PopUp.newPopUp(parent, e.getMessage(), "Error", PopUpType.ERROR);
            }
        }
    }

}
