package gui;

import javax.swing.*;
import java.awt.*;

public class ColorStyle {
    // TEXT
    public static final Color TITLE = new Color(47, 36, 131);
    public static final Color TEXT = Color.black;
    // MENU
    public static final Color BUTTON_BACKGROUND = new Color(47, 36, 131);
    public static final Color BUTTON_TEXT = Color.white;

    public static final Color CANCEL_BUTTON_BACKGROUND = new Color(149, 0, 1);
    public static final Color CANCEL_BUTTON_TEXT = Color.white;

    private ColorStyle() {
        throw new IllegalStateException("Utility class");
    }

    public static JButton newButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(ColorStyle.BUTTON_TEXT);
        button.setBackground(ColorStyle.BUTTON_BACKGROUND);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("Tahoma", Font.PLAIN, 16));
        return button;
    }

    public static JButton newCancelButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(ColorStyle.CANCEL_BUTTON_TEXT);
        button.setBackground(ColorStyle.CANCEL_BUTTON_BACKGROUND);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("Tahoma", Font.PLAIN, 16));
        return button;
    }
}
