package Backend;

import Interface.AdminOptionsWindow;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        JFrame cypherWindow = new JFrame("Auth window");
        cypherWindow.setContentPane(new AdminOptionsWindow().getAdminOptionsPanel());
        cypherWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cypherWindow.pack();
        cypherWindow.setSize(350, 200);
        cypherWindow.setMinimumSize(new Dimension(200, 200));
        cypherWindow.setVisible(true);
    }
}
