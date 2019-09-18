package Backend;

import UI.AuthWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App {
    public static void main(String[] args) {
        JFrame authWindow = new JFrame("Auth window");
        authWindow.setContentPane(new AuthWindow().authPanel);
        authWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        authWindow.pack();
        authWindow.setSize(200, 200);
        authWindow.setMinimumSize(new Dimension(200, 200));
        PasswordUtils.initPasswords();
        authWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PasswordUtils.savePasswordsToFile();
                e.getWindow().dispose();
            }
        });
        authWindow.setVisible(true);
    }
}
