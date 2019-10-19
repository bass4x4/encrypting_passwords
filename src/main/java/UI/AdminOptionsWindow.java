package UI;

import Backend.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminOptionsWindow {
    private JPanel adminOptionsPanel;
    private JButton changePasswordButton;
    private JButton listUsersButton;
    private JButton addUserButton;
    private JButton cypherButton;
    private JButton passphraseSettingsButton;

    AdminOptionsWindow() {
        changePasswordButton.addActionListener(actionEvent -> PasswordUtils.setUsersNewPassword(PasswordUtils.ADMIN_NAME));
        addUserButton.addActionListener(actionEvent -> createPanelOfType(new AddUserWindow().getAddUserPanel(), 200, 200));
        listUsersButton.addActionListener(actionEvent -> {
            ListUsersWindow listUsersWindow = new ListUsersWindow();
            listUsersWindow.setUp();
            createPanelOfType(listUsersWindow.getListUsersPanel(), 500, 350);
        });
        cypherButton.addActionListener(actionEvent -> createPanelOfType(new CypherWindow().getCypherPanel(), 600, 350));
        passphraseSettingsButton.addActionListener(actionEvent -> createPanelOfType(new PassphraseWindow().getPassphrasePanel(), 300, 300));
    }

    private void createPanelOfType(JPanel adminOptionsPanel, int x, int y) {
        final JDialog optionWindow = new JDialog(new JFrame("Change password"), true);

        optionWindow.setContentPane(adminOptionsPanel);
        optionWindow.pack();
        optionWindow.setSize(x, y);
        optionWindow.setMinimumSize(new Dimension(200, 200));
        optionWindow.setVisible(true);
    }

    JPanel getAdminOptionsPanel() {
        return adminOptionsPanel;
    }
}
