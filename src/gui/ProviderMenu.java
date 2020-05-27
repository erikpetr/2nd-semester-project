package gui;

import controller.ProviderController;
import database.DataAccessException;
import gui.PopUp.PopUpType;
import model.Provider;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProviderMenu extends JPanel {
    private final JSplitPane providerPane;
    private final JTextPane providerInfo;
    private final JList<Provider> providerList;
    private final JPanel optionPanel;
    private ProviderController providerController;
    private DefaultListModel<Provider> providerListModel;

    /**
     * Create the panel.
     */
    public ProviderMenu() {
        try {
            providerController = new ProviderController();
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
        }
        setLayout(new BorderLayout(0, 0));

        providerPane = new JSplitPane();
        providerPane.setResizeWeight(0.5);
        providerPane.setContinuousLayout(true);
        add(providerPane, BorderLayout.CENTER);

        providerInfo = new JTextPane();
        providerPane.setRightComponent(providerInfo);

        providerList = new JList<>();
        providerList.addListSelectionListener(e -> {
            if (providerList.getSelectedValue() != null) {
                showControlsFor(providerList.getSelectedValue());
                showInfoFor(providerList.getSelectedValue());
            }
        });
        providerPane.setLeftComponent(providerList);

        optionPanel = new JPanel();
        add(optionPanel, BorderLayout.NORTH);


        init();
    }


    public void init() {
        loadProviders();
        optionPanel.removeAll();
        optionPanel.revalidate();
        optionPanel.repaint();
    }

    private void showControlsFor(Provider provider) {
        optionPanel.removeAll();

        JButton btnCreateProvider = new JButton("New provider");
        btnCreateProvider.addActionListener(e -> {
            createProvider();
            init();
        });
        optionPanel.add(btnCreateProvider);

        JButton btnUpdateProvider = new JButton("Update provider");
        btnUpdateProvider.addActionListener(e -> {
            LayoutChangeMonitor.getInstance().setLayout(new UpdateProviderMenu(provider), "update_provider");
            init();
        });
        optionPanel.add(btnUpdateProvider);

        JButton btnDeleteProvider = new JButton("Delete");
        btnDeleteProvider.addActionListener(e -> {
            try {
                providerController.deleteProvider(provider);
            } catch (DataAccessException e1) {
                PopUp.newPopUp(this, e1.getMessage(), "Error", PopUpType.ERROR);
            }
            PopUp.newPopUp(this, "Provider deleted!", "Success", PopUpType.INFORMATION);
            init();
        });
        optionPanel.add(btnDeleteProvider);

        optionPanel.revalidate();
        optionPanel.repaint();

    }

    private void showInfoFor(Provider selectedValue) {
        String info = "";
        info += "<html><h1><b>Provider name: </b>" + selectedValue.getName() + " (ID: " + selectedValue.getId() + ")</h1></br>";
        info += "<h2>Email address: " + selectedValue.getEmail() + "</h2></br>";
        info += "<h2>Address: " + selectedValue.getAddress().toString() + "</h2>";
        info += "<h2>Available: " + (selectedValue.getAvailable() ? "Yes" : "No") + "</h2>";
        providerInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        providerInfo.setEditable(false);
        providerInfo.setContentType("text/html");
        providerInfo.setText(info);
    }

    public void loadProviders() {
        new ProviderLoader(this).start();
    }

    public void createProvider() {
        LayoutChangeMonitor.getInstance().setLayout(new UpdateProviderMenu(null), "update_provider");
    }

    private class ProviderLoader extends Thread {
        JComponent parent;

        public ProviderLoader(JComponent parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            try {
                providerListModel = new DefaultListModel<>();
                List<Provider> dataList = providerController.all();
                for (Provider provider : dataList) {
                    providerListModel.addElement(provider);
                }

                providerList.setModel(providerListModel);

                providerList.setCellRenderer(new ListCell());
            } catch (DataAccessException e) {
                PopUp.newPopUp(parent, e.getMessage(), "Error", PopUpType.ERROR);
            }
        }
    }

}
