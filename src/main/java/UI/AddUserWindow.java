package UI;

import Backend.PasswordUtils;
import Backend.UserContext;

import javax.swing.*;

public class AddUserWindow {
    private JPanel addUserPanel;
    private JTextField userNameField;
    private JButton addUserButton;
    private JLabel addUserLabel;
    private JSpinner passwordLimit;
    private JLabel passwordLengthLabel;

    public AddUserWindow() {
        addUserButton.addActionListener(actionEvent -> {
            String userName = userNameField.getText();
            if (!userName.isEmpty()) {
                if (!PasswordUtils.PASSWORDS.containsKey(userName)) {
                    int passwordLength;
                    try {
                        passwordLength = (int) passwordLimit.getValue();
                        if (passwordLength > 0) {
                            UserContext newUserContext = new UserContext(userName, "", false, false, passwordLength);
                            PasswordUtils.PASSWORDS.put(userName, newUserContext);
                            JOptionPane.showMessageDialog(null, String.format("Successfully added %s user.", userName));
                        } else {
                            JOptionPane.showMessageDialog(null, "Password must be a number and should contain at least 1 symbol!");
                        }
                    } catch (ClassCastException e) {
                        JOptionPane.showMessageDialog(null, "Password must be a number and should contain at least 1 symbol!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, String.format("User with name %s already exists.", userName));
                }
            }
        });
    }

    public JPanel getAddUserPanel() {
        return addUserPanel;
    }
}
