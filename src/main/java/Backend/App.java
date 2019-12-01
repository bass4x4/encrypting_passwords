package Backend;

import Interface.AdminOptionsWindow;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        JFrame cipherWindow = new JFrame("Auth window");
        cipherWindow.setContentPane(new AdminOptionsWindow().getAdminOptionsPanel());
        cipherWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cipherWindow.pack();
        cipherWindow.setSize(250, 150);
        cipherWindow.setResizable(false);
        cipherWindow.setVisible(true);
    }
}
