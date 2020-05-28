package gui;

import javax.swing.*;

public class LayoutChangeMonitor {
    private static LayoutChangeMonitor instance;
    private JComponent component;
    private String constrains;

    private LayoutChangeMonitor() {

    }

    public static LayoutChangeMonitor getInstance() {
        if (instance == null) {
            instance = new LayoutChangeMonitor();
        }
        return instance;
    }

    public synchronized void setLayout(JComponent component, String constrains) {
        this.component = component;
        this.constrains = constrains;
        notifyAll();
    }

    public synchronized JComponent getLayout() {
        try {
            wait();
        } catch (InterruptedException e) {
            return this.component;
        }
        return this.component;
    }

    public String getConstrains() {
        return this.constrains;
    }
}
