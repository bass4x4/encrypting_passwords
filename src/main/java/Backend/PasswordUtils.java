package Backend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class PasswordUtils {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtils.class);

    public static final String PATH = System.getProperty("user.dir");

    public static final String EXTRA_PASS_PART = "RC4Ankushev";


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

    public static String cesarCipher(String rawText) {
        char[] result = new char[rawText.length()];
        for (int i = 0; i < rawText.length(); i++) {
            result[i] = (char) ((int) rawText.charAt(i) + 4);
        }
        return new String(result);
    }

    public static String cesarDecipher(String result) {
        char[] rawText = new char[result.length()];
        for (int i = 0; i < result.length(); i++) {
            rawText[i] = (char) ((int) result.charAt(i) - 4);
        }
        return new String(rawText);
    }
}