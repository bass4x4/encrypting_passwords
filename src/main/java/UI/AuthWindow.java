package UI;


import Backend.PasswordUtils;
import com.google.common.base.Joiner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AuthWindow {
    public JPanel authPanel;
    public JButton authButton;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JPanel fieldsPanel;

    public static Integer numberOfIncorrectInputs = 0;

    public AuthWindow() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuOption = new JMenu("About");
        JMenuItem open = new JMenuItem(new AboutAction());
        menuOption.add(open);
        menuBar.add(menuOption);
        authPanel.add(menuBar, BorderLayout.NORTH);

        authButton.addActionListener(action -> {
            String passwordString = new String(passwordField.getPassword());
            if (passwordString.contains(" ")) {
                JOptionPane.showMessageDialog(null, "Space symbol is not allowed in password!");
            } else {
                String userName = userNameField.getText();
                if (PasswordUtils.PASSWORDS.containsKey(userName)) {
                    if (inputPasswordIsCorrectOrSetNew(passwordString, userName)) {
                        numberOfIncorrectInputs = 0;
                        PasswordUtils.currentUser = PasswordUtils.PASSWORDS.get(userName);

                        if (PasswordUtils.currentUser.isBlocked()) {
                            JOptionPane.showMessageDialog(null, "Your account is blocked!");
                        } else if (userName.equals(PasswordUtils.ADMIN_NAME)) {
                            final JDialog adminOptionsWindow = new JDialog(new JFrame("Admin options"), true);

                            adminOptionsWindow.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e)
                                {
                                    passwordField.setText("");
                                    e.getWindow().dispose();
                                }
                            });

                            adminOptionsWindow.setContentPane(new AdminOptionsWindow().getAdminOptionsPanel());
                            adminOptionsWindow.pack();
                            adminOptionsWindow.setSize(300, 300);
                            adminOptionsWindow.setMaximumSize(new Dimension(200, 200));

                            adminOptionsWindow.setVisible(true);
                        } else {
                            final JDialog userOptionsWindow = new JDialog(new JFrame("User options"), true);

                            userOptionsWindow.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e)
                                {
                                    passwordField.setText("");
                                    e.getWindow().dispose();
                                }
                            });

                            userOptionsWindow.setContentPane(new UserOptionsWindow().getUserOptionsPanel());
                            userOptionsWindow.pack();
                            userOptionsWindow.setSize(200, 200);
                            userOptionsWindow.setMaximumSize(new Dimension(200, 200));

                            userOptionsWindow.setVisible(true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please check your password!");
                        if (++numberOfIncorrectInputs == 3) {
                            System.exit(1);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, String.format("No user with name %s", userName));
                }
            }
        });
    }

    private static class AboutAction extends AbstractAction {
        AboutAction() {
            putValue(NAME, "Show info");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JOptionPane.showMessageDialog(null, "Лабораторную выполнил Анкушев А.Д.\nГруппа А-05-16\nВариант №1\n\n" +
                    "Условие №1:\nДлина не меньше минимальной длины, устанавливаемой\nадминистратором и сохраняемой в учетной записи пользователя\n" +
                    "Условие №3:\nШифрование перестановкой\n(ключ задается именем учетной записи пользователя).\n" +
                    "Условие №4:\nТип симметричного шифрования : Блочный\n" +
                    "Используемый режим шифрования : Электронная кодовая книга\nДобавление к ключу случайного значения : да\nИспользуемый алгоритм хеширования" +
                    " : MD2\nМЭИ, 2019.");
        }
    }

    private boolean inputPasswordIsCorrectOrSetNew(String passwordString, String userName) {
        String encryptedPassword = PasswordUtils.PASSWORDS.get(userName).getPassword();
        if (!userName.equals(PasswordUtils.ADMIN_NAME) && encryptedPassword.isEmpty()) {
            PasswordUtils.setUsersPassword(userName);
            return true;
        } else {
            String newEncryptedPassword = PasswordUtils.getEncryptedPassword(userName, passwordString);
            return encryptedPassword.equals(newEncryptedPassword);
        }
    }
}
