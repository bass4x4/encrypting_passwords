package Backend;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class PasswordUtils {
    public final static HashMap<String, UserContext> PASSWORDS = new HashMap<>();

    private final static String PASSWORDS_FILE = "\\PASSWORDS.txt";

    public final static String ADMIN_NAME = "admin";

    public static UserContext currentUser = null;

    private static String path = System.getProperty("user.dir") + PASSWORDS_FILE;

    static void initPasswords() {
        if (initPasswordsFromFile()) {
            UserContext adminContext = new UserContext(ADMIN_NAME, "", false, true, 8);
            PASSWORDS.put(ADMIN_NAME, adminContext);
            savePasswordsToFile();
            setAdminsNewPassword(true);
        }
    }

    private static boolean initPasswordsFromFile() {
        try {
            Optional<String> first = Files.lines(Paths.get(path)).findFirst();
            if (first.isPresent()) {
                if (first.get().equals("admin,,0,1,8")) {
                    return true;
                } else {
                    parsePasswordsFromFile(Files.lines(Paths.get(path)));
                    return false;
                }
            }
        } catch(IOException io) {
            JOptionPane.showMessageDialog(null, "Error reading file with passwords!");
        }
        return true;
    }

    private static void parsePasswordsFromFile(Stream<String> lines) {
        lines.forEach(PasswordUtils::parseContextFromLine);
    }

    private static void parseContextFromLine(String contextString) {
        String[] split = contextString.split(",");
        UserContext userContext = new UserContext(split[0], split[1], split[2].equals("1"), split[3].equals("1"), Integer.parseInt(split[4]));
        PASSWORDS.put(split[0], userContext);
    }

    public static void setAdminsNewPassword(boolean firstLogin) {
        if (firstLogin) {
            setUsersPassword(ADMIN_NAME);
        } else {
            setUsersNewPassword(ADMIN_NAME);
        }

    }

    public static void setUsersNewPassword(String userName) {
        String oldPassword = "";
        while (isBadPassword(oldPassword)) {
            oldPassword = showInputDialog("Confirm old password");
            if (oldPassword == null) {
                return;
            }
        }

        String decryptedPassword = PasswordUtils.getDecryptedPassword(userName);
        if (!oldPassword.equals(decryptedPassword)) {
            setUsersNewPassword(userName);
        } else {
            setUsersPassword(userName);
        }
    }

    public static void setUsersPassword(String userName) {
        String newPassword = getNewConfirmedPassword(userName);
        if (!newPassword.isEmpty()) {
            String encryptedPassword = PasswordUtils.getEncryptedPassword(userName, newPassword);
            PASSWORDS.get(userName).setPassword(encryptedPassword);
        } else {
            setUsersPassword(userName);
        }
    }

    private static String getNewConfirmedPassword(String userName) {
        String newPassword = "";
        while (isBadPassword(newPassword) || !passwordFitsRules(userName, newPassword)) {
            newPassword = showInputDialog("Choose password");
            if (newPassword == null) {
                return "";
            }
        }

        String confirmedPassword = "";
        while (isBadPassword(confirmedPassword) || !confirmedPassword.equals(newPassword)) {
            confirmedPassword = showInputDialog("Confirm password");
            if (confirmedPassword == null) {
                return "";
            }
        }

        return newPassword;
    }

    private static boolean passwordFitsRules(String userName, String password) {
        if (PASSWORDS.get(userName).isPasswordLimited()) {
            int minimumPasswordLength = PASSWORDS.get(userName).getMinimumPasswordLength();
            boolean passwordMatches = password.length() >= minimumPasswordLength;
            if (!passwordMatches) {
                JOptionPane.showMessageDialog(null, String.format("Minimum password length is %d!", minimumPasswordLength));
            }
            return passwordMatches;
        }
        return true;
    }

    static void savePasswordsToFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path, false);
            PASSWORDS.values().forEach(userContext -> {
                try {
                    fileOutputStream.write(getFormattedContext(userContext).getBytes());
                } catch (IOException e) {
                    System.out.println("Could't write context to file");
                }
            });
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    private static String getFormattedContext(UserContext userContext) {
        int isBlocked = userContext.isBlocked() ? 1 : 0;
        int isLimited = userContext.isPasswordLimited() ? 1 : 0;
        return String.format("%s,%s,%d,%d,%d\n", userContext.getUserName(), userContext.getPassword(), isBlocked, isLimited, userContext.getMinimumPasswordLength());
    }

    private static String showInputDialog(String message) {
        JPasswordField passwordField = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, passwordField, message, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        } else {
            return null;
        }
    }

    private static boolean isBadPassword(String password) {
        return password.isEmpty() || password.contains(" ") || password.contains(",");
    }

    private static int[] getKey(String userNameToCut, String password) {
        String userName;
        if (userNameToCut.length() > password.length()) {
            userName = userNameToCut.substring(0, password.length());
        } else {
            userName = userNameToCut;
        }
        HashSet<Character> uniqueCharacters = new HashSet<>();
        for (int i = 0; i < userName.toCharArray().length; i++) {
            uniqueCharacters.add(userName.charAt(i));
        }

        ArrayList<Character> sortedCharacters = new ArrayList<>(uniqueCharacters);
        Collections.sort(sortedCharacters);

        AtomicInteger counter = new AtomicInteger(0);
        int[] key = new int[userName.length()];

        sortedCharacters.forEach(character -> {
            for (int i = 0; i < userName.toCharArray().length; i++) {
                if (userName.charAt(i) == character) {
                    key[i] = counter.getAndIncrement();
                }
            }
        });

        return key;
    }

    public static String getEncryptedPassword(String userName, String password) {
        int[] key = getKey(userName, password);
        char[] encryptedPassword = new char[password.length()];
        if (password.length() == key.length) {
            for (int i = 0; i < key.length; i++) {
                encryptedPassword[i] = password.charAt(key[i]);
            }
            return new String(encryptedPassword);
        } else {
            if (password.length() % key.length == 0) {
                return getEncryptedByPartsPassword(password, key, encryptedPassword);
            } else {
                String passwordWithExtraSpaces = password;
                int numberOfSpacesToAdd = key.length - (password.length() % key.length);
                for (int i = 0; i < numberOfSpacesToAdd; i++) {
                    passwordWithExtraSpaces += " ";
                }
                char[] encryptedPasswordWithExtraSpaces = new char[passwordWithExtraSpaces.length()];
                return getEncryptedByPartsPassword(passwordWithExtraSpaces, key, encryptedPasswordWithExtraSpaces);
            }
        }
    }

    private static String getEncryptedByPartsPassword(String password, int[] key, char[] encryptedPassword) {
        int numberOfParts = password.length() / key.length;
        for (int numberOfPart = 0; numberOfPart < numberOfParts; numberOfPart++) {
            encryptPartOfPassword(password, encryptedPassword, key, numberOfPart);
        }
        return new String(encryptedPassword);
    }

    private static void encryptPartOfPassword(String password, char[] encryptedPassword, int[] key, int numberOfPart) {
        int startPositionToEncrypt = numberOfPart * key.length;
        for (int i = startPositionToEncrypt; i < startPositionToEncrypt + key.length; i++) {
            int positionToEncryptFromZero = key[i - startPositionToEncrypt];
            encryptedPassword[i] = password.charAt(startPositionToEncrypt + positionToEncryptFromZero);
        }
    }

    public static String getDecryptedPassword(String userName) {
        String encryptedPassword = PasswordUtils.PASSWORDS.get(userName).getPassword();
        int[] key = getKey(userName, encryptedPassword);
        char[] decryptedPassword = new char[encryptedPassword.length()];

        if (encryptedPassword.length() == key.length) {
            for (int i = 0; i < key.length; i++) {
                decryptedPassword[key[i]] = encryptedPassword.charAt(i);
            }
            return new String(decryptedPassword);
        } else {
            String decryptedByPartsPassword = getDecryptedByPartsPassword(encryptedPassword, key, decryptedPassword);
            return decryptedByPartsPassword.trim();
        }
    }

    private static String getDecryptedByPartsPassword(String encryptedPassword, int[] key, char[] decryptedPassword) {
        int numberOfParts = encryptedPassword.length() / key.length;
        for (int numberOfPart = 0; numberOfPart < numberOfParts; numberOfPart++) {
            decryptPartOfPassword(encryptedPassword, decryptedPassword, key, numberOfPart);
        }
        return new String(decryptedPassword);
    }

    private static void decryptPartOfPassword(String encryptedPassword, char[] decryptedPassword, int[] key, int numberOfPart) {
        int startPositionToDecrypt = numberOfPart * key.length;
        for (int i = startPositionToDecrypt; i < startPositionToDecrypt + key.length; i++) {
            int startPositionToDecryptFromZero = startPositionToDecrypt + key[i - startPositionToDecrypt];
            decryptedPassword[startPositionToDecryptFromZero] = encryptedPassword.charAt(i);
        }
    }
}