package gui;

import org.apache.commons.text.WordUtils;

import javax.swing.*;
import java.awt.*;

public class PopUp {

    public static void newPopUp(Component parent, String message, String title, PopUpType type) {
        JOptionPane.showMessageDialog(parent, WordUtils.wrap(message, 80), title, type.value);
    }

    public enum PopUpType {
        INFORMATION(JOptionPane.INFORMATION_MESSAGE),
        WARNING(JOptionPane.WARNING_MESSAGE),
        ERROR(JOptionPane.ERROR_MESSAGE),
        PLAIN(JOptionPane.PLAIN_MESSAGE);

        public final int value;

        PopUpType(int value) {
            this.value = value;
        }
    }
}
