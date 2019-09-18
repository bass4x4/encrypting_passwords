package UI;

import Backend.PasswordUtils;

import javax.swing.*;

public class UserOptionsWindow {
    private JButton changePasswordButton;
    private JPanel userOptionsPanel;

    public UserOptionsWindow() {
        changePasswordButton.addActionListener(actionEvent -> {
            PasswordUtils.setUsersNewPassword(PasswordUtils.currentUser.getUserName());
        });
    }

    public JPanel getUserOptionsPanel() {
        return userOptionsPanel;
    }
}
