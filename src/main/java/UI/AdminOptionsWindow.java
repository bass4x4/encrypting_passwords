package UI;

import Backend.PasswordUtils;

import javax.swing.*;
import java.awt.*;

public class AdminOptionsWindow {
    private JPanel adminOptionsPanel;
    private JButton changePasswordButton;
    private JButton listUsersButton;
    private JButton addUserButton;

    AdminOptionsWindow() {
        changePasswordButton.addActionListener(actionEvent -> {
            PasswordUtils.setUsersNewPassword(PasswordUtils.ADMIN_NAME);;
        });

        addUserButton.addActionListener(actionEvent -> {
            createPanelOfType(new AddUserWindow().getAddUserPanel(), 200, 200);
        });
        listUsersButton.addActionListener(actionEvent -> {
            ListUsersWindow listUsersWindow = new ListUsersWindow();
            listUsersWindow.setUp();
            createPanelOfType(listUsersWindow.getListUsersPanel(), 500, 250);
        });
    }

    private void createPanelOfType(JPanel adminOptionsPanel, int x, int y) {
        final JDialog optionWindow = new JDialog(new JFrame("Change password"), true);

        optionWindow.setContentPane(adminOptionsPanel);
        optionWindow.pack();
        optionWindow.setSize(x, y);
        optionWindow.setMinimumSize(new Dimension(200, 200));
        optionWindow.setVisible(true);
    }

    public JPanel getAdminOptionsPanel() {
        return adminOptionsPanel;
    }
}
