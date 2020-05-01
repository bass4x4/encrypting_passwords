package Backend;

import Interface.CipherWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class App {
    public static void main(String[] args) {
        JFrame cipherWindow = new JFrame("Auth window");
        JPanel cipherPanel = new CipherWindow().getCipherPanel();
        cipherPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        cipherWindow.setContentPane(cipherPanel);
        cipherWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cipherWindow.pack();
        cipherWindow.setSize(700, 400);
        cipherWindow.setResizable(false);
        cipherWindow.setVisible(true);
    }
}
