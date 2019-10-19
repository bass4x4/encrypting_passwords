package UI;

import Backend.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UserOptionsWindow {
    private JButton changePasswordButton;
    private JPanel userOptionsPanel;
    private JButton cypherButton;

    public UserOptionsWindow() {
        changePasswordButton.addActionListener(actionEvent -> {
            PasswordUtils.setUsersNewPassword(PasswordUtils.currentUser.getUserName());
        });
        cypherButton.addActionListener(actionEvent -> createPanelOfType(new CypherWindow().getCypherPanel(), 600, 350));
    }

    private void createPanelOfType(JPanel adminOptionsPanel, int x, int y) {
        final JDialog optionWindow = new JDialog(new JFrame("Change password"), true);

        optionWindow.setContentPane(adminOptionsPanel);
        optionWindow.pack();
        optionWindow.setSize(x, y);
        optionWindow.setMinimumSize(new Dimension(200, 200));
        optionWindow.setVisible(true);
    }

    JPanel getUserOptionsPanel() {
        return userOptionsPanel;
    }
}
