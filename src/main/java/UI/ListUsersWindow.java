package UI;

import Backend.PasswordUtils;
import Backend.UserContext;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ListUsersWindow {
    private JPanel listUsersPanel;
    private JCheckBox passwordIsAllowed;
    private JCheckBox userIsBlocked;
    private JButton blockAllButton;
    private JButton enablePasswordLimit;
    private JList usersList;
    private JButton saveButton;
    private JLabel passwordLengthLabel;
    private JLabel userNameLabel;

    private DefaultListModel<UserContext> listModel = new DefaultListModel<>();

    private List<UserContext> contextList = new ArrayList<>(PasswordUtils.PASSWORDS.values());

    ListUsersWindow() {
        usersList.addListSelectionListener(listSelectionEvent -> {
            int selectedIndex = usersList.getSelectedIndex();
            if (selectedIndex != -1) {
                UserContext context = contextList.get(selectedIndex);
                setContext(context);
            }
        });
        saveButton.addActionListener(actionEvent -> {
            int selectedIndex = usersList.getSelectedIndex();
            if (selectedIndex != -1) {
                UserContext context = contextList.get(selectedIndex);
                saveContext(context, selectedIndex);
            }
        });
        blockAllButton.addActionListener(actionEvent -> {
            contextList.forEach(userContext -> {
                if (!userContext.getUserName().equals(PasswordUtils.ADMIN_NAME)) {
                    userContext.setBlocked(true);
                }
            });
            PasswordUtils.PASSWORDS.values().forEach(userContext -> {
                if (!userContext.getUserName().equals(PasswordUtils.ADMIN_NAME)) {
                    userContext.setBlocked(true);
                }
            });

            int selectedIndex = usersList.getSelectedIndex();
            if (selectedIndex != -1) {
                UserContext context = contextList.get(selectedIndex);
                if (!context.getUserName().equals(PasswordUtils.ADMIN_NAME)) {
                    userIsBlocked.setSelected(true);
                }
            }
        });
        enablePasswordLimit.addActionListener(actionEvent -> {
            contextList.forEach(userContext -> {
                if (!userContext.getUserName().equals(PasswordUtils.ADMIN_NAME)) {
                    userContext.setPasswordAllowed(true);
                }
            });
            PasswordUtils.PASSWORDS.values().forEach(userContext -> {
                if (!userContext.getUserName().equals(PasswordUtils.ADMIN_NAME)) {
                    userContext.setPasswordAllowed(true);
                }
            });

            int selectedIndex = usersList.getSelectedIndex();
            if (selectedIndex != -1) {
                UserContext context = contextList.get(selectedIndex);
                if (!context.getUserName().equals(PasswordUtils.ADMIN_NAME)) {
                    passwordIsAllowed.setSelected(true);
                }
            }
        });
    }

    JPanel getListUsersPanel() {
        return listUsersPanel;
    }

    private void setContext(UserContext context) {
        passwordIsAllowed.setSelected(context.isPasswordLimited());
        userIsBlocked.setSelected(context.isBlocked());
        passwordLengthLabel.setText(String.format(" : %d", context.getMinimumPasswordLength()));
        if (context.getUserName().equals(PasswordUtils.ADMIN_NAME)) {
            userIsBlocked.setEnabled(false);
            passwordIsAllowed.setEnabled(false);
        } else {
            userIsBlocked.setEnabled(true);
            passwordIsAllowed.setEnabled(true);

        }
    }

    private void saveContext(UserContext context, int selectedIndex) {
        UserContext userContext = new UserContext(context.getUserName(),
                context.getPassword(),
                userIsBlocked.isSelected(),
                passwordIsAllowed.isSelected(),
                context.getMinimumPasswordLength());
        PasswordUtils.PASSWORDS.put(context.getUserName(), userContext);
        contextList.get(selectedIndex).setBlocked(userIsBlocked.isSelected());
        contextList.get(selectedIndex).setPasswordAllowed(passwordIsAllowed.isSelected());
    }

    public void setUp() {
        contextList.forEach(listModel::addElement);
        usersList.setModel(listModel);
    }
}
