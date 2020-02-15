package Backend;


import Interface.PassphraseWindow;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Set;

public class PasswordUtils {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtils.class);

    public static final String ADMIN_NAME = "admin";

    public static final String PATH = System.getProperty("user.dir");

    private static final Set<Character> SPECIAL_SYMBOLS = Sets.newHashSet('!', '@', '#', '$', '%', '&', '*', '(', ')', '_', '+', '=', '/'
            , ',', '.', '?', ';', ':', '<', '>', '~', '`', '[', ']', '{', '}', '|');

    public static String PASSPHRASE = "";

    public static String FILE_TO_CIPHER_PATH = "";

    public static final String EXTRA_PASS_PART = "RC4Ankushev";

    public static void setNewConfirmedPassphrase() {
        String newPassphrase = showInputDialog("Выберите парольную фразу:");
        if (newPassphrase == null) {
            return;
        }
        while (!passphraseFitsRules(newPassphrase)) {
            newPassphrase = showInputDialog("Выберите парольную фразу:");
            if (newPassphrase == null) {
                return;
            }
        }

        String confirmedPassphrase = showInputDialog("Подтвердите парольную фразу:");
        if (confirmedPassphrase == null) {
            return;
        }
        while (!passphraseFitsRules(confirmedPassphrase)) {
            confirmedPassphrase = showInputDialog("Подтвердите парольную фразу:");
            if (confirmedPassphrase == null) {
                return;
            }
        }
        PASSPHRASE = confirmedPassphrase;
    }

    public static boolean passphraseFitsRules() {
        return passphraseFitsRules(PASSPHRASE);
    }

    public static boolean passphraseFitsRules(String passphrase) {
        if (passphrase.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Пожалуйста, задайте парольную фразу!");
            return false;
        }
        if (passphrase.length() > PassphraseWindow.maximumPassphraseLength || passphrase.length() < PassphraseWindow.minimumPassphraseLength) {
            JOptionPane.showMessageDialog(null, "Длина парольной фразы не удовлетворяет условиям!");
            return false;
        }

        if (PassphraseWindow.specialSymbolsAllowed) {
            if (!containsSpecialSymbols(passphrase)) {
                JOptionPane.showMessageDialog(null, "Парольная фраза должна содержать специальные символы!");
                return false;
            }
        }

        if (PassphraseWindow.numbersAllowed) {
            if (!passphrase.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(null, "Парольная фраза должна содержать цифры!");
                return false;
            }
        }

        if (PassphraseWindow.lowerCaseAllowed) {
            if (!passphrase.matches(".*[a-z].*")) {
                JOptionPane.showMessageDialog(null, "Парольная фраза должна содержать буквы нижнего регистра!");
                return false;
            }
        }

        if (PassphraseWindow.upperCaseAllowed) {
            if (!passphrase.matches(".*[A-Z].*")) {
                JOptionPane.showMessageDialog(null, "Парольная фраза должна содержать буквы верхнего регистра!");
                return false;
            }
        }
        return true;
    }

    private static boolean containsSpecialSymbols(String passphrase) {
        for (int i = 0; i < passphrase.length(); i++) {
            if (SPECIAL_SYMBOLS.contains(passphrase.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String showInputDialog(String message) {
        JPasswordField passwordField = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, passwordField, message, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        } else {
            JOptionPane.showMessageDialog(null, "Парольная фраза не выбрана!");
            return null;
        }
    }

    public static String showInputNameDialog(String message, String name) {
        JTextField passwordField = new JTextField(name);
        int okCxl = JOptionPane.showConfirmDialog(null, passwordField, message, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            return passwordField.getText();
        } else {
            JOptionPane.showMessageDialog(null, "Парольная фраза не выбрана!");
            return null;
        }
    }
}