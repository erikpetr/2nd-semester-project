package gui;


import javax.swing.*;
import java.awt.*;


public class ListCell implements ListCellRenderer<Object> {

    public Component getListCellRendererComponent(JList<? extends Object>
                                                          list, Object object, int index, boolean isSelected, boolean cellHasFocus) {
        DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
        String item = object.toString();
        return defaultListCellRenderer.getListCellRendererComponent(list, item, index, isSelected, cellHasFocus);
    }


}
